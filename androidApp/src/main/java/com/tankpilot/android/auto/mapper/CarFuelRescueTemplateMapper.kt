package com.tankpilot.android.auto.mapper

import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import com.tankpilot.core.StationId
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation
import com.tankpilot.fuelrescue.domain.RecommendationCategory

/**
 * Thin androidx.car.app translation of [buildStationRowContent] — no business logic
 * here, just Row construction.
 */
fun buildFuelRescueItemList(
    recommendations: List<FuelStationRecommendation>,
    categories: Map<StationId, Set<RecommendationCategory>>,
    onStationClick: (FuelStationRecommendation) -> Unit
): ItemList {
    val builder = ItemList.Builder()
    recommendations.forEach { recommendation ->
        val content = buildStationRowContent(recommendation, categories[recommendation.station.id] ?: emptySet())
        builder.addItem(
            Row.Builder()
                .setTitle(content.title)
                .addText(content.primaryLine)
                .addText(content.secondaryLine)
                .setOnClickListener { onStationClick(recommendation) }
                .build()
        )
    }
    return builder.build()
}
