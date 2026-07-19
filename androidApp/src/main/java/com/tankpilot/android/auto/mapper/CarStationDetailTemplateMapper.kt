package com.tankpilot.android.auto.mapper

import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.Row
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation
import com.tankpilot.fuelrescue.domain.RecommendationCategory

/**
 * Thin androidx.car.app translation of [buildStationDetailContent]. The Navigate
 * action is added only when [hasValidNavigationTarget] passes and the station is not
 * synthetic demo data — an invalid coordinate simply omits the action rather than
 * offering a broken one, and a demo station shows an explicit disabled notice instead
 * of ever handing off navigation to a fabricated location.
 */
fun buildStationDetailPane(
    recommendation: FuelStationRecommendation,
    categories: Set<RecommendationCategory>,
    warningForUnverifiedReachability: Boolean,
    onNavigateClick: () -> Unit
): Pane {
    val content = buildStationDetailContent(recommendation, warningForUnverifiedReachability)
    val station = recommendation.station

    val builder = Pane.Builder()
        .addRow(Row.Builder().setTitle("Address").addText(content.addressLine).build())
        .addRow(Row.Builder().setTitle("Price").addText(content.priceLine).build())
        .addRow(Row.Builder().setTitle("Distance & Arrival").addText(content.distanceArrivalLine).build())

    content.warningLine?.let { warning ->
        builder.addRow(Row.Builder().setTitle("Note").addText(warning).build())
    }

    if (station.isDemoData) {
        builder.addRow(
            Row.Builder()
                .setTitle("Demo Station")
                .addText("Demo data — navigation disabled.")
                .build()
        )
    } else if (hasValidNavigationTarget(station)) {
        builder.addAction(
            Action.Builder()
                .setTitle("Navigate")
                .setOnClickListener(onNavigateClick)
                .build()
        )
    }

    return builder.build()
}

fun stationDetailTitle(recommendation: FuelStationRecommendation, categories: Set<RecommendationCategory>): String {
    val label = categoryLabel(categories)
    return if (label != null) "$label · ${recommendation.station.name}" else recommendation.station.name
}
