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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect val platformModule: Module

val commonModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
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
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule, commonModule)
    }

fun initKoin() = initKoin {}
