package com.tankpilot.android.di

import org.koin.dsl.module
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.fuelrescue.domain.FuelStationProvider
import com.tankpilot.testsupport.telemetry.data.MockTelemetryProvider
import com.tankpilot.testsupport.telemetry.data.MockAmbientTemperatureProvider
import com.tankpilot.testsupport.location.data.MockHeadingProvider
import com.tankpilot.testsupport.fuelrescue.data.MockFuelStationProvider
import com.tankpilot.testsupport.fuelrescue.data.DebugFuelRescueScenarioOverrideProvider
import com.tankpilot.fuelrescue.domain.FuelRescueScenarioOverrideProvider
import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.DebugCarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.android.auto.model.DebugCarLocationSource

// TripSessionProvider is bound once, in commonModule, as DrivingSessionTripProviderAdapter —
// the real GPS-driven trip session. Trip tracking must work identically in debug and release.
val variantModule = module {
    single<VehicleTelemetryProvider> { MockTelemetryProvider() }
    single<HeadingProvider> { MockHeadingProvider() }
    single<AmbientTemperatureProvider> { MockAmbientTemperatureProvider(isDeveloperMode = true) }
    single<FuelStationProvider> { MockFuelStationProvider() }
    single<CarFuelPreviewProvider> { DebugCarFuelPreviewProvider() }
    single<CarLocationSource> { DebugCarLocationSource() }
    single<FuelRescueScenarioOverrideProvider> { DebugFuelRescueScenarioOverrideProvider() }
}
