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
    
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { DashboardViewModel(get(), get<VehicleTelemetryProvider>(), get<TripSessionProvider>(), get<HeadingProvider>(), get<AmbientTemperatureProvider>(), get<DashboardActivationCoordinator>(), get<FuelStateUseCase>(), get<HapticManager>(), get<com.tankpilot.core.AppClock>()) }
}
