package com.tankpilot.testsupport.fuelrescue.data

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.Gallons
import com.tankpilot.fuelrescue.domain.FuelRescueScenarioOverride
import com.tankpilot.fuelrescue.domain.FuelRescueScenarioOverrideProvider
import com.tankpilot.testsupport.MockConfidenceScenario
import com.tankpilot.testsupport.MockFuelScenario
import com.tankpilot.testsupport.TestFixtures

/**
 * Debug-only Fuel Rescue input override for DHU scenario testing (phases/phase-03a
 * -android-auto-foundation.md, Phase 3A.3). Inert unless a Test Lab scenario has set
 * TestFixtures.androidAutoScenarioOverrideEnabled — normal debug operation (real
 * vehicle/trip/fill-up data) is unaffected otherwise.
 */
class DebugFuelRescueScenarioOverrideProvider : FuelRescueScenarioOverrideProvider {
    override fun overrideOrNull(): FuelRescueScenarioOverride? {
        if (!TestFixtures.androidAutoScenarioOverrideEnabled.value) return null

        val remainingGallons = when (TestFixtures.fuelScenario.value) {
            MockFuelScenario.FULL_TANK -> 15.4
            MockFuelScenario.HALF_TANK -> 8.0
            MockFuelScenario.LOW_FUEL -> 2.9
            MockFuelScenario.CRITICAL_FUEL -> 0.9
            // "No estimate" has nothing for Fuel Rescue to override toward — that
            // scenario is validated on the Fuel Status (root) screen instead.
            MockFuelScenario.NO_ESTIMATE -> return null
        }

        val confidenceLevel = when (TestFixtures.confidenceScenario.value) {
            MockConfidenceScenario.VERY_HIGH -> ConfidenceLevel.VERY_HIGH
            MockConfidenceScenario.HIGH -> ConfidenceLevel.HIGH
            MockConfidenceScenario.MEDIUM -> ConfidenceLevel.MEDIUM
            MockConfidenceScenario.LOW -> ConfidenceLevel.LOW
        }

        return FuelRescueScenarioOverride(
            estimatedRemaining = Gallons(remainingGallons),
            confidenceLevel = confidenceLevel
        )
    }
}
