package com.tankpilot.fuelrescue.data

import com.tankpilot.fuelrescue.domain.FuelRescueScenarioOverride
import com.tankpilot.fuelrescue.domain.FuelRescueScenarioOverrideProvider

/** Release binding — never overrides real fuel data with a fixture. */
class NoOpFuelRescueScenarioOverrideProvider : FuelRescueScenarioOverrideProvider {
    override fun overrideOrNull(): FuelRescueScenarioOverride? = null
}
