package com.tankpilot.telemetry.domain

import com.tankpilot.obd.domain.ObdConnectionState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SpeedArbitratorTest {

    @Test
    fun testGpsFallbackWhenObdStale() {
        val arbitrator = SpeedArbitrator()
        
        // Setup OBD valid
        arbitrator.onObdSpeedUpdate(50.0, 1000L, ObdConnectionState.VEHICLE_CONNECTED)
        arbitrator.onObdSpeedUpdate(55.0, 2000L, ObdConnectionState.VEHICLE_CONNECTED)
        
        // 2 consecutive OBD samples acquired, source should be OBD
        val state1 = arbitrator.evaluate(2500L, ObdConnectionState.VEHICLE_CONNECTED)
        assertEquals(TelemetrySource.OBD, state1.source)
        assertEquals(55.0, state1.speedKmh)
        
        // Provide GPS update at 3500L
        arbitrator.onGpsSpeedUpdate(60.0, 3500L)
        
        // At 4500L, OBD last timestamp was 2000L. Delta is 2500L (stale!)
        val state2 = arbitrator.evaluate(4500L, ObdConnectionState.VEHICLE_CONNECTED)
        assertEquals(TelemetrySource.GPS, state2.source)
        assertEquals(60.0, state2.speedKmh)
    }

    @Test
    fun testGpsFallbackOnDisconnect() {
        val arbitrator = SpeedArbitrator()
        
        arbitrator.onObdSpeedUpdate(50.0, 1000L, ObdConnectionState.VEHICLE_CONNECTED)
        arbitrator.onObdSpeedUpdate(55.0, 2000L, ObdConnectionState.VEHICLE_CONNECTED)
        
        val state1 = arbitrator.evaluate(2500L, ObdConnectionState.VEHICLE_CONNECTED)
        assertEquals(TelemetrySource.OBD, state1.source)
        
        // Disconnect
        val state2 = arbitrator.evaluate(2600L, ObdConnectionState.DISCONNECTED)
        assertEquals(TelemetrySource.GPS, state2.source)
        assertNull(state2.speedKmh) // no fresh GPS provided
    }

    @Test
    fun testRequiresTwoConsecutiveObdSamples() {
        val arbitrator = SpeedArbitrator()
        
        arbitrator.onGpsSpeedUpdate(30.0, 1000L)
        val state1 = arbitrator.evaluate(1500L, ObdConnectionState.VEHICLE_CONNECTED)
        assertEquals(TelemetrySource.GPS, state1.source)
        
        // First OBD sample
        val state2 = arbitrator.onObdSpeedUpdate(32.0, 2000L, ObdConnectionState.VEHICLE_CONNECTED)
        assertEquals(TelemetrySource.GPS, state2.source) // Still GPS
        
        // Second OBD sample
        val state3 = arbitrator.onObdSpeedUpdate(33.0, 3000L, ObdConnectionState.VEHICLE_CONNECTED)
        assertEquals(TelemetrySource.OBD, state3.source) // Switched to OBD
        assertEquals(33.0, state3.speedKmh)
    }

    @Test
    fun testRejectsImpossibleObdSpeed() {
        val arbitrator = SpeedArbitrator()
        
        arbitrator.onObdSpeedUpdate(50.0, 1000L, ObdConnectionState.VEHICLE_CONNECTED)
        // Impossible speed resets consecutive counter
        arbitrator.onObdSpeedUpdate(300.0, 2000L, ObdConnectionState.VEHICLE_CONNECTED)
        
        val state1 = arbitrator.evaluate(2500L, ObdConnectionState.VEHICLE_CONNECTED)
        assertEquals(TelemetrySource.GPS, state1.source)
    }
}
