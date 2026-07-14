package com.tankpilot.testsupport.fuelrescue.data

import com.tankpilot.core.FuelPriceUnit
import com.tankpilot.core.FuelPricePerUnit
import com.tankpilot.core.CurrencyMicros
import com.tankpilot.core.Money
import com.tankpilot.core.FuelType
import com.tankpilot.core.StationId
import com.tankpilot.core.StationProvider
import com.tankpilot.fuelrescue.domain.*
import com.tankpilot.testsupport.MockStationScenario
import com.tankpilot.testsupport.TestFixtures
import kotlinx.datetime.Clock

/**
 * Honors TestFixtures.stationScenario so Android Auto DHU testing (and phone Test Lab
 * scenarios) can exercise Fuel Rescue's edge cases deterministically — see
 * phases/phase-03a-android-auto-foundation.md, Phase 3A.3.
 */
class MockFuelStationProvider : FuelStationProvider {
    override suspend fun getNearbyStations(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double,
        fuelType: FuelType
    ): List<FuelStation> {
        val now = Clock.System.now().toEpochMilliseconds()

        return when (TestFixtures.stationScenario.value) {
            MockStationScenario.OFFLINE ->
                throw RuntimeException("Simulated offline — no network available for station data")

            MockStationScenario.NORMAL ->
                normalStations(latitude, longitude, now)

            MockStationScenario.NO_SAFE_REACHABLE ->
                normalStations(latitude, longitude, now).map { station ->
                    // Far enough that no plausible fuel level puts it in safe range.
                    station.copy(distanceMiles = 500.0, routeDistanceMiles = 620.0, estimatedDriveMinutes = 560.0)
                }

            MockStationScenario.MISSING_PRICES ->
                normalStations(latitude, longitude, now).map { it.copy(fuelPrices = emptyList()) }

            MockStationScenario.STALE_PRICES ->
                normalStations(latitude, longitude, now).map { station ->
                    station.copy(
                        fuelPrices = station.fuelPrices.map {
                            it.copy(freshness = PriceFreshness.STALE, updatedAt = now - 30 * 60 * 60 * 1000L)
                        }
                    )
                }

            MockStationScenario.INVALID_COORDINATES ->
                normalStations(latitude, longitude, now).map { it.copy(latitude = Double.NaN, longitude = Double.NaN) }
        }
    }

    private fun normalStations(latitude: Double, longitude: Double, now: Long): List<FuelStation> = listOf(
        FuelStation(
            id = StationId(StationProvider.GOOGLE_PLACES, "costco_1"),
            name = "Costco Wholesale",
            brand = "Costco",
            latitude = latitude + 0.01,
            longitude = longitude + 0.01,
            address = "1200 Highway 10, Impala City",
            distanceMiles = 0.8,
            routeDistanceMiles = 1.1,
            estimatedDriveMinutes = 3.0,
            isOpen = true,
            navigationDestination = "maps://?q=${latitude + 0.01},${longitude + 0.01}",
            fuelPrices = listOf(
                StationFuelPrice(
                    fuelType = FuelType.REGULAR,
                    fuelGradeKey = "regular",
                    displayFuelGrade = "Regular",
                    price = FuelPricePerUnit(Money(CurrencyMicros(2_790_000L), "USD"), FuelPriceUnit.PER_GALLON),
                    updatedAt = now - 2 * 60 * 60 * 1000L, // 2h ago
                    freshness = PriceFreshness.RECENT,
                    source = "Mock Provider"
                )
            ),
            lastFetchedAt = now
        ),
        FuelStation(
            id = StationId(StationProvider.GOOGLE_PLACES, "chevron_2"),
            name = "Chevron",
            brand = "Chevron",
            latitude = latitude - 0.015,
            longitude = longitude + 0.02,
            address = "450 Main St, Impala City",
            distanceMiles = 1.4,
            routeDistanceMiles = 1.9,
            estimatedDriveMinutes = 5.0,
            isOpen = true,
            navigationDestination = "maps://?q=${latitude - 0.015},${longitude + 0.02}",
            fuelPrices = listOf(
                StationFuelPrice(
                    fuelType = FuelType.REGULAR,
                    fuelGradeKey = "regular",
                    displayFuelGrade = "Regular",
                    price = FuelPricePerUnit(Money(CurrencyMicros(3_150_000L), "USD"), FuelPriceUnit.PER_GALLON),
                    updatedAt = now - 8 * 60 * 60 * 1000L, // 8h ago
                    freshness = PriceFreshness.AGING,
                    source = "Mock Provider"
                )
            ),
            lastFetchedAt = now
        ),
        FuelStation(
            id = StationId(StationProvider.GOOGLE_PLACES, "shell_3"),
            name = "Shell",
            brand = "Shell",
            latitude = latitude + 0.03,
            longitude = longitude - 0.01,
            address = "900 Valley Blvd, Impala City",
            distanceMiles = 2.2,
            routeDistanceMiles = 2.9,
            estimatedDriveMinutes = 7.0,
            isOpen = true,
            navigationDestination = "maps://?q=${latitude + 0.03},${longitude - 0.01}",
            fuelPrices = listOf(
                StationFuelPrice(
                    fuelType = FuelType.REGULAR,
                    fuelGradeKey = "regular",
                    displayFuelGrade = "Regular",
                    price = FuelPricePerUnit(Money(CurrencyMicros(3_250_000L), "USD"), FuelPriceUnit.PER_GALLON),
                    updatedAt = now - 30 * 60 * 60 * 1000L, // 30h ago (stale)
                    freshness = PriceFreshness.STALE,
                    source = "Mock Provider"
                )
            ),
            lastFetchedAt = now
        )
    )
}
