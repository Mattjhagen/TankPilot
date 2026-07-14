package com.tankpilot.trip.data

import com.tankpilot.core.Miles
import com.tankpilot.trip.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Production non-OBD trip session provider.
 *
 * Tracks elapsed time via wall-clock polling and accepts manual distance increments.
 * In the future this can be extended to integrate GPS distance accumulation.
 *
 * This is the correct release-build provider until a GPS distance source is wired.
 * It does not produce fabricated OBD speed or distance.
 */
class ManualTripSessionProvider : TripSessionProvider {

    private val _sessionState = MutableStateFlow(TripSessionState.INACTIVE)
    override val sessionState: StateFlow<TripSessionState> = _sessionState

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    override val elapsedTime: StateFlow<Duration> = _elapsedTime

    private val _distanceDriven = MutableStateFlow(Miles(0.0))
    override val distanceDriven: StateFlow<Miles> = _distanceDriven

    private val _averageSpeed = MutableStateFlow<Double?>(null)
    override val averageSpeed: StateFlow<Double?> = _averageSpeed

    private val _startedAt = MutableStateFlow<Instant?>(null)
    override val startedAt: StateFlow<Instant?> = _startedAt

    private var tripStartMs: Long = 0L

    override suspend fun startTrip(source: TripStartSource) {
        if (_sessionState.value != TripSessionState.INACTIVE &&
            _sessionState.value != TripSessionState.ENDED
        ) return

        tripStartMs = Clock.System.now().toEpochMilliseconds()
        _startedAt.value = Instant.fromEpochMilliseconds(tripStartMs)
        _elapsedTime.value = Duration.ZERO
        _distanceDriven.value = Miles(0.0)
        _averageSpeed.value = null
        _sessionState.value = TripSessionState.ACTIVE
    }

    override suspend fun endTrip(reason: TripEndReason) {
        if (_sessionState.value == TripSessionState.ACTIVE ||
            _sessionState.value == TripSessionState.PAUSED
        ) {
            updateElapsed()
            _sessionState.value = TripSessionState.ENDED
        }
    }

    /** Called externally (e.g. by GPS integration) to increment driven distance. */
    fun addDistance(miles: Miles) {
        if (_sessionState.value == TripSessionState.ACTIVE) {
            _distanceDriven.value = Miles(_distanceDriven.value.value + miles.value)
            updateAverageSpeed()
        }
    }

    /** Should be called periodically (e.g. every second) to update elapsed time. */
    fun tick() {
        if (_sessionState.value == TripSessionState.ACTIVE) {
            updateElapsed()
        }
    }

    private fun updateElapsed() {
        val now = Clock.System.now().toEpochMilliseconds()
        val elapsedSeconds = (now - tripStartMs) / 1000L
        _elapsedTime.value = elapsedSeconds.seconds
    }

    private fun updateAverageSpeed() {
        val elapsedHours = _elapsedTime.value.inWholeSeconds / 3600.0
        if (elapsedHours > 0) {
            _averageSpeed.value = _distanceDriven.value.value / elapsedHours
        }
    }
}
