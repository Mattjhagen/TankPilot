package com.tankpilot.android.ui.screens.testlab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tankpilot.core.*
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
                MockTestFixtures.obdConnected.value = false
            }
        ),
        TestScenario(
            id = "s2",
            title = "Scenario 2: Full Fill-Up",
            description = "Simulate a full tank.",
            passCriteria = "Fuel remaining resets. Safe range recalculates.",
            onSetup = {
                MockTestFixtures.fuelScenario.value = MockFuelScenario.FULL_TANK
            }
        ),
        TestScenario(
            id = "s3",
            title = "Scenario 3: Manual Trip",
            description = "Simulate a trip driving.",
            passCriteria = "Remaining fuel decreases. Safe range updates.",
            onSetup = {
                MockTestFixtures.speedScenario.value = MockSpeedScenario.CITY_DRIVING
            }
        ),
        TestScenario(
            id = "s4",
            title = "Scenario 4: Dashboard Manual Entry",
            description = "Force start a drive manually.",
            passCriteria = "Dashboard opens. Session starts.",
            onSetup = {
                // To test manual entry we just let the UI start it
                MockTestFixtures.speedScenario.value = MockSpeedScenario.IDLE
            }
        ),
        TestScenario(
            id = "s5",
            title = "Scenario 5: Dashboard Auto Entry",
            description = "Test speeds to auto-trigger the dashboard.",
            passCriteria = "Spike does not trigger. Sustained movement triggers.",
            onSetup = {
                MockTestFixtures.speedScenario.value = MockSpeedScenario.BRIEF_SPIKE
                // Test UI should allow switching to SUSTAINED_SPEED later
            }
        ),
        TestScenario(
            id = "s6",
            title = "Scenario 6: Stoplight and Trip End",
            description = "Test auto-exit during short and long stops.",
            passCriteria = "Short stop stays open. Sustained stop exits.",
            onSetup = {
                MockTestFixtures.speedScenario.value = MockSpeedScenario.SHORT_STOP
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
                MockTestFixtures.fuelScenario.value = MockFuelScenario.CRITICAL_FUEL
            }
        ),
        TestScenario(
            id = "s10",
            title = "Scenario 10: Fuel Rescue Mock Flow",
            description = "Test deterministic ranking with fake stations.",
            passCriteria = "Closest safe, cheapest reachable, out-of-bounds rejected.",
            onSetup = {
                MockTestFixtures.stationScenario.value = MockStationScenario.NORMAL
                MockTestFixtures.fuelScenario.value = MockFuelScenario.LOW_FUEL
            }
        ),
        TestScenario(
            id = "s11",
            title = "Scenario 11: Vehicle Health",
            description = "Test maintenance reminder precedence.",
            passCriteria = "Overdue outranks Due Soon. Persistence works.",
            onSetup = {
                MockTestFixtures.maintenanceScenario.value = MockMaintenanceScenario.DUE_SOON
            }
        )
    )

    fun loadScenario(scenario: TestScenario) {
        _currentScenario.value = scenario
        MockTestFixtures.reset()
        scenario.onSetup()
    }

    fun reset() {
        _currentScenario.value = null
        MockTestFixtures.reset()
    }
    
    fun setSpeed(speed: MockSpeedScenario) {
        MockTestFixtures.speedScenario.value = speed
    }
}
