package com.tankpilot.android.auto.mapper

import com.tankpilot.core.GeoCoordinate
import com.tankpilot.fuelrescue.domain.FuelStation
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation
import com.tankpilot.fuelrescue.domain.PriceFreshness
import com.tankpilot.fuelrescue.domain.RecommendationCategory
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val UNAVAILABLE = "Unavailable"

/**
 * Pure, platform-independent formatting for a Fuel Rescue list row — no androidx.car.app
 * types here so this stays unit-testable on plain JVM. [CarFuelRescueTemplateMapper]
 * turns this into an actual Row.
 */
data class CarStationRowContent(
    val title: String,
    val primaryLine: String,
    val secondaryLine: String
)

fun buildStationRowContent(
    recommendation: FuelStationRecommendation,
    categories: Set<RecommendationCategory>
): CarStationRowContent {
    val distanceMiles = recommendation.station.routeDistanceMiles
    val arrivalGallons = recommendation.estimatedFuelRemainingOnArrival.value

    val primaryLine = if (distanceMiles != null) {
        "${formatOneDecimal(distanceMiles)} mi · arrive with ${formatOneDecimal(arrivalGallons)} gal"
    } else {
        "Distance $UNAVAILABLE"
    }

    val secondaryLine = recommendation.advertisedPrice?.let { price ->
        val micros = price.toPerGallon().money.amountMicros.value
        "${formatCurrency(micros)}/gal · ${freshnessLabel(recommendation.priceFreshness)}"
    } ?: "Price $UNAVAILABLE"

    val label = categoryLabel(categories)
    val title = if (label != null) "$label · ${recommendation.station.name}" else recommendation.station.name

    return CarStationRowContent(title = title, primaryLine = primaryLine, secondaryLine = secondaryLine)
}

/** Best Overall takes priority when a station wins more than one category. */
fun categoryLabel(categories: Set<RecommendationCategory>): String? = when {
    RecommendationCategory.BEST_OVERALL in categories -> "Best Overall"
    RecommendationCategory.CLOSEST_SAFE in categories -> "Closest Safe"
    RecommendationCategory.CHEAPEST_REACHABLE in categories -> "Cheapest Reachable"
    else -> null
}

/** Navigate must never be offered against an invalid or missing coordinate. */
fun hasValidNavigationTarget(station: FuelStation): Boolean {
    return GeoCoordinate.isValid(station.latitude, station.longitude)
}

data class CarStationDetailContent(
    val addressLine: String,
    val priceLine: String,
    val distanceArrivalLine: String,
    val warningLine: String?
)

fun buildStationDetailContent(
    recommendation: FuelStationRecommendation,
    warningForUnverifiedReachability: Boolean
): CarStationDetailContent {
    val addressLine = recommendation.station.address ?: "Address $UNAVAILABLE"

    val priceLine = recommendation.advertisedPrice?.let { price ->
        val micros = price.toPerGallon().money.amountMicros.value
        "${formatCurrency(micros)}/gal · ${freshnessLabel(recommendation.priceFreshness)}"
    } ?: "Price $UNAVAILABLE"

    val distanceMiles = recommendation.station.routeDistanceMiles
    val arrivalGallons = recommendation.estimatedFuelRemainingOnArrival.value
    val distanceArrivalLine = if (distanceMiles != null) {
        "${formatOneDecimal(distanceMiles)} mi · arrive with ${formatOneDecimal(arrivalGallons)} gal"
    } else {
        "Distance $UNAVAILABLE"
    }

    val warningLine = if (warningForUnverifiedReachability) {
        "This station's reachability could not be confirmed at your current fuel level."
    } else {
        null
    }

    return CarStationDetailContent(addressLine, priceLine, distanceArrivalLine, warningLine)
}

private fun freshnessLabel(freshness: PriceFreshness): String = when (freshness) {
    PriceFreshness.RECENT -> "recently reported"
    PriceFreshness.AGING -> "reported hours ago"
    PriceFreshness.STALE -> "price may be outdated"
    PriceFreshness.UNKNOWN -> "freshness unknown"
}

/**
 * Curates the up-to-3-station display list from the full ranked recommendation set,
 * showing only the distinct stations that won a category (a station can win more than
 * one). Order: Best Overall, then Closest Safe, then Cheapest Reachable.
 */
fun selectDisplayedRecommendations(
    recommendations: List<FuelStationRecommendation>,
    categories: Map<com.tankpilot.core.StationId, Set<RecommendationCategory>>
): List<FuelStationRecommendation> {
    val order = listOf(
        RecommendationCategory.BEST_OVERALL,
        RecommendationCategory.CLOSEST_SAFE,
        RecommendationCategory.CHEAPEST_REACHABLE
    )
    val seen = mutableSetOf<com.tankpilot.core.StationId>()
    val result = mutableListOf<FuelStationRecommendation>()
    for (category in order) {
        val stationId = categories.entries.firstOrNull { category in it.value }?.key ?: continue
        if (!seen.add(stationId)) continue
        recommendations.firstOrNull { it.station.id == stationId }?.let { result.add(it) }
    }
    return result
}

private fun formatOneDecimal(value: Double): String {
    val rounded = (value * 10.0).roundToInt() / 10.0
    return rounded.toString()
}

private fun formatCurrency(amountMicros: Long): String {
    val cents = (amountMicros / 10_000.0).roundToLong()
    val dollars = cents / 100
    val remainderCents = cents % 100
    return "$$dollars.${remainderCents.toString().padStart(2, '0')}"
}
