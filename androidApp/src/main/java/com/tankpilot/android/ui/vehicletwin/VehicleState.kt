package com.tankpilot.android.ui.vehicletwin

data class VehicleState(
    val fuelPercentage: Float,
    val isMoving: Boolean = false,
    val isHeadlightsOn: Boolean = false
)
