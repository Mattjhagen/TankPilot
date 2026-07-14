package com.tankpilot.android.auto.screen

import android.content.Intent
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import com.tankpilot.android.auto.mapper.buildStationDetailPane
import com.tankpilot.android.auto.mapper.stationDetailTitle
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation
import com.tankpilot.fuelrescue.domain.RecommendationCategory

/**
 * Station Detail (PaneTemplate). Navigate hands off to the host's navigation app via
 * CarContext.ACTION_NAVIGATE — TankPilot never implements turn-by-turn itself. The
 * action is present only when the station's coordinate passes validation (see
 * hasValidNavigationTarget), so an invalid coordinate simply has no Navigate button
 * rather than a broken one.
 */
class StationDetailScreen(
    carContext: CarContext,
    private val recommendation: FuelStationRecommendation,
    private val categories: Set<RecommendationCategory>,
    private val warningForUnverifiedReachability: Boolean
) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val pane = buildStationDetailPane(
            recommendation = recommendation,
            categories = categories,
            warningForUnverifiedReachability = warningForUnverifiedReachability,
            onNavigateClick = { startNavigation() }
        )
        return PaneTemplate.Builder(pane)
            .setTitle(stationDetailTitle(recommendation, categories))
            .setHeaderAction(Action.BACK)
            .build()
    }

    private fun startNavigation() {
        val station = recommendation.station
        val uri = station.navigationDestination?.let { Uri.parse(it) }
            ?: Uri.parse("geo:${station.latitude},${station.longitude}?q=${Uri.encode(station.name)}")
        carContext.startCarApp(Intent(CarContext.ACTION_NAVIGATE, uri))
    }
}
