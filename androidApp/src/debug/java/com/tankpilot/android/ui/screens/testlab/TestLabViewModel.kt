package com.tankpilot.android.ui.screens.testlab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tankpilot.testsupport.TestFixtures
import com.tankpilot.testsupport.MockSpeedScenario
import com.tankpilot.testsupport.MockFuelScenario
import com.tankpilot.testsupport.MockStationScenario
import com.tankpilot.testsupport.MockConfidenceScenario
import com.tankpilot.testsupport.MockMaintenanceScenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TestScenario(
    val id: String,
    val title: String,
    val description: String,
    val passCriteria: String,
    val onSetup: () -> Unit
)

class TestLabViewModel : ViewModel() {

    private val _currentScenario = MutableStateFlow<TestScenario?>(null)
    val currentScenario: StateFlow<TestScenario?> = _currentScenario.asStateFlow()

    val scenarios = listOf(
        TestScenario(
            id = "s1",
            title = "Scenario 1: New Vehicle",
            description = "Setup a fresh vehicle without OBD.",
            passCriteria = "Dashboard shows unavailable live telemetry instead of zero.",
            onSetup = {
                TestFixtures.obdConnected.value = false
            }
        ),
        TestScenario(
            id = "s2",
            title = "Scenario 2: Full Fill-Up",
            description = "Simulate a full tank.",
            passCriteria = "Fuel remaining resets. Safe range recalculates.",
            onSetup = {
                TestFixtures.fuelScenario.value = MockFuelScenario.FULL_TANK
            }
        ),
        TestScenario(
            id = "s3",
            title = "Scenario 3: Manual Trip",
            description = "Simulate a trip driving.",
            passCriteria = "Remaining fuel decreases. Safe range updates.",
            onSetup = {
                TestFixtures.speedScenario.value = MockSpeedScenario.CITY_DRIVING
            }
        ),
        TestScenario(
            id = "s4",
            title = "Scenario 4: Dashboard Manual Entry",
            description = "Force start a drive manually.",
            passCriteria = "Dashboard opens. Session starts.",
            onSetup = {
                // To test manual entry we just let the UI start it
                TestFixtures.speedScenario.value = MockSpeedScenario.IDLE
            }
        ),
        TestScenario(
            id = "s5",
            title = "Scenario 5: Dashboard Auto Entry",
            description = "Test speeds to auto-trigger the dashboard.",
            passCriteria = "Spike does not trigger. Sustained movement triggers.",
            onSetup = {
                TestFixtures.speedScenario.value = MockSpeedScenario.BRIEF_SPIKE
                // Test UI should allow switching to SUSTAINED_SPEED later
            }
        ),
        TestScenario(
            id = "s6",
            title = "Scenario 6: Stoplight and Trip End",
            description = "Test auto-exit during short and long stops.",
            passCriteria = "Short stop stays open. Sustained stop exits.",
            onSetup = {
                TestFixtures.speedScenario.value = MockSpeedScenario.SHORT_STOP
            }
        ),
        TestScenario(
            id = "s7",
            title = "Scenario 7: Session Restoration",
            description = "Test recreating activity.",
            passCriteria = "Dashboard state restores. No duplicate timers.",
            onSetup = {}
        ),
        TestScenario(
            id = "s8",
            title = "Scenario 8: Wake Lock",
            description = "Test FLAG_KEEP_SCREEN_ON.",
            passCriteria = "Screen stays awake only when Dashboard is active.",
            onSetup = {}
        ),
        TestScenario(
            id = "s9",
            title = "Scenario 9: Haptics",
            description = "Test one-shot haptic feedbacks.",
            passCriteria = "Haptics fire exactly once per event.",
            onSetup = {
                TestFixtures.fuelScenario.value = MockFuelScenario.CRITICAL_FUEL
            }
        ),
        TestScenario(
            id = "s10",
            title = "Scenario 10: Fuel Rescue Mock Flow",
            description = "Test deterministic ranking with fake stations.",
            passCriteria = "Closest safe, cheapest reachable, out-of-bounds rejected.",
            onSetup = {
                TestFixtures.stationScenario.value = MockStationScenario.NORMAL
                TestFixtures.fuelScenario.value = MockFuelScenario.LOW_FUEL
            }
        ),
        TestScenario(
            id = "s11",
            title = "Scenario 11: Vehicle Health",
            description = "Test maintenance reminder precedence.",
            passCriteria = "Overdue outranks Due Soon. Persistence works.",
            onSetup = {
                TestFixtures.maintenanceScenario.value = MockMaintenanceScenario.DUE_SOON
            }
        )
    )

    /**
     * Android Auto DHU validation scenarios (phases/phase-03a-android-auto
     * -foundation.md, Phase 3A.3). Each sets
     * TestFixtures.androidAutoScenarioOverrideEnabled so the car's Fuel Status and
     * Fuel Rescue screens reflect the fixture instead of production data — no separate
     * in-car selector exists, this phone-side control is the only place these are set.
     */
    val androidAutoScenarios = listOf(
        TestScenario(
            id = "aa1",
            title = "AA1: Normal Fuel",
            description = "Comfortable fuel level, nearby stations all safely reachable.",
            passCriteria = "Root screen shows Normal status. Fuel Rescue lists Best Overall / Closest Safe / Cheapest Reachable.",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.HALF_TANK
                TestFixtures.stationScenario.value = MockStationScenario.NORMAL
                TestFixtures.confidenceScenario.value = MockConfidenceScenario.HIGH
            }
        ),
        TestScenario(
            id = "aa2",
            title = "AA2: Low Fuel",
            description = "Low-fuel threshold crossed, stations still safely reachable.",
            passCriteria = "Root screen shows Low Fuel status.",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.LOW_FUEL
                TestFixtures.stationScenario.value = MockStationScenario.NORMAL
                TestFixtures.confidenceScenario.value = MockConfidenceScenario.HIGH
            }
        ),
        TestScenario(
            id = "aa3",
            title = "AA3: Critical Fuel — Safe Stations",
            description = "Critically low fuel, but nearby stations remain safely reachable.",
            passCriteria = "Root screen shows Critical status. Fuel Rescue still lists safe recommendations, not the critical screen.",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.CRITICAL_FUEL
                TestFixtures.stationScenario.value = MockStationScenario.NORMAL
                TestFixtures.confidenceScenario.value = MockConfidenceScenario.HIGH
            }
        ),
        TestScenario(
            id = "aa4",
            title = "AA4: Critical Fuel — No Safe Station",
            description = "Critically low fuel and every candidate station is out of safe range.",
            passCriteria = "Fuel Rescue hands off to the Critical Fuel screen: \"No station is safely within the current estimate.\"",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.CRITICAL_FUEL
                TestFixtures.stationScenario.value = MockStationScenario.NO_SAFE_REACHABLE
                TestFixtures.confidenceScenario.value = MockConfidenceScenario.HIGH
            }
        ),
        TestScenario(
            id = "aa5",
            title = "AA5: Missing Prices",
            description = "Candidate stations have no price data.",
            passCriteria = "Rows show \"Price Unavailable\", never $0.00. A priceless station may still be Closest Safe, never Cheapest Reachable.",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.HALF_TANK
                TestFixtures.stationScenario.value = MockStationScenario.MISSING_PRICES
                TestFixtures.confidenceScenario.value = MockConfidenceScenario.HIGH
            }
        ),
        TestScenario(
            id = "aa6",
            title = "AA6: Stale Cached Prices",
            description = "Candidate stations only have prices older than 24 hours.",
            passCriteria = "Rows and Station Detail show a stale/outdated freshness label alongside the price, not silently as if current.",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.HALF_TANK
                TestFixtures.stationScenario.value = MockStationScenario.STALE_PRICES
                TestFixtures.confidenceScenario.value = MockConfidenceScenario.HIGH
            }
        ),
        TestScenario(
            id = "aa7",
            title = "AA7: Offline Station Data",
            description = "Simulated station-provider failure (no network).",
            passCriteria = "Fuel Rescue shows no stations without crashing; Fuel Status still works (offline doesn't affect local fuel data).",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.HALF_TANK
                TestFixtures.stationScenario.value = MockStationScenario.OFFLINE
                TestFixtures.confidenceScenario.value = MockConfidenceScenario.HIGH
            }
        ),
        TestScenario(
            id = "aa8",
            title = "AA8: No Fuel Estimate",
            description = "Fuel estimate itself is unavailable.",
            passCriteria = "Root screen shows \"Unavailable\" for every fuel field — never 0% or 0 gal.",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.NO_ESTIMATE
            }
        ),
        TestScenario(
            id = "aa9",
            title = "AA9: Invalid Station Coordinates",
            description = "Candidate stations report NaN latitude/longitude.",
            passCriteria = "Station Detail omits the Navigate action entirely for these stations rather than offering a broken one.",
            onSetup = {
                TestFixtures.androidAutoScenarioOverrideEnabled.value = true
                TestFixtures.fuelScenario.value = MockFuelScenario.HALF_TANK
                TestFixtures.stationScenario.value = MockStationScenario.INVALID_COORDINATES
                TestFixtures.confidenceScenario.value = MockConfidenceScenario.HIGH
            }
        )
    )

    fun loadScenario(scenario: TestScenario) {
        _currentScenario.value = scenario
        TestFixtures.reset()
        scenario.onSetup()
    }

    fun reset() {
        _currentScenario.value = null
        TestFixtures.reset()
    }
    
    fun setSpeed(speed: MockSpeedScenario) {
        TestFixtures.speedScenario.value = speed
    }
}
