package com.tankpilot.android.auto.screen

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Android Auto root screen. Shows only production-derived (or, in debug builds
 * with no vehicle configured yet, fixture-derived) fuel status — no telemetry or
 * OBD dependency, so it works with no adapter connected and fully offline.
 */
class TankPilotCarHomeScreen(
    carContext: CarContext,
    private val fuelStateUseCase: FuelStateUseCase,
    private val fuelRescueUseCase: FuelRescueUseCase,
    private val carFuelPreviewProvider: CarFuelPreviewProvider,
    private val carLocationSource: CarLocationSource
) : Screen(carContext) {

    init {
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
    }

    override fun onGetTemplate(): Template {
        val snapshot = buildCarFuelSnapshot(fuelStateUseCase, carFuelPreviewProvider)
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
        return PaneTemplate.Builder(pane)
            .setTitle(if (snapshot.isPreviewFixture) "TankPilot (Preview)" else "TankPilot")
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}
