package com.tankpilot.android.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.android.auto.screen.TankPilotCarHomeScreen
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android Auto session. Resolves the same Koin graph TankPilotApplication starts
 * at process launch — no separate DI setup needed for the car entry point.
 */
class TankPilotCarSession : Session(), KoinComponent {

    private val fuelStateUseCase: FuelStateUseCase by inject()
    private val fuelRescueUseCase: FuelRescueUseCase by inject()
    private val carFuelPreviewProvider: CarFuelPreviewProvider by inject()
    private val carLocationSource: CarLocationSource by inject()

    override fun onCreateScreen(intent: Intent): Screen {
        return TankPilotCarHomeScreen(
            carContext = carContext,
            fuelStateUseCase = fuelStateUseCase,
            fuelRescueUseCase = fuelRescueUseCase,
            carFuelPreviewProvider = carFuelPreviewProvider,
            carLocationSource = carLocationSource
        )
    }
}
