package com.tankpilot.telemetry.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull

class ObdParserTest {

    @Test
    fun testParseRpm() {
        // 1A F8 in hex is 6904 decimal. 6904 / 4 = 1726 RPM
        val rawResponse = "41 0C 1A F8"
        val rpm = ObdParser.parseRpm(rawResponse)
        assertNotNull(rpm)
        assertEquals(1726.0, rpm, 0.01)

        val noDataResponse = "41 0C NO DATA"
        assertNull(ObdParser.parseRpm(noDataResponse))
    }

    @Test
    fun testParseSpeed() {
        // 2D in hex is 45 decimal
        val rawResponse = "41 0D 2D"
        val speed = ObdParser.parseSpeed(rawResponse)
        assertNotNull(speed)
        assertEquals(45.0, speed, 0.01)
    }

    @Test
    fun testParseCoolantTemp() {
        // 5A in hex is 90 decimal. 90 - 40 = 50 C
        val rawResponse = "41 05 5A"
        val temp = ObdParser.parseCoolantTemp(rawResponse)
        assertNotNull(temp)
        assertEquals(50.0, temp, 0.01)
    }

    @Test
    fun testParseEngineLoad() {
        // 7F in hex is 127 decimal. 127 * 100 / 255 = 49.8%
        val rawResponse = "41 04 7F"
        val load = ObdParser.parseEngineLoad(rawResponse)
        assertNotNull(load)
        assertEquals(49.8, load, 0.1)
    }

    @Test
    fun testParseVin() {
        // Mock VIN response (17 characters ASCII)
        // 1G1JC54F73H123456 in hex
        // 1=31 G=47 1=31 J=4A C=43 5=35 4=34 F=46 7=37 3=33 H=48 1=31 2=32 3=33 4=34 5=35 6=36
        val rawResponse = "49 02 01 31 47 31 4A 43 35 34 46 37 33 48 31 32 33 34 35 36"
        val vin = ObdParser.parseVin(rawResponse)
        assertNotNull(vin)
        assertEquals("1G1JC54F73H123456", vin)
    }
}
