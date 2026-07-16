package com.tankpilot.android.auto.model

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.FuelStatus
import com.tankpilot.testsupport.MockConfidenceScenario
import com.tankpilot.testsupport.MockFuelScenario
import com.tankpilot.testsupport.MockStationScenario
import com.tankpilot.testsupport.TestFixtures

/**
 * Debug-only Android Auto fuel-status fixture for DHU scenario testing (phases/phase
 * -03a-android-auto-foundation.md, Phase 3A.3). Inert (returns null, so production
 * data or the real "no vehicle configured" unavailable state shows through) unless a
 * Test Lab scenario has explicitly set TestFixtures.androidAutoScenarioOverrideEnabled.
 * Never compiled into release — see
 * androidApp/src/release/.../auto/model/ReleaseCarFuelPreviewProvider.kt.
 */
class DebugCarFuelPreviewProvider : CarFuelPreviewProvider {
    override fun previewSnapshot(): CarFuelSnapshot? {
        if (!TestFixtures.androidAutoScenarioOverrideEnabled.value) return null

        if (TestFixtures.fuelScenario.value == MockFuelScenario.NO_ESTIMATE) {
            return CarFuelSnapshot(
                vehicleLabel = null,
                fuelPercent = null,
                gallonsRemaining = null,
                conservativeRangeMiles = null,
                expectedRangeMiles = null,
                confidencePercent = null,
                confidenceLevel = null,
                fuelStatus = FuelStatus.UNKNOWN,
                reachableStationCount = null,
                drivingPattern = null,
                mpgValue = null,
                mpgSource = null,
                alertsText = null,
                isTrackingActive = false,
                isLocationUnavailable = true,
                isPreviewFixture = true
            )
        }

        val (percent, gallons, rangeMiles) = when (TestFixtures.fuelScenario.value) {
            MockFuelScenario.FULL_TANK -> Triple(96, 15.4, 410.0)
            MockFuelScenario.HALF_TANK -> Triple(50, 8.0, 210.0)
            MockFuelScenario.LOW_FUEL -> Triple(18, 2.9, 70.0)
            MockFuelScenario.CRITICAL_FUEL -> Triple(6, 0.9, 20.0)
            MockFuelScenario.NO_ESTIMATE -> Triple(0, 0.0, 0.0) // unreachable, handled above
        }

        val confidenceLevel = when (TestFixtures.confidenceScenario.value) {
            MockConfidenceScenario.VERY_HIGH -> ConfidenceLevel.VERY_HIGH
            MockConfidenceScenario.HIGH -> ConfidenceLevel.HIGH
            MockConfidenceScenario.MEDIUM -> ConfidenceLevel.MEDIUM
            MockConfidenceScenario.LOW -> ConfidenceLevel.LOW
        }
        val confidencePercent = when (confidenceLevel) {
            ConfidenceLevel.VERY_HIGH -> 96
            ConfidenceLevel.HIGH -> 88
            ConfidenceLevel.MEDIUM -> 72
            ConfidenceLevel.LOW -> 45
        }

        val fuelStatus = when (TestFixtures.fuelScenario.value) {
            MockFuelScenario.CRITICAL_FUEL -> FuelStatus.CRITICAL
            MockFuelScenario.LOW_FUEL -> FuelStatus.LOW
            else -> FuelStatus.NORMAL
        }

        val reachableStationCount = when (TestFixtures.stationScenario.value) {
            MockStationScenario.NO_SAFE_REACHABLE -> 0
            MockStationScenario.OFFLINE -> null
            MockStationScenario.NORMAL,
            MockStationScenario.MISSING_PRICES,
            MockStationScenario.STALE_PRICES,
            MockStationScenario.INVALID_COORDINATES -> 3
        }

        return CarFuelSnapshot(
            vehicleLabel = "Preview Vehicle",
            fuelPercent = percent,
            gallonsRemaining = gallons,
            conservativeRangeMiles = rangeMiles,
            expectedRangeMiles = rangeMiles * 1.1,
            confidencePercent = confidencePercent,
            confidenceLevel = confidenceLevel,
            fuelStatus = fuelStatus,
            reachableStationCount = reachableStationCount,
            drivingPattern = "City",
            mpgValue = 24.5,
            mpgSource = "GPS Est.",
            alertsText = if (fuelStatus == FuelStatus.CRITICAL) "Critical Fuel Warning" else null,
            isTrackingActive = true,
            isLocationUnavailable = false,
            isPreviewFixture = true
        )
    }
}
