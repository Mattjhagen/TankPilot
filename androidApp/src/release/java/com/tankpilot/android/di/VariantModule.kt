package com.tankpilot.android.di

import org.koin.dsl.module
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.fuelrescue.domain.FuelStationProvider
import com.tankpilot.telemetry.data.UnavailableTelemetryProvider
import com.tankpilot.telemetry.data.UnavailableAmbientTemperatureProvider
import com.tankpilot.location.data.UnavailableHeadingProvider
import com.tankpilot.android.fuelrescue.data.ReleaseDemoFuelStationProvider
import com.tankpilot.fuelrescue.data.NoOpFuelRescueScenarioOverrideProvider
import com.tankpilot.fuelrescue.domain.FuelRescueScenarioOverrideProvider
import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.ReleaseCarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.android.auto.model.ReleaseCarLocationSource

/**
 * Release provider bindings.
 *
 * Provider map:
 *   VehicleTelemetryProvider    → UnavailableTelemetryProvider
 *     Reports DISCONNECTED. All telemetry fields null. No fabricated zeros.
 *   TripSessionProvider is NOT bound here — it is bound once, in commonModule, as
 *     DrivingSessionTripProviderAdapter (the real GPS-driven trip session). Trip
 *     tracking must work identically in debug and release.
 *   HeadingProvider             → UnavailableHeadingProvider
 *     Emits null. Dashboard shows — for compass.
 *   AmbientTemperatureProvider  → UnavailableAmbientTemperatureProvider
 *     Emits null. Dashboard shows — for outside temperature.
 *   FuelStationProvider         → ReleaseDemoFuelStationProvider (TEMPORARY — Phase A)
 *     Phase A Google Play Internal Testing bring-up only: returns 3 fixed, clearly-
 *     labeled "Demo Fuel Stop A/B/C" fixtures (never real business names) near the
 *     caller's location, so Android Auto's POI root screen has something real to
 *     render before a real station data source (e.g. Google Places) is wired up.
 *     REVERT to NoOpFuelStationProvider, or swap in a real provider, once Phase A
 *     POI-visibility validation on the physical head unit is confirmed — do not ship
 *     this past internal testing. See ReleaseDemoFuelStationProvider's class doc.
 *   CarFuelPreviewProvider       → ReleaseCarFuelPreviewProvider
 *     Always returns null. Android Auto shows "Unavailable" instead of a fixture
 *     when no vehicle is configured.
 *   CarLocationSource            → ReleaseCarLocationSource
 *     Real LocationManager last-known fix, or null if unavailable. Never a fabricated
 *     coordinate.
 *   FuelRescueScenarioOverrideProvider → NoOpFuelRescueScenarioOverrideProvider
 *     Always returns null. Release never overrides real Fuel Rescue inputs with a
 *     DHU testing fixture.
 *
 * A missing OBD adapter must not break DI or prevent app startup.
 * No mock telemetry, no test-support code.
 */
val variantModule = module {
    single<VehicleTelemetryProvider> { UnavailableTelemetryProvider() }
    single<HeadingProvider> { UnavailableHeadingProvider() }
    single<AmbientTemperatureProvider> { UnavailableAmbientTemperatureProvider() }
    single<FuelStationProvider> { ReleaseDemoFuelStationProvider() }
    single<CarFuelPreviewProvider> { ReleaseCarFuelPreviewProvider() }
    single<CarLocationSource> { ReleaseCarLocationSource(get()) }
    single<FuelRescueScenarioOverrideProvider> { NoOpFuelRescueScenarioOverrideProvider() }
}

