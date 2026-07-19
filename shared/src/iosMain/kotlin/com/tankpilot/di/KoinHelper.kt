package com.tankpilot.di

import com.tankpilot.trip.domain.DrivingSessionCoordinator
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuel.domain.FuelModelUseCase
import com.tankpilot.core.FlowWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object KoinHelper : KoinComponent {
    val drivingSessionCoordinator: DrivingSessionCoordinator
        get() = try {
            getKoin().get<DrivingSessionCoordinator>()
        } catch (e: Exception) {
            println("KOIN FATAL ERROR: ${e.message}")
            e.printStackTrace()
            throw e
        }
        
    val fuelStateUseCase: FuelStateUseCase by inject()
    val fuelModelUseCase: FuelModelUseCase by inject()
    
    val driveAutoStartStateMachine: com.tankpilot.trip.domain.DriveAutoStartStateMachine by inject()
    
    fun getDrivingSessionStateFlow() = FlowWrapper(drivingSessionCoordinator.sessionState)
    fun getEstimatedFuelRemainingFlow() = FlowWrapper(fuelStateUseCase.estimatedFuelRemaining)
    fun getRouteFlow() = FlowWrapper(drivingSessionCoordinator.throttledRouteFlow)
    fun getActiveContextsFlow() = FlowWrapper(driveAutoStartStateMachine.activeContexts)
    
    fun getObdTelemetryFlow() = FlowWrapper(com.tankpilot.telemetry.data.ObdTelemetrySnapshotManager.snapshotFlow)
    
    fun enterObdContext() {
        kotlinx.coroutines.GlobalScope.launch {
            driveAutoStartStateMachine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.Obd2)
        }
    }
    
    fun exitObdContext() {
        kotlinx.coroutines.GlobalScope.launch {
            driveAutoStartStateMachine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.Obd2)
        }
    }
}

