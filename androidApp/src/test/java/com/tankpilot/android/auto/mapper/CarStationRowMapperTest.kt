package com.tankpilot.android.auto.mapper

import com.tankpilot.core.CurrencyMicros
import com.tankpilot.core.FuelPricePerUnit
import com.tankpilot.core.FuelPriceUnit
import com.tankpilot.core.Gallons
import com.tankpilot.core.Money
import com.tankpilot.core.StationId
import com.tankpilot.core.StationProvider
import com.tankpilot.fuelrescue.domain.FuelStation
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation
import com.tankpilot.fuelrescue.domain.PriceFreshness
import com.tankpilot.fuelrescue.domain.ReachabilityStatus
import com.tankpilot.fuelrescue.domain.RecommendationCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CarStationRowMapperTest {

    private val now = 1_000_000_000L

    private fun station(
        id: String = "s1",
        latitude: Double = 37.0,
        longitude: Double = -122.0,
        routeDistanceMiles: Double? = 2.3
    ) = FuelStation(
        id = StationId(StationProvider.GOOGLE_PLACES, id),
        name = "Shell",
        brand = "Shell",
        latitude = latitude,
        longitude = longitude,
        address = "123 Main St",
        distanceMiles = routeDistanceMiles ?: 2.3,
        routeDistanceMiles = routeDistanceMiles,
        estimatedDriveMinutes = 6.0,
        isOpen = true,
        navigationDestination = null,
        fuelPrices = emptyList(),
        lastFetchedAt = now
    )

    private fun recommendation(
        station: FuelStation = station(),
        advertisedPrice: FuelPricePerUnit? = FuelPricePerUnit(
            Money(CurrencyMicros(3_190_000L), "USD"),
            FuelPriceUnit.PER_GALLON
        ),
        freshness: PriceFreshness = PriceFreshness.RECENT
    ) = FuelStationRecommendation(
        station = station,
        reachabilityStatus = ReachabilityStatus.SAFELY_REACHABLE,
        estimatedFuelUsedToReach = Gallons(0.5),
        estimatedFuelRemainingOnArrival = Gallons(1.8),
        advertisedPrice = advertisedPrice,
        effectiveTripCost = Money(CurrencyMicros(0L), "USD"),
        estimatedFillCost = Money(CurrencyMicros(0L), "USD"),
        estimatedSavings = Money(CurrencyMicros(0L), "USD"),
        detourMiles = 0.0,
        priceFreshness = freshness,
        recommendationScore = 80.0,
        recommendationReasons = emptyList(),
        warningMessages = emptyList()
    )

    @Test
    fun priceLineAlwaysCarriesAFreshnessLabelWhenPriceIsPresent() {
        for (freshness in PriceFreshness.values()) {
            val content = buildStationRowContent(recommendation(freshness = freshness), emptySet())
            assertTrue(
                "secondaryLine for $freshness should mention price, was: ${content.secondaryLine}",
                content.secondaryLine.contains("/gal")
            )
            assertFalse(
                "secondaryLine for $freshness dropped freshness context: ${content.secondaryLine}",
                content.secondaryLine == "\$3.19/gal"
            )
        }
    }

    @Test
    fun missingPriceRendersAsUnavailableNotBlankOrZero() {
        val content = buildStationRowContent(recommendation(advertisedPrice = null), emptySet())
        assertEquals("Price Unavailable", content.secondaryLine)
    }

    @Test
    fun categoryLabelPrioritizesBestOverall() {
        val all = setOf(
            RecommendationCategory.BEST_OVERALL,
            RecommendationCategory.CLOSEST_SAFE,
            RecommendationCategory.CHEAPEST_REACHABLE
        )
        assertEquals("Best Overall", categoryLabel(all))
        assertEquals("Closest Safe", categoryLabel(setOf(RecommendationCategory.CLOSEST_SAFE)))
        assertEquals("Cheapest Reachable", categoryLabel(setOf(RecommendationCategory.CHEAPEST_REACHABLE)))
        assertNull(categoryLabel(emptySet()))
    }

    @Test
    fun validStationCoordinateEnablesNavigation() {
        assertTrue(hasValidNavigationTarget(station(latitude = 37.7749, longitude = -122.4194)))
    }

    @Test
    fun invalidStationCoordinateRemovesNavigationAction() {
        assertFalse(hasValidNavigationTarget(station(latitude = Double.NaN, longitude = -122.4194)))
        assertFalse(hasValidNavigationTarget(station(latitude = 0.0, longitude = 0.0)))
        assertFalse(hasValidNavigationTarget(station(latitude = 200.0, longitude = -122.0)))
    }

    @Test
    fun selectDisplayedRecommendationsDedupesWhenOneStationWinsEveryCategory() {
        val rec = recommendation()
        val categories = mapOf(
            rec.station.id to setOf(
                RecommendationCategory.BEST_OVERALL,
                RecommendationCategory.CLOSEST_SAFE,
                RecommendationCategory.CHEAPEST_REACHABLE
            )
        )

        val displayed = selectDisplayedRecommendations(listOf(rec), categories)

        assertEquals(1, displayed.size)
    }
}
