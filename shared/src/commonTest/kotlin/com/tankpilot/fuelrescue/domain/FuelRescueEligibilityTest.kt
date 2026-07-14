package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.CurrencyMicros
import com.tankpilot.core.FuelPricePerUnit
import com.tankpilot.core.FuelPriceUnit
import com.tankpilot.core.FuelType
import com.tankpilot.core.Gallons
import com.tankpilot.core.Money
import com.tankpilot.core.StationId
import com.tankpilot.core.StationProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FuelRescueEligibilityTest {

    private val now = 1_000_000_000L

    private fun station(
        id: String,
        routeDistanceMiles: Double? = 2.0,
        isOpen: Boolean? = true
    ) = FuelStation(
        id = StationId(StationProvider.GOOGLE_PLACES, id),
        name = "Station $id",
        brand = null,
        latitude = 37.0,
        longitude = -122.0,
        address = "123 Main St",
        distanceMiles = routeDistanceMiles ?: 2.0,
        routeDistanceMiles = routeDistanceMiles,
        estimatedDriveMinutes = 5.0,
        isOpen = isOpen,
        navigationDestination = null,
        fuelPrices = emptyList(),
        lastFetchedAt = now
    )

    private fun price(dollars: Double) = FuelPricePerUnit(
        Money(CurrencyMicros((dollars * 1_000_000).toLong()), "USD"),
        FuelPriceUnit.PER_GALLON
    )

    private fun recommendation(
        stationId: String,
        reachability: ReachabilityStatus,
        routeDistanceMiles: Double? = 2.0,
        isOpen: Boolean? = true,
        advertisedPrice: FuelPricePerUnit? = price(3.50),
        effectiveTripCostDollars: Double = 10.0,
        score: Double = 50.0
    ) = FuelStationRecommendation(
        station = station(stationId, routeDistanceMiles, isOpen),
        reachabilityStatus = reachability,
        estimatedFuelUsedToReach = Gallons(1.0),
        estimatedFuelRemainingOnArrival = Gallons(1.0),
        advertisedPrice = advertisedPrice,
        effectiveTripCost = Money(CurrencyMicros((effectiveTripCostDollars * 1_000_000).toLong()), "USD"),
        estimatedFillCost = Money(CurrencyMicros(0L), "USD"),
        estimatedSavings = Money(CurrencyMicros(0L), "USD"),
        detourMiles = 0.0,
        priceFreshness = PriceFreshness.RECENT,
        recommendationScore = score,
        recommendationReasons = emptyList(),
        warningMessages = emptyList()
    )

    @Test
    fun unsafeStationsNeverAppearInCategorize() {
        val safe = recommendation("safe", ReachabilityStatus.SAFELY_REACHABLE)
        val marginal = recommendation("marginal", ReachabilityStatus.MARGINALLY_REACHABLE)
        val outside = recommendation("outside", ReachabilityStatus.OUTSIDE_SAFE_RANGE)
        val unknown = recommendation("unknown", ReachabilityStatus.UNKNOWN, routeDistanceMiles = null)

        val categories = FuelRescueEligibility.categorize(listOf(safe, marginal, outside, unknown))

        assertTrue(categories.containsKey(safe.station.id))
        assertFalse(categories.containsKey(marginal.station.id))
        assertFalse(categories.containsKey(outside.station.id))
        assertFalse(categories.containsKey(unknown.station.id))
    }

    @Test
    fun missingPriceStationCanBeClosestSafeButNeverCheapestReachable() {
        val priceless = recommendation(
            "priceless",
            ReachabilityStatus.SAFELY_REACHABLE,
            routeDistanceMiles = 1.0,
            advertisedPrice = null
        )
        val priced = recommendation(
            "priced",
            ReachabilityStatus.SAFELY_REACHABLE,
            routeDistanceMiles = 5.0,
            advertisedPrice = price(3.00),
            effectiveTripCostDollars = 5.0
        )

        val categories = FuelRescueEligibility.categorize(listOf(priceless, priced))

        assertTrue(RecommendationCategory.CLOSEST_SAFE in (categories[priceless.station.id] ?: emptySet()))
        assertFalse(RecommendationCategory.CHEAPEST_REACHABLE in (categories[priceless.station.id] ?: emptySet()))
        assertTrue(RecommendationCategory.CHEAPEST_REACHABLE in (categories[priced.station.id] ?: emptySet()))
    }

    @Test
    fun closedStationNeverCategorizedEvenIfOtherwiseEligible() {
        val closed = recommendation("closed", ReachabilityStatus.SAFELY_REACHABLE, isOpen = false)

        val categories = FuelRescueEligibility.categorize(listOf(closed))

        assertTrue(categories.isEmpty())
        assertFalse(FuelRescueEligibility.hasSafeRecommendation(listOf(closed)))
    }

    @Test
    fun noSafeStationMeansNoRecommendationAndEmptyCategories() {
        val marginal = recommendation("marginal", ReachabilityStatus.MARGINALLY_REACHABLE)
        val outside = recommendation("outside", ReachabilityStatus.OUTSIDE_SAFE_RANGE)

        assertFalse(FuelRescueEligibility.hasSafeRecommendation(listOf(marginal, outside)))
        assertTrue(FuelRescueEligibility.categorize(listOf(marginal, outside)).isEmpty())
    }

    @Test
    fun sameStationCanWinMultipleCategories() {
        val onlyOption = recommendation("only", ReachabilityStatus.SAFELY_REACHABLE)

        val categories = FuelRescueEligibility.categorize(listOf(onlyOption))

        val won = categories[onlyOption.station.id] ?: emptySet()
        assertEquals(
            setOf(RecommendationCategory.BEST_OVERALL, RecommendationCategory.CLOSEST_SAFE, RecommendationCategory.CHEAPEST_REACHABLE),
            won
        )
    }
}
