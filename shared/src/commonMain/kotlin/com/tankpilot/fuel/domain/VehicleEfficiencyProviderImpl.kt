package com.tankpilot.fuel.domain

import com.tankpilot.vehicle.domain.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class VehicleEfficiencyProviderImpl(
    private val vehicleRepository: VehicleRepository,
    private val scope: CoroutineScope
) : VehicleEfficiencyProvider {

    private val activeVehicle = vehicleRepository.getVehicles()
        .map { it.firstOrNull() }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val currentFactoryCityMpg: StateFlow<Double?> = activeVehicle
        .map { it?.factoryCityMpg }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val currentFactoryHighwayMpg: StateFlow<Double?> = activeVehicle
        .map { it?.factoryHwyMpg }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val currentLearnedMpg: StateFlow<Double?> = activeVehicle
        .map { it?.learnedMpg }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val currentTankCapacityGallons: StateFlow<Double?> = activeVehicle
        .map { it?.tankCapacity }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val currentReserveFuelGallons: StateFlow<Double?> = activeVehicle
        .map { it?.reserveFuelGallons }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val currentLowFuelThresholdPercent: StateFlow<Double?> = activeVehicle
        .map { it?.lowFuelThresholdPercent }
        .stateIn(scope, SharingStarted.Eagerly, null)
}
