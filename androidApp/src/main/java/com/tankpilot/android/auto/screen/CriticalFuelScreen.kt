package com.tankpilot.android.auto.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation

/**
 * Critical No-Safe-Station state (MessageTemplate). Reached only when
 * FuelRescueEligibility.hasSafeRecommendation is false — never claims a station is
 * reachable. Actions are limited to what TankPilot can actually support: Roadside
 * Assistance (informational hand-off, not a guaranteed connection), Open Nearest
 * Station (explicitly labeled unconfirmed), and Return via the header back action.
 * MessageTemplate supports at most 2 body actions, which is why Return isn't a third
 * button here.
 */
class CriticalFuelScreen(
    carContext: CarContext,
    private val nearestStation: FuelStationRecommendation?
) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val builder = MessageTemplate.Builder("No station is safely within the current estimate.")
            .setTitle("Critical Fuel Estimate")
            .setHeaderAction(Action.BACK)
            .addAction(
                Action.Builder()
                    .setTitle("Roadside Assistance")
                    .setOnClickListener {
                        screenManager.push(RoadsideAssistanceInfoScreen(carContext))
                    }
                    .build()
            )

        nearestStation?.let { station ->
            builder.addAction(
                Action.Builder()
                    .setTitle("Open Nearest Station (Unconfirmed)")
                    .setOnClickListener {
                        screenManager.push(
                            StationDetailScreen(
                                carContext = carContext,
                                recommendation = station,
                                categories = emptySet(),
                                warningForUnverifiedReachability = true
                            )
                        )
                    }
                    .build()
            )
        }

        return builder.build()
    }
}
