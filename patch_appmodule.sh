#!/bin/bash
cat << 'INNER_EOF' > /Users/matty/Tank_Pilot/TankPilot/androidApp/src/main/java/com/tankpilot/android/di/AppModule.kt
package com.tankpilot.android.di

import com.tankpilot.android.viewmodel.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

import com.tankpilot.android.viewmodel.DashboardViewModel
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import com.tankpilot.trip.domain.TripSessionProvider
import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.dashboard.domain.DashboardActivationCoordinator
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.android.managers.HapticManager

val appModule = module {
    single { HapticManager(get()) }
    
    single {
        com.tankpilot.android.managers.DrivingTrackingCoordinator(
            context = get(),
            drivingSessionCoordinator = get(),
            scope = get()
        )
    }
    
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { 
        DashboardViewModel(
            savedStateHandle = get(),
            telemetryProvider = get(),
            tripSessionProvider = get(),
            headingProvider = get(),
            ambientTemperatureProvider = get(),
            dashboardActivationCoordinator = get(),
            fuelStateUseCase = get(),
            hapticManager = get(),
            clock = get(),
            drivingSessionCoordinator = get(),
            drivingTrackingCoordinator = get()
        ) 
    }
}
INNER_EOF
chmod +x patch_appmodule.sh
./patch_appmodule.sh
