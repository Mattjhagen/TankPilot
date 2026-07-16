package com.tankpilot.di

import com.tankpilot.core.DatabaseDriverFactory
import com.tankpilot.core.AppClock
import com.tankpilot.core.SystemClock
import com.tankpilot.db.TankPilotDb
import com.tankpilot.vehicle.domain.VehicleRepository
import com.tankpilot.vehicle.data.SqlDelightVehicleRepository
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.trip.data.SqlDelightTripRepository
import com.tankpilot.trip.domain.DrivingClassifier
import com.tankpilot.fillup.domain.FillUpRepository
import com.tankpilot.fillup.data.SqlDelightFillUpRepository
import com.tankpilot.fuelrescue.domain.FuelStationProvider
import com.tankpilot.fuelrescue.domain.FuelStationRepository
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import com.tankpilot.fuelrescue.domain.GasPriceConfig
import com.tankpilot.fuelrescue.data.SqlDelightFuelStationRepository
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import com.tankpilot.trip.domain.TripSessionProvider
import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.dashboard.domain.DashboardActivationCoordinator
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuel.domain.LiveMpgTracker
import com.tankpilot.fuel.domain.VehicleEfficiencyProvider
import com.tankpilot.fuel.domain.VehicleEfficiencyProviderImpl
import com.tankpilot.fuel.domain.MpgEstimator
import com.tankpilot.fuel.domain.AlertEngine
import com.tankpilot.fuel.domain.CalibrationEngine
import com.tankpilot.fuel.domain.FuelModelUseCase
import com.tankpilot.trip.domain.DrivingSessionCoordinator
import com.tankpilot.trip.domain.LocationPipeline
import com.tankpilot.trip.domain.ActiveTripStateMachine
import com.tankpilot.trip.domain.ActiveTripMetricsUseCase
import com.tankpilot.trip.domain.SpeedSelectionUseCase
import com.tankpilot.trip.domain.DrivingSessionTripProviderAdapter
import com.tankpilot.trip.domain.ActiveSessionRepository
import com.tankpilot.trip.data.SqlDelightActiveSessionRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
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

    single<FuelStationRepository> {
        SqlDelightFuelStationRepository(db = get(), provider = get(), dispatcher = get())
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

    single<FuelRescueUseCase> {
        FuelRescueUseCase(
            fuelStationRepository = get(),
            fuelStateUseCase = get(),
            scenarioOverrideProvider = get(),
            scope = get()
        )
    }

    single<ActiveSessionRepository> {
        SqlDelightActiveSessionRepository(db = get(), dispatcher = get())
    }

    single<VehicleEfficiencyProvider> {
        VehicleEfficiencyProviderImpl(vehicleRepository = get(), scope = get())
    }

    single<MpgEstimator> {
        MpgEstimator(efficiencyProvider = get())
    }

    single<LocationPipeline> {
        LocationPipeline(scope = get())
    }

    single<ActiveTripStateMachine> {
        ActiveTripStateMachine()
    }

    single<ActiveTripMetricsUseCase> {
        ActiveTripMetricsUseCase()
    }

    single<SpeedSelectionUseCase> {
        val telemetryProvider = get<VehicleTelemetryProvider>()
        val pipeline = get<LocationPipeline>()
        val gpsSpeedFlow = pipeline.validatedLocation
            .map { it?.speedKmh }
            .stateIn(get(), SharingStarted.Eagerly, null)
        SpeedSelectionUseCase(
            telemetryFlow = telemetryProvider.telemetryFlow,
            gpsSpeedFlow = gpsSpeedFlow,
            clock = get(),
            scope = get()
        )
    }

    single<DrivingSessionCoordinator> {
        val fuelStateUseCase = get<FuelStateUseCase>()
        val activeVehicleId = fuelStateUseCase.currentVehicle
            .map { it?.id }
            .stateIn(get(), SharingStarted.Eagerly, null)
        DrivingSessionCoordinator(
            locationPipeline = get(),
            stateMachine = get(),
            metricsUseCase = get(),
            mpgEstimator = get(),
            speedSelectionUseCase = get(),
            tripRepository = get(),
            activeVehicleId = activeVehicleId,
            scope = get()
        )
    }

    single<TripSessionProvider> {
        DrivingSessionTripProviderAdapter(coordinator = get(), scope = get())
    }

    single<AlertEngine> { AlertEngine() }

    single<CalibrationEngine> { CalibrationEngine(vehicleRepository = get(), tripRepository = get()) }

    single<FuelModelUseCase> {
        val coordinator = get<DrivingSessionCoordinator>()
        val fuelStateUseCase = get<FuelStateUseCase>()
        val scope = get<kotlinx.coroutines.CoroutineScope>()
        FuelModelUseCase(
            persistedFuelRemaining = fuelStateUseCase.estimatedFuelRemaining.map { it.value }.stateIn(scope, SharingStarted.Eagerly, 0.0),
            activeFuelBurn = coordinator.activeFuelBurnUseCase.activeFuelBurn,
            efficiencyProvider = get(),
            confidencePercent = fuelStateUseCase.confidencePercent,
            alertEngine = get(),
            scope = scope
        )
    }

    single<GasPriceConfig> { GasPriceConfig() }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule, commonModule)
    }

fun initKoin() = initKoin {}
