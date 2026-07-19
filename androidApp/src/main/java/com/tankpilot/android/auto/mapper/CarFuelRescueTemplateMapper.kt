package com.tankpilot.android.auto.mapper

import android.text.SpannableString
import android.text.Spannable
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import com.tankpilot.core.StationId
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation
import com.tankpilot.fuelrescue.domain.RecommendationCategory

/**
 * Thin androidx.car.app translation of [buildStationRowContent] — no business logic
 * here, just Row construction. Attaches Place metadata (map marker) and a structured
 * DistanceSpan whenever the station has a valid, navigable coordinate — a Google Play
 * POI-category host expects each populated place row to carry real location metadata,
 * not just descriptive text.
 */
fun buildFuelRescueItemList(
    recommendations: List<FuelStationRecommendation>,
    categories: Map<StationId, Set<RecommendationCategory>>,
    onStationClick: (FuelStationRecommendation) -> Unit
): ItemList {
    val builder = ItemList.Builder()
    recommendations.forEachIndexed { index, recommendation ->
        val content = buildStationRowContent(recommendation, categories[recommendation.station.id] ?: emptySet())
        val station = recommendation.station
        val rowBuilder = Row.Builder()
            .setTitle(content.title)
            .addText(distanceCarText(content.primaryLine, content.distanceMiles))
            .addText(content.secondaryLine)
            .setOnClickListener { onStationClick(recommendation) }

        if (hasValidNavigationTarget(station)) {
            val place = Place.Builder(CarLocation.create(station.latitude, station.longitude))
                .setMarker(PlaceMarker.Builder().setLabel((index + 1).toString()).build())
                .build()
            rowBuilder.setMetadata(Metadata.Builder().setPlace(place).build())
        }

        builder.addItem(rowBuilder.build())
    }
    return builder.build()
}

/**
 * Wraps the leading "X.X mi" portion of [primaryLine] in a [DistanceSpan] so the host
 * receives structured distance data, not just a string — the arrival-fuel text after
 * the " · " separator is left as plain text. No-op (returns the plain string) when
 * [distanceMiles] is null, matching the "Distance Unavailable" case.
 */
private fun distanceCarText(primaryLine: String, distanceMiles: Double?): CharSequence {
    if (distanceMiles == null) return primaryLine
    val spannable = SpannableString(primaryLine)
    spannable.setSpan(
        DistanceSpan.create(Distance.create(distanceMiles, Distance.UNIT_MILES)),
        0,
        distanceSpanEndIndex(primaryLine),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannable
}

/**
 * Pure: the [0, end) range of [primaryLine] the DistanceSpan should cover (the leading
 * "X.X mi" segment, before the " · " separator). Extracted as a standalone function so
 * it's testable on plain JVM — CarText's real span-extraction machinery
 * (android.text.Spannable.getSpans, used internally by CarText.create when passed a
 * Spanned CharSequence) only works on a real Android runtime, not in this project's
 * Robolectric-free unit tests, so the full Row/CarText construction path in
 * [buildFuelRescueItemList] is exercised on-device/DHU rather than here.
 */
internal fun distanceSpanEndIndex(primaryLine: String): Int =
    primaryLine.indexOf(" ·").let { if (it >= 0) it else primaryLine.length }
