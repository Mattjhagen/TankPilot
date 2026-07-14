package com.tankpilot.android.auto.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.tankpilot.android.auto.mapper.buildFuelRescueItemList
import com.tankpilot.android.auto.mapper.selectDisplayedRecommendations
import com.tankpilot.fuelrescue.domain.FuelRescueEligibility
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Fuel Rescue Recommendations (category POI, PlaceListMapTemplate). Shows only
 * stations FuelRescueEligibility judged safely reachable — see the Important Safety
 * Rule in phases/phase-03a-android-auto-foundation.md. When none exist, hands off to
 * CriticalFuelScreen exactly once (guarded — this screen's own onGetTemplate never
 * re-decides navigation, only reacts to the already-made decision).
 */
class FuelRescueRecommendationsScreen(
    carContext: CarContext,
    private val fuelRescueUseCase: FuelRescueUseCase,
    private val originLatitude: Double?,
    private val originLongitude: Double?
) : Screen(carContext) {

    private var hasNavigatedToCritical = false

    init {
        if (originLatitude != null && originLongitude != null) {
            lifecycleScope.launch {
                fuelRescueUseCase.refresh(originLatitude, originLongitude, forceRefresh = false)
            }
        }
        lifecycleScope.launch {
            combine(
                fuelRescueUseCase.recommendations,
                fuelRescueUseCase.isRefreshing,
                fuelRescueUseCase.hasLoadedOnce
            ) { recommendations, refreshing, loadedOnce -> Triple(recommendations, refreshing, loadedOnce) }
                .collect { (recommendations, refreshing, loadedOnce) ->
                    val isCritical = loadedOnce && !refreshing &&
                        !FuelRescueEligibility.hasSafeRecommendation(recommendations)
                    if (isCritical && !hasNavigatedToCritical) {
                        hasNavigatedToCritical = true
                        val nearest = recommendations.minByOrNull {
                            it.station.routeDistanceMiles ?: it.station.distanceMiles
                        }
                        screenManager.push(CriticalFuelScreen(carContext, nearest))
                    } else {
                        invalidate()
                    }
                }
        }
    }

    override fun onGetTemplate(): Template {
        if (originLatitude == null || originLongitude == null) {
            return MessageTemplate.Builder(
                "Location unavailable. Fuel Rescue needs your current position to find nearby stations."
            )
                .setTitle("Find Fuel")
                .setHeaderAction(Action.BACK)
                .build()
        }

        val isRefreshing = fuelRescueUseCase.isRefreshing.value
        val hasLoadedOnce = fuelRescueUseCase.hasLoadedOnce.value
        val recommendations = fuelRescueUseCase.recommendations.value

        if (isRefreshing || !hasLoadedOnce || !FuelRescueEligibility.hasSafeRecommendation(recommendations)) {
            // Either still loading, or a critical-state hand-off is in flight (triggered
            // from the collector above) — show a loading placeholder rather than a
            // second copy of the critical message.
            return PlaceListMapTemplate.Builder()
                .setLoading(true)
                .setTitle("Find Fuel")
                .setHeaderAction(Action.BACK)
                .build()
        }

        val categories = fuelRescueUseCase.categories.value
        val displayed = selectDisplayedRecommendations(recommendations, categories)
        val itemList = buildFuelRescueItemList(displayed, categories) { recommendation ->
            screenManager.push(
                StationDetailScreen(
                    carContext = carContext,
                    recommendation = recommendation,
                    categories = categories[recommendation.station.id] ?: emptySet(),
                    warningForUnverifiedReachability = false
                )
            )
        }

        return PlaceListMapTemplate.Builder()
            .setItemList(itemList)
            .setTitle("Find Fuel")
            .setHeaderAction(Action.BACK)
            .build()
    }
}
