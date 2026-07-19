package com.tankpilot.android.auto.mapper

import androidx.car.app.model.Row
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the androidx.car.app translation layer directly — every populated Place List
 * row must carry structured Place metadata (map marker) and a DistanceSpan when the
 * station has a valid, navigable coordinate, per Google's POI-category expectations
 * (see TankPilotCarRootScreen's doc comment for the product rationale).
 */
class CarFuelRescueTemplateMapperTest {

    private val now = 1_000_000_000L

    private fun station(
        latitude: Double = 37.7749,
        longitude: Double = -122.4194,
        routeDistanceMiles: Double? = 3.4
    ) = FuelStation(
        id = StationId(StationProvider.GOOGLE_PLACES, "s1"),
        name = "Shell",
        brand = "Shell",
        latitude = latitude,
        longitude = longitude,
        address = "123 Main St",
        distanceMiles = routeDistanceMiles ?: 3.4,
        routeDistanceMiles = routeDistanceMiles,
        estimatedDriveMinutes = 6.0,
        isOpen = true,
        navigationDestination = null,
        fuelPrices = emptyList(),
        lastFetchedAt = now
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

    // Metadata/Place attachment is tested with routeDistanceMiles = null, sidestepping
    // the DistanceSpan/SpannableString path entirely (see distanceSpanEndIndex's doc
    // comment for why that path can't run in this project's plain-JVM unit tests) —
    // Metadata attachment is governed solely by hasValidNavigationTarget(station), which
    // is independent of distance formatting.

    @Test
    fun validCoordinateRowCarriesPlaceMetadataWithMarker() {
        val rec = recommendation(station(latitude = 37.7749, longitude = -122.4194, routeDistanceMiles = null))
        val itemList = buildFuelRescueItemList(listOf(rec), emptyMap()) {}

        val row = itemList.items[0] as Row
        val place = row.metadata.place
        assertTrue("Expected Place metadata for a valid coordinate", place != null)
        assertTrue(
            "Marker location should match the station's coordinate",
            place!!.location.latitude == 37.7749 && place.location.longitude == -122.4194
        )
    }

    @Test
    fun invalidCoordinateRowHasNoPlaceMetadata() {
        val rec = recommendation(station(latitude = Double.NaN, longitude = -122.0, routeDistanceMiles = null))
        val itemList = buildFuelRescueItemList(listOf(rec), emptyMap()) {}

        val row = itemList.items[0] as Row
        assertNull("An invalid coordinate must never fabricate a map marker", row.metadata.place)
    }

    @Test
    fun distanceSpanRangeCoversOnlyTheLeadingDistanceSegment() {
        val line = "3.4 mi · arrive with 1.8 gal"
        val separatorIndex = line.indexOf(" ·")
        assertEquals(separatorIndex, distanceSpanEndIndex(line))
    }

    @Test
    fun distanceSpanRangeCoversTheWholeStringWhenNoSeparatorIsPresent() {
        val line = "Distance Unavailable"
        assertEquals(line.length, distanceSpanEndIndex(line))
    }
}
