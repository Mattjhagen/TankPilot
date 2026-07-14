package com.tankpilot.dashboard.domain

import com.tankpilot.telemetry.domain.TelemetryData
import com.tankpilot.trip.domain.TripSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.tankpilot.core.AppClock
import com.tankpilot.core.SystemClock

class DashboardActivationCoordinator(
    private val isAutoModeEnabled: Boolean = true,
    private val clock: AppClock = SystemClock()
) {
    private val _dashboardMode = MutableStateFlow(DashboardMode.INACTIVE)
    val dashboardMode: StateFlow<DashboardMode> = _dashboardMode.asStateFlow()

    /** Non-null only while in CONFIRMATION_REQUIRED state. Cleared on any resolution. */
    private val _pendingSessionState = MutableStateFlow<DashboardSessionState?>(null)
    val pendingSessionState: StateFlow<DashboardSessionState?> = _pendingSessionState.asStateFlow()

    private var cooldownUntilMs: Long = 0
    private var speedAboveThresholdSinceMs: Long? = null
    private var speedBelowThresholdSinceMs: Long? = null
    
    // Thresholds
    private val enterSpeedThresholdKmh = 12.87 // 8 mph
    private val enterDurationMs = 5000L // 5 seconds
    private val exitSpeedThresholdKmh = 3.21 // 2 mph
    private val exitDurationMs = 90000L // 90 seconds
    private val cooldownDurationMs = 600000L // 10 minutes

    fun restoreState(sessionState: DashboardSessionState) {
        if (!sessionState.isVisible || sessionState.lastActivityTimestamp <= 0L) {
            _dashboardMode.value = DashboardMode.INACTIVE
            _pendingSessionState.value = null
            return
        }

        val now = clock.now().toEpochMilliseconds()
        // Future timestamps are invalid — treat as stale
        if (sessionState.lastActivityTimestamp > now) {
            _dashboardMode.value = DashboardMode.INACTIVE
            _pendingSessionState.value = null
            return
        }

        val elapsedMs = now - sessionState.lastActivityTimestamp
        val thirtyMinsMs = 30 * 60 * 1000L
        val fourHoursMs = 4 * 60 * 60 * 1000L

        when {
            elapsedMs <= thirtyMinsMs -> {
                // Auto-restore: elapsed is within 30 minutes (inclusive)
                _dashboardMode.value = DashboardMode.ACTIVE
                _pendingSessionState.value = null
                if (!sessionState.enteredAutomatically) cooldownUntilMs = 0
            }
            elapsedMs <= fourHoursMs -> {
                // Stale: 30m < elapsed <= 4h — require user confirmation
                _dashboardMode.value = DashboardMode.CONFIRMATION_REQUIRED
                _pendingSessionState.value = sessionState
            }
            else -> {
                // Too stale: discard
                _dashboardMode.value = DashboardMode.INACTIVE
                _pendingSessionState.value = null
            }
        }
    }

    /**
     * Called when the user taps "Resume Drive" in the session resume dialog.
     * Transitions CONFIRMATION_REQUIRED → ACTIVE and clears pending state.
     */
    fun confirmRestore() {
        if (_dashboardMode.value == DashboardMode.CONFIRMATION_REQUIRED) {
            _pendingSessionState.value?.let { if (!it.enteredAutomatically) cooldownUntilMs = 0 }
            _dashboardMode.value = DashboardMode.ACTIVE
            _pendingSessionState.value = null
        }
    }

    /**
     * Called when the user taps "End Previous Trip" or "Dismiss" in the resume dialog.
     * Transitions CONFIRMATION_REQUIRED → INACTIVE without deleting trip data.
     * The caller is responsible for any repository trip-ending (e.g. marking trip ENDED).
     */
    fun dismissRestore() {
        if (_dashboardMode.value == DashboardMode.CONFIRMATION_REQUIRED) {
            _dashboardMode.value = DashboardMode.INACTIVE
            _pendingSessionState.value = null
        }
    }

    fun onTelemetryUpdate(telemetry: TelemetryData, isConnected: Boolean) {
        val speedKmh = telemetry.speedKmh
        val now = clock.now().toEpochMilliseconds()

        // Handle cooldown
        if (now < cooldownUntilMs) {
            if (_dashboardMode.value != DashboardMode.COOLDOWN) {
                _dashboardMode.value = DashboardMode.COOLDOWN
            }
            return // Block auto-entry
        } else if (_dashboardMode.value == DashboardMode.COOLDOWN) {
            _dashboardMode.value = DashboardMode.INACTIVE
        }

        if (speedKmh == null || !isConnected) {
            speedAboveThresholdSinceMs = null
            return
        }

        // Auto-Enter Logic
        if (_dashboardMode.value == DashboardMode.INACTIVE && isAutoModeEnabled) {
            if (speedKmh >= enterSpeedThresholdKmh) {
                if (speedAboveThresholdSinceMs == null) {
                    speedAboveThresholdSinceMs = now
                } else if (now - speedAboveThresholdSinceMs!! >= enterDurationMs) {
                    _dashboardMode.value = DashboardMode.ACTIVE
                    speedAboveThresholdSinceMs = null
                }
            } else {
                speedAboveThresholdSinceMs = null
            }
        }

        // Auto-Exit Logic
        if (_dashboardMode.value == DashboardMode.ACTIVE) {
            if (speedKmh < exitSpeedThresholdKmh) {
                if (speedBelowThresholdSinceMs == null) {
                    speedBelowThresholdSinceMs = now
                } else if (now - speedBelowThresholdSinceMs!! >= exitDurationMs) {
                    _dashboardMode.value = DashboardMode.INACTIVE
                    speedBelowThresholdSinceMs = null
                }
            } else {
                speedBelowThresholdSinceMs = null
            }
        }
    }

    fun onTripStateChange(state: TripSessionState) {
        if (state == TripSessionState.ENDED && _dashboardMode.value == DashboardMode.ACTIVE) {
            _dashboardMode.value = DashboardMode.INACTIVE
        }
    }

    fun manualEnter() {
        _dashboardMode.value = DashboardMode.ACTIVE
        speedAboveThresholdSinceMs = null
        speedBelowThresholdSinceMs = null
        cooldownUntilMs = 0
    }

    fun manualExit() {
        if (_dashboardMode.value == DashboardMode.ACTIVE) {
            _dashboardMode.value = DashboardMode.INACTIVE
            cooldownUntilMs = clock.now().toEpochMilliseconds() + cooldownDurationMs
            speedAboveThresholdSinceMs = null
            speedBelowThresholdSinceMs = null
        }
    }
}
