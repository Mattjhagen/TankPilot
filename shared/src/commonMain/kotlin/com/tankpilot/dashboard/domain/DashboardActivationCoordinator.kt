package com.tankpilot.dashboard.domain

import com.tankpilot.telemetry.domain.TelemetryData
import com.tankpilot.trip.domain.TripSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

class DashboardActivationCoordinator(
    private val isAutoModeEnabled: Boolean = true
) {
    private val _dashboardMode = MutableStateFlow(DashboardMode.INACTIVE)
    val dashboardMode: StateFlow<DashboardMode> = _dashboardMode.asStateFlow()

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
        if (sessionState.isVisible) {
            _dashboardMode.value = DashboardMode.ACTIVE
            if (!sessionState.enteredAutomatically) {
                cooldownUntilMs = 0
            }
        }
    }

    fun onTelemetryUpdate(telemetry: TelemetryData, isConnected: Boolean) {
        val speedKmh = telemetry.speedKmh
        val now = Clock.System.now().toEpochMilliseconds()

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
            cooldownUntilMs = Clock.System.now().toEpochMilliseconds() + cooldownDurationMs
            speedAboveThresholdSinceMs = null
            speedBelowThresholdSinceMs = null
        }
    }
}
