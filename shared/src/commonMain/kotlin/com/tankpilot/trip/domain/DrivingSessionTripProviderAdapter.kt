package com.tankpilot.trip.domain

import com.tankpilot.core.Miles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class DrivingSessionTripProviderAdapter(
    private val coordinator: DrivingSessionCoordinator,
    private val scope: CoroutineScope
) : TripSessionProvider {

    override val sessionState: StateFlow<TripSessionState> = coordinator.sessionState
        .map { state ->
            when (state.activeTripState) {
                ActiveTripState.IDLE -> TripSessionState.INACTIVE
                ActiveTripState.START_CANDIDATE -> TripSessionState.INACTIVE
                ActiveTripState.ACTIVE -> TripSessionState.ACTIVE
                ActiveTripState.STOP_CANDIDATE -> TripSessionState.ACTIVE
                ActiveTripState.COMPLETING -> TripSessionState.ENDED
            }
        }.stateIn(scope, SharingStarted.Eagerly, TripSessionState.INACTIVE)

    override val elapsedTime: StateFlow<Duration> = coordinator.sessionState
        .map { it.elapsedTimeSeconds.seconds }
        .stateIn(scope, SharingStarted.Eagerly, Duration.ZERO)

    override val distanceDriven: StateFlow<Miles> = coordinator.sessionState
        .map { Miles(it.distanceMiles) }
        .stateIn(scope, SharingStarted.Eagerly, Miles(0.0))

    override val averageSpeed: StateFlow<Double?> = coordinator.sessionState
        .map { it.averageSpeedMph }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val startedAt: StateFlow<Instant?> = coordinator.stateMachine.tripId
        .map { id: String? ->
            if (id != null) {
                Clock.System.now()
            } else null
        }.stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun startTrip(source: TripStartSource) {
        coordinator.startTripManually()
    }

    override suspend fun endTrip(reason: TripEndReason) {
        coordinator.endTripManually()
    }
}
