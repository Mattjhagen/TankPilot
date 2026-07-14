package com.tankpilot.di

import com.tankpilot.core.DatabaseDriverFactory
import com.tankpilot.core.AppClock
import com.tankpilot.core.SystemClock
import com.tankpilot.db.TankPilotDb
import com.tankpilot.vehicle.domain.VehicleRepository
import com.tankpilot.vehicle.data.SqlDelightVehicleRepository
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.trip.data.SqlDelightTripRepository
import com.tankpilot.fillup.domain.FillUpRepository
import com.tankpilot.fillup.data.SqlDelightFillUpRepository
import com.tankpilot.fuelrescue.domain.FuelStationProvider
import com.tankpilot.fuelrescue.domain.FuelStationRepository
import com.tankpilot.fuelrescue.data.MockFuelStationProvider
import com.tankpilot.fuelrescue.data.SqlDelightFuelStationRepository
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import com.tankpilot.telemetry.data.MockTelemetryProvider
import com.tankpilot.trip.domain.TripSessionProvider
import com.tankpilot.trip.data.MockTripSessionProvider
import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.location.data.MockHeadingProvider
import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.telemetry.data.MockAmbientTemperatureProvider
import com.tankpilot.dashboard.domain.DashboardActivationCoordinator
import com.tankpilot.fuel.domain.FuelStateUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.IO
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect val platformModule: Module

val commonModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    single<CoroutineScope> { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
    single<AppClock> { SystemClock() }
    
    single<TankPilotDb> {
        val driverFactory: DatabaseDriverFactory = get()
        TankPilotDb(driverFactory.createDriver())
    }

    single<VehicleRepository> {
        SqlDelightVehicleRepository(db = get(), dispatcher = get())
    }

    single<TripRepository> {
        SqlDelightTripRepository(db = get(), dispatcher = get())
    }

    single<FillUpRepository> {
        SqlDelightFillUpRepository(db = get(), dispatcher = get())
    }

    single<FuelStationProvider> {
        MockFuelStationProvider()
    }

    single<FuelStationRepository> {
        SqlDelightFuelStationRepository(db = get(), provider = get(), dispatcher = get())
    }

    single<VehicleTelemetryProvider> {
        MockTelemetryProvider()
    }

    single<TripSessionProvider> {
        MockTripSessionProvider()
    }

    single<HeadingProvider> {
        MockHeadingProvider()
    }

    single<AmbientTemperatureProvider> {
        MockAmbientTemperatureProvider(isDeveloperMode = true)
    }

    single<DashboardActivationCoordinator> {
        DashboardActivationCoordinator(isAutoModeEnabled = true)
    }

    single<FuelStateUseCase> {
        FuelStateUseCase(
            vehicleRepository = get(),
            tripRepository = get(),
            fillUpRepository = get(),
            scope = get()
        )
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule, commonModule)
    }

fun initKoin() = initKoin {}
