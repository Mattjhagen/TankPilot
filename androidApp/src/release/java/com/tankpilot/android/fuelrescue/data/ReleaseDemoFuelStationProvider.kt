package com.tankpilot.android.fuelrescue.data

import com.tankpilot.core.CurrencyMicros
import com.tankpilot.core.FuelPricePerUnit
import com.tankpilot.core.FuelPriceUnit
import com.tankpilot.core.FuelType
import com.tankpilot.core.Money
import com.tankpilot.core.StationId
import com.tankpilot.core.StationProvider
import com.tankpilot.fuelrescue.domain.FuelStation
import com.tankpilot.fuelrescue.domain.FuelStationProvider
import com.tankpilot.fuelrescue.domain.PriceFreshness
import com.tankpilot.fuelrescue.domain.StationFuelPrice
import kotlinx.datetime.Clock

/**
 * TEMPORARY — Phase A Google Play Internal Testing bring-up only. Returns 3 fixed,
 * unmistakably-synthetic fuel stations (never real business names) so Android Auto's
 * root screen shows a genuine POI experience with no real map/station API wired up yet.
 * Coordinates are small offsets from the caller-supplied origin, so the fixtures always
 * render "nearby" wherever the test device currently is, rather than a fixed real-world
 * location. All 3 set FuelStation.isDemoData = true, which the Android Auto layer uses
 * to bypass production fuel-safety filtering (see CarStationRowMapper.
 * selectRootDisplayRecommendations/isRootCritical) and to disable the Navigate action
 * (see CarStationDetailTemplateMapper) — a synthetic location must never be handed off
 * to a real navigation app.
 *
 * REVERT to NoOpFuelStationProvider, or replace with a real provider (e.g. Google
 * Places), once a real station data source is wired up — do not ship this past internal
 * testing. See VariantModule.kt's binding for this class.
 */
class ReleaseDemoFuelStationProvider : FuelStationProvider {

    override suspend fun getNearbyStations(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double,
        fuelType: FuelType
    ): List<FuelStation> {
        val now = Clock.System.now().toEpochMilliseconds()
        return listOf(
            demoStation(
                idSuffix = "a",
                name = "Demo Fuel Stop A",
                latitude = latitude + 0.01,
                longitude = longitude + 0.01,
                distanceMiles = 0.8,
                priceMicros = 2_790_000L,
                now = now
            ),
            demoStation(
                idSuffix = "b",
                name = "Demo Fuel Stop B",
                latitude = latitude - 0.015,
                longitude = longitude + 0.02,
                distanceMiles = 1.4,
                priceMicros = 3_150_000L,
                now = now
            ),
            demoStation(
                idSuffix = "c",
                name = "Demo Fuel Stop C",
                latitude = latitude + 0.03,
                longitude = longitude - 0.01,
                distanceMiles = 2.2,
                priceMicros = 3_250_000L,
                now = now
            )
        )
    }

    private fun demoStation(
        idSuffix: String,
        name: String,
        latitude: Double,
        longitude: Double,
        distanceMiles: Double,
        priceMicros: Long,
        now: Long
    ): FuelStation = FuelStation(
        id = StationId(StationProvider.UNKNOWN, "demo_stop_$idSuffix"),
        name = name,
        brand = null,
        latitude = latitude,
        longitude = longitude,
        address = "Demo fixture — not a real station",
        distanceMiles = distanceMiles,
        routeDistanceMiles = distanceMiles * 1.25 + 0.3,
        estimatedDriveMinutes = (distanceMiles * 1.25 + 0.3) / 35.0 * 60.0,
        isOpen = true,
        navigationDestination = null,
        fuelPrices = listOf(
            StationFuelPrice(
                fuelType = FuelType.REGULAR,
                fuelGradeKey = "regular",
                displayFuelGrade = "Regular",
                price = FuelPricePerUnit(Money(CurrencyMicros(priceMicros), "USD"), FuelPriceUnit.PER_GALLON),
                updatedAt = now,
                freshness = PriceFreshness.RECENT,
                source = "Demo Fixture — not a real price"
            )
        ),
        lastFetchedAt = now,
        isDemoData = true
    )
}
