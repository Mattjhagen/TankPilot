package com.tankpilot.android.auto

import android.content.Intent
import android.util.Log
import androidx.car.app.Screen
import androidx.car.app.Session
import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.android.auto.screen.TankPilotCarHomeScreen
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import com.tankpilot.trip.domain.DrivingSessionCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TAG = "TankPilotAuto"

/**
 * Android Auto session. Resolves the same Koin graph TankPilotApplication starts
 * at process launch — no separate DI setup needed for the car entry point.
 *
 * Temporary diagnostic logging (Phase 3A.5) — see TankPilotCarAppService. Also logs
 * whether Koin injection itself succeeds, since a NoBeanDefFoundException thrown from
 * the `by inject()` delegates below would happen lazily on first property access, not
 * at construction, and would otherwise be an unlogged, silent-looking crash from the
 * host's perspective.
 */
class TankPilotCarSession : Session(), KoinComponent {

    private val fuelStateUseCase: FuelStateUseCase by inject()
    private val fuelRescueUseCase: FuelRescueUseCase by inject()
    private val carFuelPreviewProvider: CarFuelPreviewProvider by inject()
    private val carLocationSource: CarLocationSource by inject()
    private val drivingSessionCoordinator: DrivingSessionCoordinator by inject()

    init {
        Log.d(TAG, "TankPilotCarSession constructed")
    }

    override fun onCreateScreen(intent: Intent): Screen {
        Log.d(TAG, "onCreateScreen() called, intent=$intent")
        return try {
            val screen = TankPilotCarHomeScreen(
                carContext = carContext,
                fuelStateUseCase = fuelStateUseCase,
                fuelRescueUseCase = fuelRescueUseCase,
                carFuelPreviewProvider = carFuelPreviewProvider,
                carLocationSource = carLocationSource,
                drivingSessionCoordinator = drivingSessionCoordinator
            )
            Log.d(TAG, "onCreateScreen() returning TankPilotCarHomeScreen successfully")
            screen
        } catch (t: Throwable) {
            Log.e(TAG, "onCreateScreen() failed while constructing the root screen", t)
            throw t
        }
    }
}
