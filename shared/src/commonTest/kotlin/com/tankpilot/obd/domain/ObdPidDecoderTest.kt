package com.tankpilot.obd.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObdPidDecoderTest {

    @Test
    fun testDecodesSpeed() {
        val lines = listOf("410D32") // 32 hex = 50
        val result = ObdPidDecoder.decode(lines, ObdPid.VEHICLE_SPEED)
        assertEquals(50.0, result)
    }

    @Test
    fun testDecodesRpm() {
        val lines = listOf("410C1A72") // 1A72 = 6770 -> 6770/4 = 1692.5
        val result = ObdPidDecoder.decode(lines, ObdPid.ENGINE_RPM)
        assertEquals(1692.5, result)
    }

    @Test
    fun testDecodesCoolant() {
        val lines = listOf("410564") // 64 = 100 -> 100 - 40 = 60
        val result = ObdPidDecoder.decode(lines, ObdPid.COOLANT_TEMPERATURE)
        assertEquals(60.0, result)
    }

    @Test
    fun testDecodesVoltage() {
        val lines = listOf("12.6V")
        val result = ObdPidDecoder.decodeVoltage(lines)
        assertEquals(12.6, result)
        
        val lines2 = listOf(" 14.18 V ")
        val result2 = ObdPidDecoder.decodeVoltage(lines2)
        assertEquals(14.18, result2)
    }

    @Test
    fun testHandlesInvalidData() {
        val lines = listOf("410C1A") // too short for RPM
        val result = ObdPidDecoder.decode(lines, ObdPid.ENGINE_RPM)
        assertNull(result)
    }
}
