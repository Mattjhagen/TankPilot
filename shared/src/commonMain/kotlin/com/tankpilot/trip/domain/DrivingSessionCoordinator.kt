package com.tankpilot.trip.domain

import com.tankpilot.core.AppLogger
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

private const val TAG = "TankPilotDrive"

data class DrivingSessionState(
    val selectedSpeed: SelectedSpeed,
    val drivingPattern: DrivingPattern,
    val activeTripState: ActiveTripState,
    val tripId: String?,
    val distanceMeters: Double,
    val elapsedTimeSeconds: Long,
    val idleTimeSeconds: Long,
    val averageSpeedKmh: Double?,
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
    val routeRecorder: TripRouteRecorder,
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
        accumulatedDistanceMeters = metricsUseCase.accumulatedDistanceMeters,
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
        metricsUseCase.accumulatedDistanceMeters,
        metricsUseCase.elapsedTimeSeconds,
        metricsUseCase.idleTimeSeconds,
        metricsUseCase.averageSpeedKmh,
        metricsUseCase.maxSpeedKmh,
        activeFuelBurnUseCase.activeFuelBurn,
        instantMpgFlow
    ) { args ->
        DrivingSessionState(
            selectedSpeed = args[0] as SelectedSpeed,
            drivingPattern = args[1] as DrivingPattern,
            activeTripState = args[2] as ActiveTripState,
            tripId = args[3] as? String,
            distanceMeters = args[4] as Double,
            elapsedTimeSeconds = args[5] as Long,
            idleTimeSeconds = args[6] as Long,
            averageSpeedKmh = args[7] as? Double,
            maxSpeedKmh = args[8] as Double,
            activeFuelBurn = args[9] as Double,
            mpgEstimate = args[10] as MpgEstimate
        )
    }.stateIn(scope, SharingStarted.Eagerly, 
        DrivingSessionState(
            selectedSpeed = SelectedSpeed(null, SpeedSource.UNKNOWN, null, false),
            drivingPattern = DrivingPattern.UNKNOWN,
            activeTripState = ActiveTripState.IDLE,
            tripId = null,
            distanceMeters = 0.0,
            elapsedTimeSeconds = 0L,
            idleTimeSeconds = 0L,
            averageSpeedKmh = null,
            maxSpeedKmh = 0.0,
            activeFuelBurn = 0.0,
            mpgEstimate = MpgEstimate(null, MpgEstimateSource.UNKNOWN, 0L, 0.0)
        )
    )

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    val throttledRouteFlow: StateFlow<List<LocationSample>> = routeRecorder.route
        .sample(1000L)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        stateMachine.state.onEach { state ->
            if (state == ActiveTripState.ACTIVE && stateMachine.tripId.value != null) {
                routeRecorder.startRecording(stateMachine.tripId.value!!)
            } else if (state == ActiveTripState.COMPLETING) {
                scope.launch {
                    val trip = tripCompletionUseCase.completeAndPersist()
                    val (pendingPoints, startIndex) = routeRecorder.stopAndFinalize()
                    if (trip != null) {
                        tripRepository.saveTripAndFinalRoute(trip, pendingPoints, startIndex)
                        val vId = activeVehicleId.value
                        if (vId != null) {
                            activeSessionRepository.deleteSession(vId)
                            AppLogger.d(TAG, "Active session cleared: vehicleId=$vId")
                        }
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
                        AppLogger.d(TAG, "Active session found for vehicleId=$vId, restoring")
                        stateMachine.restoreSession(session.tripId, session.startTimestamp)
                        metricsUseCase.restoreSession(
                            distanceMeters = session.accumulatedDistance,
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
                        accumulatedDistance = state.distanceMeters,
                        elapsedTimeSeconds = state.elapsedTimeSeconds,
                        idleTimeSeconds = state.idleTimeSeconds,
                        activeFuelBurn = state.activeFuelBurn,
                        lastActivityTimestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    activeSessionRepository.saveSession(session)
                    AppLogger.d(TAG, "Active session saved: tripId=$tId, distance=${session.accumulatedDistance}mi, elapsed=${session.elapsedTimeSeconds}s")
                }
            }
        }
    }

    fun onRawLocationUpdate(sample: LocationSample, currentWallClockTime: kotlinx.datetime.Instant = Clock.System.now()) {
        locationPipeline.onRawLocationUpdate(sample, currentWallClockTime)

        val validated = locationPipeline.validatedLocation.value
        if (validated != null) {
            metricsUseCase.onLocationUpdate(validated, stateMachine.state.value == ActiveTripState.ACTIVE || stateMachine.state.value == ActiveTripState.STOP_CANDIDATE)

            if (stateMachine.state.value == ActiveTripState.ACTIVE || stateMachine.state.value == ActiveTripState.STOP_CANDIDATE) {
                scope.launch {
                    routeRecorder.onLocationSample(validated)
                }
            }

            // A GPS fix with no speed component (Location.hasSpeed() == false) means "unknown",
            // not "confirmed stationary" — feeding the classifier/state machine a fabricated
            // 0.0 here would incorrectly cancel an in-progress trip start. Distance/idle
            // tracking above is unaffected: ActiveTripMetricsUseCase already null-checks speed.
            val speed = validated.speedKmh
            if (speed != null) {
                classifier.onSpeedUpdate(speed, validated.roadContext, validated.timestamp.toEpochMilliseconds())
                stateMachine.onSpeedUpdate(speed, validated.timestamp.toEpochMilliseconds())
            }
        }
    }

    fun startTripManually() {
        stateMachine.startTripManually(Clock.System.now().toEpochMilliseconds())
    }

    fun startSession(): Boolean {
        if (stateMachine.state.value != ActiveTripState.IDLE) {
            return false
        }
        stateMachine.startTripManually(Clock.System.now().toEpochMilliseconds())
        return stateMachine.state.value != ActiveTripState.IDLE
    }

    fun endTripManually() {
        stateMachine.endTripManually()
    }
}
