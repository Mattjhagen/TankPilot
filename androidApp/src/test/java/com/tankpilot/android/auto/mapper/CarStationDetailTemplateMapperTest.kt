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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the Navigate-action gating on the Station Detail Pane: real stations with a
 * valid coordinate get Navigate, invalid coordinates get neither Navigate nor a demo
 * notice, and — critically — demo stations never get Navigate even when their
 * coordinate is technically valid, since navigation must never be handed off to a
 * synthetic location.
 */
class CarStationDetailTemplateMapperTest {

    private val now = 1_000_000_000L

    private fun station(
        latitude: Double = 37.7749,
        longitude: Double = -122.4194,
        isDemoData: Boolean = false
    ) = FuelStation(
        id = StationId(StationProvider.GOOGLE_PLACES, "s1"),
        name = "Shell",
        brand = "Shell",
        latitude = latitude,
        longitude = longitude,
        address = "123 Main St",
        distanceMiles = 3.4,
        routeDistanceMiles = 3.4,
        estimatedDriveMinutes = 6.0,
        isOpen = true,
        navigationDestination = null,
        fuelPrices = emptyList(),
        lastFetchedAt = now,
        isDemoData = isDemoData
    )

    private fun recommendation(station: FuelStation) = FuelStationRecommendation(
        station = station,
        reachabilityStatus = ReachabilityStatus.SAFELY_REACHABLE,
        estimatedFuelUsedToReach = Gallons(0.5),
        estimatedFuelRemainingOnArrival = Gallons(1.8),
        advertisedPrice = FuelPricePerUnit(Money(CurrencyMicros(3_190_000L), "USD"), FuelPriceUnit.PER_GALLON),
        effectiveTripCost = Money(CurrencyMicros(0L), "USD"),
        estimatedFillCost = Money(CurrencyMicros(0L), "USD"),
        estimatedSavings = Money(CurrencyMicros(0L), "USD"),
        detourMiles = 0.0,
        priceFreshness = PriceFreshness.RECENT,
        recommendationScore = 80.0,
        recommendationReasons = emptyList(),
        warningMessages = emptyList()
    )

    private fun actionTitles(pane: androidx.car.app.model.Pane) = pane.actions.map { it.title.toString() }
    private fun rowTitles(pane: androidx.car.app.model.Pane) = pane.rows.map { it.title.toString() }

    @Test
    fun realStationWithValidCoordinateGetsNavigateActionAndNoDemoNotice() {
        val rec = recommendation(station(isDemoData = false))
        val pane = buildStationDetailPane(rec, emptySet(), warningForUnverifiedReachability = false) {}

        assertTrue("Expected a Navigate action", actionTitles(pane).contains("Navigate"))
        assertFalse("Real station must not show a demo notice", rowTitles(pane).contains("Demo Station"))
    }

    @Test
    fun demoStationNeverGetsNavigateActionEvenWithAValidCoordinate() {
        val rec = recommendation(station(latitude = 37.7749, longitude = -122.4194, isDemoData = true))
        val pane = buildStationDetailPane(rec, emptySet(), warningForUnverifiedReachability = false) {}

        assertFalse("A demo station must never offer navigation hand-off", actionTitles(pane).contains("Navigate"))
        assertTrue("Expected a demo notice row instead", rowTitles(pane).contains("Demo Station"))
    }

    @Test
    fun invalidCoordinateNonDemoStationGetsNeitherNavigateNorDemoNotice() {
        val rec = recommendation(station(latitude = Double.NaN, longitude = -122.0, isDemoData = false))
        val pane = buildStationDetailPane(rec, emptySet(), warningForUnverifiedReachability = false) {}

        assertFalse(actionTitles(pane).contains("Navigate"))
        assertFalse(rowTitles(pane).contains("Demo Station"))
    }
}
