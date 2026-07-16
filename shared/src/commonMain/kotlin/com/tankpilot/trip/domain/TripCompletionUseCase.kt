package com.tankpilot.trip.domain

import com.tankpilot.fuel.domain.ActiveFuelBurnUseCase
import com.tankpilot.fuel.domain.MpgEstimator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock

class TripCompletionUseCase(
    private val tripRepository: TripRepository,
    private val metricsUseCase: ActiveTripMetricsUseCase,
    private val stateMachine: ActiveTripStateMachine,
    private val fuelBurnUseCase: ActiveFuelBurnUseCase,
    private val patternClassifier: DrivingPatternClassifier,
    private val activeVehicleId: StateFlow<String?>
) {
    suspend fun completeAndPersist(): Trip? {
        val vId = activeVehicleId.value ?: return null
        val tId = stateMachine.tripId.value ?: return null

        val dist = metricsUseCase.accumulatedDistanceMiles.value
        val elapsed = metricsUseCase.elapsedTimeSeconds.value
        val idle = metricsUseCase.idleTimeSeconds.value
        val avgSpeed = metricsUseCase.averageSpeedMph.value ?: 0.0
        val maxSpeed = metricsUseCase.maxSpeedKmh.value
        val burn = fuelBurnUseCase.activeFuelBurn.value
        val pattern = patternClassifier.drivingPattern.value

        // Ignore trivial noise trips
        if (dist <= 0.01 && elapsed <= 5) {
            stateMachine.resetToIdle()
            metricsUseCase.reset()
            return null
        }

        val completedTrip = Trip(
            id = tId,
            vehicleId = vId,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            distance = dist,
            duration = elapsed,
            idleTime = idle,
            averageSpeed = avgSpeed,
            drivingType = when (pattern) {
                DrivingPattern.SUSTAINED_HIGH_SPEED -> DrivingType.HIGHWAY
                DrivingPattern.STOP_AND_GO, DrivingPattern.URBAN_FLOW -> DrivingType.CITY
                else -> DrivingType.MIXED
            },
            fuelBurned = burn,
            maxSpeedKmh = maxSpeed,
            highwayPercentage = if (pattern == DrivingPattern.SUSTAINED_HIGH_SPEED) 1.0 else 0.0
        )

        // Idempotent write: SqlDelight repository will enforce INSERT OR IGNORE
        tripRepository.saveTrip(completedTrip)

        stateMachine.resetToIdle()
        metricsUseCase.reset()

        return completedTrip
    }
}
