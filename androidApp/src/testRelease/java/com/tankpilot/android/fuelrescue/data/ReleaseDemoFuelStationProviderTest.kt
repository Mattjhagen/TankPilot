package com.tankpilot.android.fuelrescue.data

import com.tankpilot.core.FuelType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Release-variant test (only runs under :androidApp:testReleaseUnitTest, since this
 * class lives under src/release) — proves the Phase A demo fixtures are exactly what
 * Google Play Internal Testing bring-up requires: 3 uniquely-named, unmistakably
 * synthetic stations, all flagged isDemoData, positioned near the caller-supplied
 * origin (not a fixed real-world location).
 */
class ReleaseDemoFuelStationProviderTest {

    private val provider = ReleaseDemoFuelStationProvider()

    @Test
    fun returnsExactlyThreeUniquelyNamedDemoStations() = runTest {
        val stations = provider.getNearbyStations(37.0, -122.0, radiusMiles = 15.0, fuelType = FuelType.REGULAR)

        assertEquals(3, stations.size)
        assertEquals(setOf("Demo Fuel Stop A", "Demo Fuel Stop B", "Demo Fuel Stop C"), stations.map { it.name }.toSet())
    }

    @Test
    fun noStationUsesARealBusinessName() {
        val realBrandNames = setOf("costco", "chevron", "shell", "exxon", "mobil", "bp", "arco", "circle k")
        runTest {
            val stations = provider.getNearbyStations(37.0, -122.0, radiusMiles = 15.0, fuelType = FuelType.REGULAR)
            stations.forEach { station ->
                val lowerName = station.name.lowercase()
                assertTrue(
                    "Station name '${station.name}' must not resemble a real business name",
                    realBrandNames.none { lowerName.contains(it) }
                )
            }
        }
    }

    @Test
    fun everyStationIsFlaggedAsDemoData() = runTest {
        val stations = provider.getNearbyStations(37.0, -122.0, radiusMiles = 15.0, fuelType = FuelType.REGULAR)
        stations.forEach { assertTrue("${it.name} must be flagged isDemoData", it.isDemoData) }
    }

    @Test
    fun stationsAreGeneratedNearTheSuppliedOriginNotAFixedLocation() = runTest {
        val origin1 = provider.getNearbyStations(37.0, -122.0, radiusMiles = 15.0, fuelType = FuelType.REGULAR)
        val origin2 = provider.getNearbyStations(40.7128, -74.0060, radiusMiles = 15.0, fuelType = FuelType.REGULAR)

        origin1.forEach { station ->
            assertTrue(
                "Station near origin1 should be within ~0.1 degrees of (37.0, -122.0)",
                kotlin.math.abs(station.latitude - 37.0) < 0.1 && kotlin.math.abs(station.longitude - (-122.0)) < 0.1
            )
        }
        origin2.forEach { station ->
            assertTrue(
                "Station near origin2 should be within ~0.1 degrees of (40.7128, -74.0060)",
                kotlin.math.abs(station.latitude - 40.7128) < 0.1 && kotlin.math.abs(station.longitude - (-74.0060)) < 0.1
            )
        }
    }
}
