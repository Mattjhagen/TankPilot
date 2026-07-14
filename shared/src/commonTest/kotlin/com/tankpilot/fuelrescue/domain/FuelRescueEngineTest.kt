package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.CurrencyMicros
import com.tankpilot.core.FuelPriceUnit
import com.tankpilot.core.FuelPricePerUnit
import com.tankpilot.core.FuelType
import com.tankpilot.core.Gallons
import com.tankpilot.core.Money
import com.tankpilot.core.MilesPerGallon
import com.tankpilot.core.StationId
import com.tankpilot.core.StationProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FuelRescueEngineTest {

    private val now = 1000000000L

    @Test
    fun testDetourCostDefeatsCheaperPumpPrice() {
        val idA = StationId(StationProvider.GOOGLE_PLACES, "A")
        val idB = StationId(StationProvider.GOOGLE_PLACES, "B")

        val stationA = FuelStation(
            id = idA,
            name = "Station A",
            brand = "Brand A",
            latitude = 0.0,
            longitude = 0.0,
            address = "Address A",
            distanceMiles = 1.0,
            routeDistanceMiles = 1.0,
            estimatedDriveMinutes = 2.0,
            isOpen = true,
            navigationDestination = null,
            fuelPrices = listOf(
                StationFuelPrice(FuelType.REGULAR, "regular", null, FuelPricePerUnit(Money(CurrencyMicros(3_000_000L), "USD"), FuelPriceUnit.PER_GALLON), now, PriceFreshness.RECENT, "test")
            ),
            lastFetchedAt = now
        )

        val stationB = FuelStation(
            id = idB,
            name = "Station B",
            brand = "Brand B",
            latitude = 0.0,
            longitude = 0.0,
            address = "Address B",
            distanceMiles = 10.0,
            routeDistanceMiles = 10.0,
            estimatedDriveMinutes = 15.0,
            isOpen = true,
            navigationDestination = null,
            fuelPrices = listOf(
                StationFuelPrice(FuelType.REGULAR, "regular", null, FuelPricePerUnit(Money(CurrencyMicros(2_800_000L), "USD"), FuelPriceUnit.PER_GALLON), now, PriceFreshness.RECENT, "test")
            ),
            lastFetchedAt = now
        )

        val routeDistances = mapOf(
            idA to Pair(1.0, 2.0),
            idB to Pair(10.0, 15.0)
        )

        val recs = FuelRescueEngine.evaluateStations(
            estimatedRemaining = Gallons(5.0),
            learnedMpg = MilesPerGallon(20.0),
            confidenceLevel = ConfidenceLevel.VERY_HIGH,
            vehicleFuelType = FuelType.REGULAR,
            vehicleFuelGradeKey = "regular",
            reserveFuel = Gallons(1.0),
            tankCapacity = Gallons(15.0),
            candidates = listOf(stationA, stationB),
            routeDistances = routeDistances
        )

        assertTrue(recs.size == 2)
        assertEquals(idA, recs[0].station.id)
        assertEquals(idB, recs[1].station.id)
    }

    @Test
    fun testOutsideSafeRangeExclusion() {
        val idC = StationId(StationProvider.GOOGLE_PLACES, "C")

        // Station is 30 miles away, physical range is 20 miles. This is OUTSIDE_SAFE_RANGE.
        val stationC = FuelStation(
            id = idC,
            name = "Station C",
            brand = "Brand C",
            latitude = 0.0,
            longitude = 0.0,
            address = "Address C",
            distanceMiles = 30.0,
            routeDistanceMiles = 30.0,
            estimatedDriveMinutes = 40.0,
            isOpen = true,
            navigationDestination = null,
            fuelPrices = listOf(
                StationFuelPrice(FuelType.REGULAR, "regular", null, FuelPricePerUnit(Money(CurrencyMicros(3_000_000L), "USD"), FuelPriceUnit.PER_GALLON), now, PriceFreshness.RECENT, "test")
            ),
            lastFetchedAt = now
        )

        val routeDistances = mapOf(
            idC to Pair(30.0, 40.0)
        )

        val recs = FuelRescueEngine.evaluateStations(
            estimatedRemaining = Gallons(1.0),
            learnedMpg = MilesPerGallon(20.0),
            confidenceLevel = ConfidenceLevel.VERY_HIGH,
            vehicleFuelType = FuelType.REGULAR,
            vehicleFuelGradeKey = "regular",
            reserveFuel = Gallons(1.0),
            tankCapacity = Gallons(15.0),
            candidates = listOf(stationC),
            routeDistances = routeDistances
        )

        assertTrue(recs.isNotEmpty())
        assertEquals(ReachabilityStatus.OUTSIDE_SAFE_RANGE, recs[0].reachabilityStatus)
        assertEquals(0.0, recs[0].recommendationScore)
    }

    @Test
    fun testMarginallyReachableStatus() {
        val idC = StationId(StationProvider.GOOGLE_PLACES, "C")

        // Station is 10 miles away. Max physical range is 20 miles.
        // Usable fuel from origin = 1.0 - 1.0 = 0.0 gallons -> Safe Range = 0.0 miles.
        // Distance (10.0) > Safe Range (0.0) but arrival fuel (0.5 gal) > 0.0.
        // This is MARGINALLY_REACHABLE.
        val stationC = FuelStation(
            id = idC,
            name = "Station C",
            brand = "Brand C",
            latitude = 0.0,
            longitude = 0.0,
            address = "Address C",
            distanceMiles = 10.0,
            routeDistanceMiles = 10.0,
            estimatedDriveMinutes = 15.0,
            isOpen = true,
            navigationDestination = null,
            fuelPrices = listOf(
                StationFuelPrice(FuelType.REGULAR, "regular", null, FuelPricePerUnit(Money(CurrencyMicros(3_000_000L), "USD"), FuelPriceUnit.PER_GALLON), now, PriceFreshness.RECENT, "test")
            ),
            lastFetchedAt = now
        )

        val routeDistances = mapOf(
            idC to Pair(10.0, 15.0)
        )

        val recs = FuelRescueEngine.evaluateStations(
            estimatedRemaining = Gallons(1.0),
            learnedMpg = MilesPerGallon(20.0),
            confidenceLevel = ConfidenceLevel.VERY_HIGH,
            vehicleFuelType = FuelType.REGULAR,
            vehicleFuelGradeKey = "regular",
            reserveFuel = Gallons(1.0),
            tankCapacity = Gallons(15.0),
            candidates = listOf(stationC),
            routeDistances = routeDistances
        )

        assertTrue(recs.isNotEmpty())
        assertEquals(ReachabilityStatus.MARGINALLY_REACHABLE, recs[0].reachabilityStatus)
    }
}
