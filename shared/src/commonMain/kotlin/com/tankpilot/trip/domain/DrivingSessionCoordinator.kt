package com.tankpilot.trip.domain

import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.RoadContext
import com.tankpilot.location.domain.SelectedSpeed
import com.tankpilot.location.domain.SpeedSource
import com.tankpilot.fuel.domain.MpgEstimator
import com.tankpilot.fuel.domain.MpgEstimate
import com.tankpilot.fuel.domain.MpgEstimateSource
import com.tankpilot.fuel.domain.ActiveFuelBurnUseCase
import com.tankpilot.telemetry.domain.TelemetryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class DrivingSessionState(
    val selectedSpeed: SelectedSpeed,
    val drivingPattern: DrivingPattern,
    val activeTripState: ActiveTripState,
    val tripId: String?,
    val distanceMiles: Double,
    val elapsedTimeSeconds: Long,
    val idleTimeSeconds: Long,
    val averageSpeedMph: Double?,
    val maxSpeedKmh: Double,
    val activeFuelBurn: Double,
    val mpgEstimate: MpgEstimate
)

class DrivingSessionCoordinator(
    val locationPipeline: LocationPipeline,
    val stateMachine: ActiveTripStateMachine,
    val metricsUseCase: ActiveTripMetricsUseCase,
    val mpgEstimator: MpgEstimator,
    val speedSelectionUseCase: SpeedSelectionUseCase,
    private val tripRepository: TripRepository,
    private val activeSessionRepository: ActiveSessionRepository,
    private val activeVehicleId: StateFlow<String?>,
    private val scope: CoroutineScope
) {
    val classifier = DrivingPatternClassifier()

    // Real-time MPG estimate stream
    val instantMpgFlow: StateFlow<MpgEstimate> = speedSelectionUseCase.selectedSpeed
        .map { speed ->
            val pattern = classifier.drivingPattern.value
            mpgEstimator.estimateInstantMpg(
                speedKmh = speed.valueKmh,
                pattern = pattern,
                massAirFlowGps = null,
                engineLoadPercent = null
            )
        }.stateIn(scope, SharingStarted.Eagerly, MpgEstimate(null, MpgEstimateSource.UNKNOWN, 0L, 0.0))

    // Active fuel burn use case
    val activeFuelBurnUseCase = ActiveFuelBurnUseCase(
        distanceDrivenMiles = metricsUseCase.accumulatedDistanceMiles,
        idleTimeSeconds = metricsUseCase.idleTimeSeconds,
        mpgFlow = instantMpgFlow,
        displacementLiters = 2.0,
        cylinderCount = 4,
        scope = scope
    )

    // Complete and persist completed trips
    val tripCompletionUseCase = TripCompletionUseCase(
        tripRepository = tripRepository,
        metricsUseCase = metricsUseCase,
        stateMachine = stateMachine,
        fuelBurnUseCase = activeFuelBurnUseCase,
        patternClassifier = classifier,
        activeVehicleId = activeVehicleId
    )

    val sessionState: StateFlow<DrivingSessionState> = combine(
        speedSelectionUseCase.selectedSpeed,
        classifier.drivingPattern,
        stateMachine.state,
        stateMachine.tripId,
        metricsUseCase.accumulatedDistanceMiles,
        metricsUseCase.elapsedTimeSeconds,
        metricsUseCase.idleTimeSeconds,
        metricsUseCase.averageSpeedMph,
        metricsUseCase.maxSpeedKmh,
        activeFuelBurnUseCase.activeFuelBurn,
        instantMpgFlow
    ) { args ->
        DrivingSessionState(
            selectedSpeed = args[0] as SelectedSpeed,
            drivingPattern = args[1] as DrivingPattern,
            activeTripState = args[2] as ActiveTripState,
            tripId = args[3] as? String,
            distanceMiles = args[4] as Double,
            elapsedTimeSeconds = args[5] as Long,
            idleTimeSeconds = args[6] as Long,
            averageSpeedMph = args[7] as? Double,
            maxSpeedKmh = args[8] as Double,
            activeFuelBurn = args[9] as Double,
            mpgEstimate = args[10] as MpgEstimate
        )
    }.stateIn(scope, SharingStarted.Eagerly, DrivingSessionState(
        SelectedSpeed(null, SpeedSource.UNKNOWN, null, false),
        DrivingPattern.UNKNOWN,
        ActiveTripState.IDLE,
        null,
        0.0,
        0L,
        0L,
        null,
        0.0,
        0.0,
        MpgEstimate(null, MpgEstimateSource.UNKNOWN, 0L, 0.0)
    ))

    init {
        stateMachine.state.onEach { state ->
            if (state == ActiveTripState.COMPLETING) {
                scope.launch {
                    val trip = tripCompletionUseCase.completeAndPersist()
                    if (trip != null) {
                        val vId = activeVehicleId.value
                        if (vId != null) activeSessionRepository.deleteSession(vId)
                    }
                }
            }
        }.launchIn(scope)

        // Restore active session on startup if it exists
        scope.launch {
            activeVehicleId.collectLatest { vId ->
                if (vId != null && stateMachine.state.value == ActiveTripState.IDLE) {
                    val session = activeSessionRepository.getSession(vId)
                    if (session != null) {
                        stateMachine.restoreSession(session.tripId, session.startTimestamp)
                        metricsUseCase.restoreSession(
                            distanceMiles = session.accumulatedDistance,
                            elapsedSeconds = session.elapsedTimeSeconds,
                            idleSeconds = session.idleTimeSeconds,
                            maxSpeed = 0.0,
                            startTimestampEpochMs = session.startTimestamp
                        )
                    }
                }
            }
        }

        // Persist session periodically while active
        scope.launch {
            sessionState.sample(10000L).collectLatest { state ->
                val vId = activeVehicleId.value
                val tId = state.tripId
                val startMs = stateMachine.startTimestampMs
                if (vId != null && tId != null && startMs != null && 
                    (state.activeTripState == ActiveTripState.ACTIVE || state.activeTripState == ActiveTripState.STOP_CANDIDATE)) {
                    val session = ActiveSession(
                        tripId = tId,
                        vehicleId = vId,
                        startTimestamp = startMs,
                        accumulatedDistance = state.distanceMiles,
                        elapsedTimeSeconds = state.elapsedTimeSeconds,
                        idleTimeSeconds = state.idleTimeSeconds,
                        activeFuelBurn = state.activeFuelBurn,
                        lastActivityTimestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    activeSessionRepository.saveSession(session)
                }
            }
        }
    }

    fun onRawLocationUpdate(sample: LocationSample, currentWallClockTime: kotlinx.datetime.Instant = Clock.System.now()) {
        locationPipeline.onRawLocationUpdate(sample, currentWallClockTime)
        
        val validated = locationPipeline.validatedLocation.value
        if (validated != null) {
            val speed = validated.speedKmh ?: 0.0
            classifier.onSpeedUpdate(speed, validated.roadContext, validated.timestamp.toEpochMilliseconds())
            metricsUseCase.onLocationUpdate(validated, stateMachine.state.value == ActiveTripState.ACTIVE || stateMachine.state.value == ActiveTripState.STOP_CANDIDATE)
            stateMachine.onSpeedUpdate(speed, validated.timestamp.toEpochMilliseconds())
        }
    }

    fun startTripManually() {
        stateMachine.startTripManually(Clock.System.now().toEpochMilliseconds())
    }

    fun endTripManually() {
        stateMachine.endTripManually()
    }
}
