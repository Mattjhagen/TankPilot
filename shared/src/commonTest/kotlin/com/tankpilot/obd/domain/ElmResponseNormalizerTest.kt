package com.tankpilot.obd.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class ElmResponseNormalizerTest {

    @Test
    fun testNormalizerRemovesSpacesAndPrompts() {
        val raw = "41 00 BE 1F A8 13 \r\r>"
        val result = ElmResponseNormalizer.normalize(raw, "0100")
        assertEquals(listOf("4100BE1FA813"), result)
    }

    @Test
    fun testNormalizerRemovesEcho() {
        val raw = "0100 \r41 00 BE 1F A8 13 \r\r>"
        val result = ElmResponseNormalizer.normalize(raw, "0100")
        assertEquals(listOf("4100BE1FA813"), result)
    }

    @Test
    fun testNormalizerIgnoresNoData() {
        val raw = "0100 \rNO DATA \r\r>"
        val result = ElmResponseNormalizer.normalize(raw, "0100")
        assertEquals(emptyList(), result)
    }
    
    @Test
    fun testNormalizerIgnoresSearching() {
        val raw = "SEARCHING...\r41 00 BE 1F A8 13 \r\r>"
        val result = ElmResponseNormalizer.normalize(raw, "0100")
        assertEquals(listOf("4100BE1FA813"), result)
    }
}
