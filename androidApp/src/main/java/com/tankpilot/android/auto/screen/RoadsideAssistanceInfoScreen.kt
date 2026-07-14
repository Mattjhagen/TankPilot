package com.tankpilot.android.auto.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

/**
 * TankPilot has no roadside-assistance integration or dispatch capability, so this
 * only hands the driver off to their own phone/vehicle calling feature rather than
 * implying it can connect them to help itself.
 */
class RoadsideAssistanceInfoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return MessageTemplate.Builder(
            "Contact roadside assistance using your phone or vehicle's built-in calling feature."
        )
            .setTitle("Roadside Assistance")
            .setHeaderAction(Action.BACK)
            .build()
    }
}
