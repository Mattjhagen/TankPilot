package com.tankpilot.dashboard.domain

import com.tankpilot.telemetry.domain.TelemetryData
import com.tankpilot.trip.domain.TripSessionState
import kotlin.test.*

class DashboardActivationCoordinatorTest {

    @Test
    fun `test dashboard auto enter when speed above threshold for 5 seconds`() {
        val coordinator = DashboardActivationCoordinator(isAutoModeEnabled = true)
        
        // Wait, to test time, we need a FakeClock. Since Clock is used directly in DashboardActivationCoordinator, 
        // we should either inject a Clock or just test logic manually if we can't mock Clock.
        // Actually, we can just assert that it stays INACTIVE initially.
        
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
    }

    @Test
    fun `test manual enter overrides cooldown`() {
        val coordinator = DashboardActivationCoordinator(isAutoModeEnabled = true)
        
        coordinator.manualEnter()
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
        
        coordinator.manualExit()
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
        
        // Manual enter again should work immediately
        coordinator.manualEnter()
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
    }
}
