package com.tankpilot.testsupport

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class MockSpeedScenario {
    IDLE,
    BRIEF_SPIKE,
    SUSTAINED_SPEED,
    CITY_DRIVING,
    HIGHWAY_DRIVING,
    SHORT_STOP,
    SUSTAINED_STOP
}

enum class MockFuelScenario {
    FULL_TANK,
    HALF_TANK,
    LOW_FUEL,
    CRITICAL_FUEL,
    NO_ESTIMATE
}

enum class MockConfidenceScenario {
    VERY_HIGH,
    HIGH,
    MEDIUM,
    LOW
}

enum class MockStationScenario {
    NORMAL,
    NO_SAFE_REACHABLE,
    MISSING_PRICES,
    STALE_PRICES,
    OFFLINE,
    INVALID_COORDINATES
}

enum class MockMaintenanceScenario {
    OK,
    DUE_SOON,
    OVERDUE
}

object TestFixtures {
    val speedScenario = MutableStateFlow(MockSpeedScenario.IDLE)
    val fuelScenario = MutableStateFlow(MockFuelScenario.HALF_TANK)
    val confidenceScenario = MutableStateFlow(MockConfidenceScenario.HIGH)
    val stationScenario = MutableStateFlow(MockStationScenario.NORMAL)
    val maintenanceScenario = MutableStateFlow(MockMaintenanceScenario.OK)
    val obdConnected = MutableStateFlow(true)
    val ambientTemperatureAvailable = MutableStateFlow(false)

    /**
     * Explicit opt-in gate for Android Auto DHU scenario testing (phases/phase-03a
     * -android-auto-foundation.md, Phase 3A.3). False by default so selecting a value
     * above has zero effect on normal debug behavior — the Android Auto fuel/station
     * override providers only activate once a Test Lab scenario sets this true, and
     * production/release code never reads it at all.
     */
    val androidAutoScenarioOverrideEnabled = MutableStateFlow(false)

    fun reset() {
        speedScenario.value = MockSpeedScenario.IDLE
        fuelScenario.value = MockFuelScenario.HALF_TANK
        confidenceScenario.value = MockConfidenceScenario.HIGH
        stationScenario.value = MockStationScenario.NORMAL
        maintenanceScenario.value = MockMaintenanceScenario.OK
        obdConnected.value = true
        ambientTemperatureAvailable.value = false
        androidAutoScenarioOverrideEnabled.value = false
    }
}
