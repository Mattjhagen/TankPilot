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
        routeDistanceMiles: Double? = 2.3,
        isDemoData: Boolean = false
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
        lastFetchedAt = now,
        isDemoData = isDemoData
    )

    private fun recommendation(
        station: FuelStation = station(),
        advertisedPrice: FuelPricePerUnit? = FuelPricePerUnit(
            Money(CurrencyMicros(3_190_000L), "USD"),
            FuelPriceUnit.PER_GALLON
        ),
        freshness: PriceFreshness = PriceFreshness.RECENT,
        reachabilityStatus: ReachabilityStatus = ReachabilityStatus.SAFELY_REACHABLE
    ) = FuelStationRecommendation(
        station = station,
        reachabilityStatus = reachabilityStatus,
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

    @Test
    fun rowContentCarriesRawDistanceMilesForStructuredDistanceSpan() {
        val content = buildStationRowContent(recommendation(station = station(routeDistanceMiles = 4.7)), emptySet())
        assertEquals(4.7, content.distanceMiles!!, 0.0001)
    }

    @Test
    fun rowContentDistanceMilesIsNullWhenRouteDistanceUnavailable() {
        val content = buildStationRowContent(recommendation(station = station(routeDistanceMiles = null)), emptySet())
        assertNull(content.distanceMiles)
    }

    @Test
    fun selectRootDisplayRecommendationsReturnsAllDemoStationsIgnoringCategoryWinners() {
        val demoRecs = listOf(
            recommendation(station = station(id = "a", isDemoData = true)),
            recommendation(station = station(id = "b", isDemoData = true)),
            recommendation(station = station(id = "c", isDemoData = true))
        )
        // No category winners at all — production selectDisplayedRecommendations would
        // return an empty list here, but the demo-fixture path must show all 3 regardless.
        val displayed = selectRootDisplayRecommendations(demoRecs, emptyMap())
        assertEquals(3, displayed.size)
    }

    @Test
    fun selectRootDisplayRecommendationsFallsBackToProductionCurationWithNoDemoStations() {
        val rec = recommendation()
        val categories = mapOf(rec.station.id to setOf(RecommendationCategory.BEST_OVERALL))
        val displayed = selectRootDisplayRecommendations(listOf(rec), categories)
        assertEquals(selectDisplayedRecommendations(listOf(rec), categories), displayed)
    }

    @Test
    fun isRootCriticalIsFalseWheneverAnyDemoStationIsPresent() {
        // Every demo station is unreachable by real eligibility rules — would otherwise
        // read as critical/no-safe-station — but the demo-validation fixture must never
        // trigger the critical hand-off.
        val demoRecs = listOf(
            recommendation(
                station = station(id = "a", isDemoData = true),
                reachabilityStatus = ReachabilityStatus.OUTSIDE_SAFE_RANGE
            )
        )
        assertFalse(isRootCritical(demoRecs))
    }

    @Test
    fun isRootCriticalMatchesRealEligibilityWithNoDemoStationsPresent() {
        val unreachable = listOf(
            recommendation(reachabilityStatus = ReachabilityStatus.OUTSIDE_SAFE_RANGE)
        )
        assertTrue(isRootCritical(unreachable))

        val reachable = listOf(recommendation(reachabilityStatus = ReachabilityStatus.SAFELY_REACHABLE))
        assertFalse(isRootCritical(reachable))
    }
}
