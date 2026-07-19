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
import com.tankpilot.fuel.domain.FuelModelUseCase
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
 * Reads the same canonical runtime state as the phone Dashboard: [FuelModelUseCase]
 * for displayed fuel/range/status/alert text and [DrivingSessionCoordinator] for
 * speed/pattern/MPG/tracking state — nothing here recalculates that math. This screen
 * never starts location tracking or a foreground service; it only observes state a
 * phone-initiated Start Drive already produces.
 *
 * Temporary diagnostic logging (Phase 3A.5) — see TankPilotCarAppService.
 */
class TankPilotCarHomeScreen(
    carContext: CarContext,
    private val fuelStateUseCase: FuelStateUseCase,
    private val fuelModelUseCase: FuelModelUseCase,
    private val fuelRescueUseCase: FuelRescueUseCase,
    private val carFuelPreviewProvider: CarFuelPreviewProvider,
    private val carLocationSource: CarLocationSource,
    private val drivingSessionCoordinator: DrivingSessionCoordinator
) : Screen(carContext) {

    init {
        Log.d(TAG, "TankPilotCarHomeScreen constructed")
        // A single combined subscription, not one invalidate() per source flow — none
        // of these flows are themselves written to by invalidate()/onGetTemplate(), so
        // this settles once per real state change rather than looping.
        lifecycleScope.launch {
            combine(
                fuelStateUseCase.currentVehicle,
                fuelStateUseCase.confidence,
                fuelStateUseCase.confidencePercent,
                fuelModelUseCase.displayedFuelRemainingGallons,
                fuelModelUseCase.displayedFuelPercent,
                fuelModelUseCase.conservativeRangeMiles,
                fuelModelUseCase.expectedRangeMiles,
                fuelModelUseCase.fuelStatus,
                fuelModelUseCase.warningText,
                fuelRescueUseCase.reachableSafeStationCount,
                drivingSessionCoordinator.sessionState
            ) { }.collect {
                Log.d(TAG, "Canonical state changed — invalidating template")
                invalidate()
            }
        }
        // Warm the reachable-station count for the glanceable "Fuel Rescue" row —
        // this is the same refresh() the Find Fuel action itself would trigger, just
        // fired proactively so the count is ready without an extra tap. A one-time
        // kickoff, not a repeating poll. No-ops gracefully offline / with no vehicle /
        // with an invalid coordinate (see FuelRescueUseCase.refresh()).
        carLocationSource.currentLocationOrNull()?.let { origin ->
            lifecycleScope.launch {
                fuelRescueUseCase.refresh(origin.latitude, origin.longitude, forceRefresh = false)
            }
        }
    }

    override fun onGetTemplate(): Template {
        Log.d(TAG, "onGetTemplate() called")
        val snapshot = buildCarFuelSnapshot(
            fuelStateUseCase = fuelStateUseCase,
            fuelModelUseCase = fuelModelUseCase,
            fuelRescueUseCase = fuelRescueUseCase,
            carFuelPreviewProvider = carFuelPreviewProvider,
            carLocationSource = carLocationSource,
            sessionState = drivingSessionCoordinator.sessionState.value
        )
        val pane = snapshot.toPane {
            screenManager.push(
                TankPilotCarRootScreen(
                    carContext = carContext,
                    fuelStateUseCase = fuelStateUseCase,
                    fuelRescueUseCase = fuelRescueUseCase,
                    carLocationSource = carLocationSource
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
