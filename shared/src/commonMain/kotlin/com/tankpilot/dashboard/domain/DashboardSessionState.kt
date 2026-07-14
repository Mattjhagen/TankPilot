package com.tankpilot.dashboard.domain

import kotlinx.serialization.Serializable

@Serializable
data class DashboardSessionState(
    val isVisible: Boolean = false,
    val enteredAutomatically: Boolean = false,
    val startTimeEpochMs: Long = 0L,
    val isFocusModeEnabled: Boolean = false,
    val theme: DashboardTheme = DashboardTheme.ADAPTIVE
)
