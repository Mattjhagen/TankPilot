package com.tankpilot.android.ui.vehicletwin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Stub for future advanced rendering capabilities
@Composable
fun VehicleRenderer(
    state: VehicleState,
    modifier: Modifier = Modifier
) {
    VehicleTwinView(
        fuelPercentage = state.fuelPercentage,
        modifier = modifier
    )
}
