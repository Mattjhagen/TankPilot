package com.tankpilot.android.auto.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.tankpilot.android.auto.mapper.buildFuelRescueItemList
import com.tankpilot.android.auto.mapper.isRootCritical
import com.tankpilot.android.auto.mapper.selectRootDisplayRecommendations
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Android Auto root screen (category POI). Shows a genuine nearby-fuel-station list —
 * not a stats dashboard — via PlaceListMapTemplate, per Google's POI-category
 * expectations for the app's root/home screen. Absorbs the fetch/render/critical-handoff
 * responsibility previously in FuelRescueRecommendationsScreen (deleted; folded in here).
 *
 * TankPilotCarHomeScreen (the trip/fuel status Pane) is kept in the codebase but
 * deliberately unlinked from this release — Phase A proves POI visibility only; linking
 * it back in as a secondary screen is Phase B.
 *
 * Never starts location tracking or a foreground service — only observes state a
 * phone-initiated Start Drive / vehicle setup already produced.
 */
class TankPilotCarRootScreen(
    carContext: CarContext,
    private val fuelStateUseCase: FuelStateUseCase,
    private val fuelRescueUseCase: FuelRescueUseCase,
    private val carLocationSource: CarLocationSource
) : Screen(carContext) {

    private var hasNavigatedToCritical = false

    init {
        val origin = carLocationSource.currentLocationOrNull()
        if (origin != null) {
            lifecycleScope.launch {
                fuelRescueUseCase.refresh(origin.latitude, origin.longitude, forceRefresh = false)
            }
        }
        lifecycleScope.launch {
            combine(
                fuelRescueUseCase.recommendations,
                fuelRescueUseCase.isRefreshing,
                fuelRescueUseCase.hasLoadedOnce
            ) { recommendations, refreshing, loadedOnce -> Triple(recommendations, refreshing, loadedOnce) }
                .collect { (recommendations, refreshing, loadedOnce) ->
                    val isCritical = loadedOnce && !refreshing && isRootCritical(recommendations)
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

    private fun hasLocationPermission(): Boolean {
        return carContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            carContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    }

    override fun onGetTemplate(): Template {
        if (fuelStateUseCase.currentVehicle.value == null) {
            return messageTemplate("Set up a vehicle in TankPilot on your phone to see nearby fuel stations.")
        }

        if (!hasLocationPermission()) {
            return messageTemplate(
                "Location access is needed to find nearby fuel stations. Grant location " +
                    "permission to TankPilot on your phone."
            )
        }

        val origin = carLocationSource.currentLocationOrNull()
            ?: return messageTemplate("Waiting for your current location to find nearby fuel stations.")

        val isRefreshing = fuelRescueUseCase.isRefreshing.value
        val hasLoadedOnce = fuelRescueUseCase.hasLoadedOnce.value
        val recommendations = fuelRescueUseCase.recommendations.value

        if (isRefreshing || !hasLoadedOnce || isRootCritical(recommendations)) {
            // Either still loading, or a critical-state hand-off is in flight (triggered
            // from the collector above) — show a loading placeholder rather than a
            // second copy of the critical message.
            return PlaceListMapTemplate.Builder()
                .setLoading(true)
                .setTitle("TankPilot Fuel")
                .setHeaderAction(Action.APP_ICON)
                .setOnContentRefreshListener { onRefreshRequested(origin.latitude, origin.longitude) }
                .build()
        }

        val categories = fuelRescueUseCase.categories.value
        val displayed = selectRootDisplayRecommendations(recommendations, categories)
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
            .setTitle("TankPilot Fuel")
            .setHeaderAction(Action.APP_ICON)
            .setOnContentRefreshListener { onRefreshRequested(origin.latitude, origin.longitude) }
            .build()
    }

    /**
     * Kicks off the async refresh and invalidates immediately so the host clears the
     * stale template right away; the init{} collector above invalidates again once the
     * refreshed StateFlow state actually lands — the listener itself does not assume
     * its own call redraws the template.
     */
    private fun onRefreshRequested(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            fuelRescueUseCase.refresh(latitude, longitude, forceRefresh = true)
        }
        invalidate()
    }

    private fun messageTemplate(message: String): Template =
        MessageTemplate.Builder(message)
            .setTitle("TankPilot Fuel")
            .setHeaderAction(Action.APP_ICON)
            .build()
}
