package com.tankpilot.dashboard.domain

import com.tankpilot.telemetry.domain.TelemetryData
import kotlin.test.*
import kotlinx.datetime.Clock

class SpeedSmootherTest {

    @Test
    fun `test speed smoother handles normal progression`() {
        val smoother = SpeedSmoother(smoothingFactor = 0.3)
        
        // Initial value is accepted immediately
        var display = smoother.filter(10.0, SpeedSource.OBD)
        assertEquals(10, display.speedKmh)
        assertEquals(SpeedSource.OBD, display.source)

        // Small change is smoothed
        display = smoother.filter(12.0, SpeedSource.OBD)
        assertTrue(display.speedKmh!! < 12) // Should be around 10 + 0.3 * 2 = 10.6 -> 10
        
        // Large change is accepted immediately
        display = smoother.filter(40.0, SpeedSource.OBD)
        assertEquals(40, display.speedKmh)
    }

    @Test
    fun `test zero speed is immediate`() {
        val smoother = SpeedSmoother()
        
        smoother.filter(30.0, SpeedSource.OBD)
        val display = smoother.filter(0.0, SpeedSource.OBD)
        assertEquals(0, display.speedKmh)
    }
}
