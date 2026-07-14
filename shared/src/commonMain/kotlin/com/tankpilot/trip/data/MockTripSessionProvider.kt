package com.tankpilot.trip.data

import com.tankpilot.core.Miles
import com.tankpilot.core.MilesPerHour
import com.tankpilot.trip.domain.TripEndReason
import com.tankpilot.trip.domain.TripSessionProvider
import com.tankpilot.trip.domain.TripSessionState
import com.tankpilot.trip.domain.TripStartSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MockTripSessionProvider : TripSessionProvider {
    private val _sessionState = MutableStateFlow(TripSessionState.INACTIVE)
    override val sessionState: StateFlow<TripSessionState> = _sessionState.asStateFlow()

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    override val elapsedTime: StateFlow<Duration> = _elapsedTime.asStateFlow()

    private val _distanceDriven = MutableStateFlow(Miles(0.0))
    override val distanceDriven: StateFlow<Miles> = _distanceDriven.asStateFlow()

    private val _averageSpeed = MutableStateFlow<MilesPerHour?>(null)
    override val averageSpeed: StateFlow<MilesPerHour?> = _averageSpeed.asStateFlow()

    private val _startedAt = MutableStateFlow<Instant?>(null)
    override val startedAt: StateFlow<Instant?> = _startedAt.asStateFlow()

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var startTimeMs: Long = 0

    override suspend fun startTrip(source: TripStartSource) {
        if (_sessionState.value == TripSessionState.ACTIVE) return
        
        _sessionState.value = TripSessionState.ACTIVE
        _startedAt.value = Clock.System.now()
        startTimeMs = Clock.System.now().toEpochMilliseconds()

        job = scope.launch {
            while (isActive) {
                delay(1000)
                val elapsed = (Clock.System.now().toEpochMilliseconds() - startTimeMs) / 1000
                _elapsedTime.value = elapsed.seconds
                _distanceDriven.value = Miles(_distanceDriven.value.value + 0.01) // mock 36mph
                _averageSpeed.value = MilesPerHour(36.0)
            }
        }
    }

    override suspend fun endTrip(reason: TripEndReason) {
        _sessionState.value = TripSessionState.ENDED
        job?.cancel()
        job = null
    }
}
