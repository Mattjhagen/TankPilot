package com.tankpilot.fuel.domain

import com.tankpilot.core.MilesPerGallon
import kotlinx.coroutines.flow.StateFlow

interface VehicleEfficiencyProvider {
    val currentFactoryCityMpg: StateFlow<Double?>
    val currentFactoryHighwayMpg: StateFlow<Double?>
    val currentLearnedMpg: StateFlow<Double?>
    val currentTankCapacityGallons: StateFlow<Double?>
    val currentReserveFuelGallons: StateFlow<Double?>
    val currentLowFuelThresholdPercent: StateFlow<Double?>
}
