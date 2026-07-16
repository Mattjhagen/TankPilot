package com.tankpilot.android.auto

import android.content.Intent
import android.util.Log
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.android.auto.screen.TankPilotCarHomeScreen
import com.tankpilot.fuel.domain.FuelModelUseCase
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
 * Deliberately has no dependency on DrivingTrackingCoordinator or any LocationProvider:
 * Android Auto only ever observes the canonical DrivingSessionCoordinator/FuelModelUseCase
 * state a phone-initiated Start Drive produces. It must never start location tracking or
 * a foreground service itself from the background.
 *
 * Temporary diagnostic logging (Phase 3A.5) — see TankPilotCarAppService. Also logs
 * whether Koin injection itself succeeds, since a NoBeanDefFoundException thrown from
 * the `by inject()` delegates below would happen lazily on first property access, not
 * at construction, and would otherwise be an unlogged, silent-looking crash from the
 * host's perspective.
 */
class TankPilotCarSession : Session(), KoinComponent {

    private val fuelStateUseCase: FuelStateUseCase by inject()
    private val fuelModelUseCase: FuelModelUseCase by inject()
    private val fuelRescueUseCase: FuelRescueUseCase by inject()
    private val carFuelPreviewProvider: CarFuelPreviewProvider by inject()
    private val carLocationSource: CarLocationSource by inject()
    private val drivingSessionCoordinator: DrivingSessionCoordinator by inject()

    /**
     * Visibility observer only — logs and updates [AndroidAutoVisibilityState] (read by
     * the debug-only Test Lab diagnostics panel) when the car screen becomes visible/
     * hidden. It must never start tracking or a foreground service; Android Auto only
     * ever reads state a phone-initiated Start Drive already produced. Removes itself
     * on ON_DESTROY.
     */
    private val visibilityObserver: LifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                Log.d(TAG, "Android Auto screen became visible")
                AndroidAutoVisibilityState.setVisible(true)
            }
            Lifecycle.Event.ON_STOP -> {
                Log.d(TAG, "Android Auto screen no longer visible")
                AndroidAutoVisibilityState.setVisible(false)
            }
            Lifecycle.Event.ON_DESTROY -> {
                Log.d(TAG, "TankPilotCarSession destroyed — removing lifecycle observer")
                AndroidAutoVisibilityState.setVisible(false)
                lifecycle.removeObserver(visibilityObserver)
            }
            else -> {}
        }
    }

    init {
        Log.d(TAG, "TankPilotCarSession constructed")
        lifecycle.addObserver(visibilityObserver)
    }

    override fun onCreateScreen(intent: Intent): Screen {
        Log.d(TAG, "onCreateScreen() called, intent=$intent")
        return try {
            val screen = TankPilotCarHomeScreen(
                carContext = carContext,
                fuelStateUseCase = fuelStateUseCase,
                fuelModelUseCase = fuelModelUseCase,
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
