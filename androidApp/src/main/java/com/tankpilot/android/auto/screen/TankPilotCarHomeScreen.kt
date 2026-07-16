package com.tankpilot.android.auto.screen

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.tankpilot.android.auto.mapper.buildCarFuelSnapshot
import com.tankpilot.android.auto.mapper.toPane
import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import com.tankpilot.trip.domain.DrivingSessionCoordinator
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private const val TAG = "TankPilotAuto"

/**
 * Android Auto root screen. Shows only production-derived (or, in debug builds
 * with no vehicle configured yet, fixture-derived) fuel status — no telemetry or
 * OBD dependency, so it works with no adapter connected and fully offline.
 *
 * Temporary diagnostic logging (Phase 3A.5) — see TankPilotCarAppService.
 */
class TankPilotCarHomeScreen(
    carContext: CarContext,
    private val fuelStateUseCase: FuelStateUseCase,
    private val fuelRescueUseCase: FuelRescueUseCase,
    private val carFuelPreviewProvider: CarFuelPreviewProvider,
    private val carLocationSource: CarLocationSource,
    private val drivingSessionCoordinator: DrivingSessionCoordinator
) : Screen(carContext) {

    init {
        Log.d(TAG, "TankPilotCarHomeScreen constructed")
        lifecycleScope.launch {
            combine(
                fuelStateUseCase.currentVehicle,
                fuelStateUseCase.estimatedFuelRemaining,
                fuelStateUseCase.safeRange,
                fuelStateUseCase.confidence,
                fuelStateUseCase.confidencePercent
            ) { _, _, _, _, _ -> Unit }.collect {
                invalidate()
            }
        }
        lifecycleScope.launch {
            fuelRescueUseCase.reachableSafeStationCount.collect {
                invalidate()
            }
        }
        lifecycleScope.launch {
            drivingSessionCoordinator.sessionState.collect {
                invalidate()
            }
        }
        // Warm the reachable-station count for the glanceable "Fuel Rescue" row —
        // this is the same refresh() the Find Fuel action itself would trigger, just
        // fired proactively so the count is ready without an extra tap. No-ops
        // gracefully offline / with no vehicle / with an invalid coordinate (see
        // FuelRescueUseCase.refresh()).
        carLocationSource.currentLocationOrNull()?.let { origin ->
            lifecycleScope.launch {
                fuelRescueUseCase.refresh(origin.latitude, origin.longitude, forceRefresh = false)
            }
        }
    }

    override fun onGetTemplate(): Template {
        Log.d(TAG, "onGetTemplate() called")
        val snapshot = buildCarFuelSnapshot(fuelStateUseCase, fuelRescueUseCase, carFuelPreviewProvider, drivingSessionCoordinator.sessionState.value)
        val origin = carLocationSource.currentLocationOrNull()
        val pane = snapshot.toPane {
            screenManager.push(
                FuelRescueRecommendationsScreen(
                    carContext = carContext,
                    fuelRescueUseCase = fuelRescueUseCase,
                    originLatitude = origin?.latitude,
                    originLongitude = origin?.longitude
                )
            )
        }
        val template = PaneTemplate.Builder(pane)
            .setTitle(if (snapshot.isPreviewFixture) "TankPilot (Preview)" else "TankPilot")
            .setHeaderAction(Action.APP_ICON)
            .build()
        Log.d(TAG, "onGetTemplate() returning PaneTemplate successfully")
        return template
    }
}
