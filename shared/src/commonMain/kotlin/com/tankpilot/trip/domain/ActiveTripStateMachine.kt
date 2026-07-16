package com.tankpilot.trip.domain

import com.tankpilot.core.AppLogger
import com.tankpilot.core.randomUuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant

private const val TAG = "TankPilotDrive"

enum class ActiveTripState {
    IDLE,
    START_CANDIDATE,
    ACTIVE,
    STOP_CANDIDATE,
    COMPLETING
}

class ActiveTripStateMachine(
    private val startSpeedKmh: Double = 12.87, // ~8 mph
    private val startDurationMs: Long = 5000L, // 5s
    private val stopSpeedKmh: Double = 3.21,   // ~2 mph
    private val stopDurationMs: Long = 90000L   // 90s (1.5 mins)
) {
    private val _state = MutableStateFlow(ActiveTripState.IDLE)
    val state: StateFlow<ActiveTripState> = _state.asStateFlow()

    private val _tripId = MutableStateFlow<String?>(null)
    val tripId: StateFlow<String?> = _tripId.asStateFlow()

    private var candidateStartMs: Long? = null
    private var candidateStopMs: Long? = null
    private var startTimeMs: Long? = null
    val startTimestampMs: Long? get() = startTimeMs

    fun onSpeedUpdate(speedKmh: Double, timestampMs: Long) {
        val currentState = _state.value

        when (currentState) {
            ActiveTripState.IDLE -> {
                if (speedKmh >= startSpeedKmh) {
                    AppLogger.d(TAG, "Trip state: IDLE -> START_CANDIDATE")
                    _state.value = ActiveTripState.START_CANDIDATE
                    candidateStartMs = timestampMs
                }
            }
            ActiveTripState.START_CANDIDATE -> {
                if (speedKmh >= startSpeedKmh) {
                    val firstSeen = candidateStartMs ?: timestampMs
                    if (timestampMs - firstSeen >= startDurationMs) {
                        startNewTrip(timestampMs)
                    }
                } else {
                    AppLogger.d(TAG, "Trip state: START_CANDIDATE -> IDLE (speed dropped before start threshold)")
                    _state.value = ActiveTripState.IDLE
                    candidateStartMs = null
                }
            }
            ActiveTripState.ACTIVE -> {
                if (speedKmh < stopSpeedKmh) {
                    AppLogger.d(TAG, "Trip state: ACTIVE -> STOP_CANDIDATE")
                    _state.value = ActiveTripState.STOP_CANDIDATE
                    candidateStopMs = timestampMs
                }
            }
            ActiveTripState.STOP_CANDIDATE -> {
                if (speedKmh >= stopSpeedKmh) {
                    AppLogger.d(TAG, "Trip state: STOP_CANDIDATE -> ACTIVE (resumed motion)")
                    _state.value = ActiveTripState.ACTIVE
                    candidateStopMs = null
                } else {
                    val firstSeen = candidateStopMs ?: timestampMs
                    if (timestampMs - firstSeen >= stopDurationMs) {
                        AppLogger.d(TAG, "Trip state: STOP_CANDIDATE -> COMPLETING (sustained stop)")
                        _state.value = ActiveTripState.COMPLETING
                        candidateStopMs = null
                    }
                }
            }
            ActiveTripState.COMPLETING -> {
                // Wait for external coordinator to persist the trip and call resetToIdle()
            }
        }
    }

    fun restoreSession(tripId: String, startTimestampMs: Long) {
        AppLogger.d(TAG, "Active session restored: tripId=$tripId -> ACTIVE")
        _tripId.value = tripId
        startTimeMs = startTimestampMs
        _state.value = ActiveTripState.ACTIVE
        candidateStartMs = null
        candidateStopMs = null
    }

    fun startTripManually(timestampMs: Long) {
        AppLogger.d(TAG, "Trip started manually")
        startNewTrip(timestampMs)
    }

    fun endTripManually() {
        if (_state.value != ActiveTripState.IDLE) {
            AppLogger.d(TAG, "Trip state: ${_state.value} -> COMPLETING (ended manually)")
            _state.value = ActiveTripState.COMPLETING
            candidateStartMs = null
            candidateStopMs = null
        }
    }

    fun resetToIdle() {
        _state.value = ActiveTripState.IDLE
        _tripId.value = null
        candidateStartMs = null
        candidateStopMs = null
        startTimeMs = null
    }

    private fun startNewTrip(timestampMs: Long) {
        val newId = randomUuid()
        AppLogger.d(TAG, "Trip state: ${_state.value} -> ACTIVE (new trip $newId started)")
        _tripId.value = newId
        startTimeMs = timestampMs
        _state.value = ActiveTripState.ACTIVE
        candidateStartMs = null
        candidateStopMs = null
    }
}
