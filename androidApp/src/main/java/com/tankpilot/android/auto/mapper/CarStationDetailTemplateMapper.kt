package com.tankpilot.android.auto.mapper

import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.Row
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation
import com.tankpilot.fuelrescue.domain.RecommendationCategory

/**
 * Thin androidx.car.app translation of [buildStationDetailContent]. The Navigate
 * action is added only when [hasValidNavigationTarget] passes — an invalid station
 * coordinate simply omits the action rather than offering a broken one.
 */
fun buildStationDetailPane(
    recommendation: FuelStationRecommendation,
    categories: Set<RecommendationCategory>,
    warningForUnverifiedReachability: Boolean,
    onNavigateClick: () -> Unit
): Pane {
    val content = buildStationDetailContent(recommendation, warningForUnverifiedReachability)

    val builder = Pane.Builder()
        .addRow(Row.Builder().setTitle("Address").addText(content.addressLine).build())
        .addRow(Row.Builder().setTitle("Price").addText(content.priceLine).build())
        .addRow(Row.Builder().setTitle("Distance & Arrival").addText(content.distanceArrivalLine).build())

    content.warningLine?.let { warning ->
        builder.addRow(Row.Builder().setTitle("Note").addText(warning).build())
    }

    if (hasValidNavigationTarget(recommendation.station)) {
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
