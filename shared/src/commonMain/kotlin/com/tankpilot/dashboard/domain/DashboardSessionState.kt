package com.tankpilot.dashboard.domain

import kotlinx.serialization.Serializable

@Serializable
data class DashboardSessionState(
    val tripId: String? = null,
    val isVisible: Boolean = false,
    val isTripActive: Boolean = false,
    val enteredAutomatically: Boolean = false,
    val startTimeEpochMs: Long = 0L,
    val lastActivityTimestamp: Long = 0L,
    val lastReliableTelemetryTimestamp: Long = 0L,
    val isFocusModeEnabled: Boolean = false,
    val theme: DashboardTheme = DashboardTheme.ADAPTIVE
)
