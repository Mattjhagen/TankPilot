package com.tankpilot.di

import com.tankpilot.trip.domain.DrivingSessionCoordinator
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuel.domain.FuelModelUseCase
import com.tankpilot.core.FlowWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object KoinHelper : KoinComponent {
    val drivingSessionCoordinator: DrivingSessionCoordinator by inject()
    val fuelStateUseCase: FuelStateUseCase by inject()
    val fuelModelUseCase: FuelModelUseCase by inject()
    
    fun getDrivingSessionStateFlow() = FlowWrapper(drivingSessionCoordinator.sessionState)
    fun getEstimatedFuelRemainingFlow() = FlowWrapper(fuelStateUseCase.estimatedFuelRemaining)
}

