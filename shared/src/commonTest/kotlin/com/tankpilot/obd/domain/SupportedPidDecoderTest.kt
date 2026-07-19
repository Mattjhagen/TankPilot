package com.tankpilot.obd.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SupportedPidDecoderTest {

    @Test
    fun testDecodes0100Mask() {
        val lines = listOf("4100BE1FA813")
        val (supported, hasNext) = SupportedPidDecoder.decode(lines, "00")
        
        // BE1FA813 in binary:
        // B = 1011 -> PIDs 1, 3, 4 supported
        // 01 is PID 01, 04 is PID 04
        // Let's check some knowns
        assertTrue(supported.contains(ObdPid.ENGINE_LOAD)) // PID 04
        assertTrue(supported.contains(ObdPid.COOLANT_TEMPERATURE)) // PID 05
        assertTrue(supported.contains(ObdPid.ENGINE_RPM)) // PID 0C
        assertTrue(supported.contains(ObdPid.VEHICLE_SPEED)) // PID 0D
        
        // 13 -> 00010011 -> PID 31, 32 not supported, PID 20 supported? Wait, last bit is continuation
        // 13 is 00010011 -> PID 28 is 0, PID 29 is 0, PID 30 is 0, PID 31 is 1 (which is index 30), PID 32 is 1 (index 31).
        // If index 31 is 1, continuation is true!
        assertEquals(true, hasNext)
    }

    @Test
    fun testDecodesEmpty() {
        val lines = emptyList<String>()
        val (supported, hasNext) = SupportedPidDecoder.decode(lines, "00")
        assertTrue(supported.isEmpty())
        assertEquals(false, hasNext)
    }
}
