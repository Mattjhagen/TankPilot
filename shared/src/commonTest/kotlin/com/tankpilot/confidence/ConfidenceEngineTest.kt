package com.tankpilot.confidence

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.trip.domain.DrivingType
import com.tankpilot.trip.domain.Trip
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfidenceEngineTest {

    @Test
    fun testEmptyLogsReturnsLow() {
        val confidence = ConfidenceEngine.calculateConfidence(
            fillUps = emptyList(),
            trips = emptyList()
        )
        assertEquals(ConfidenceLevel.LOW, confidence)
    }

    @Test
    fun testVeryHighConfidenceFlow() {
        // Prepare data that yields max score:
        // - Last fill-up was full and <= 3 days ago: 30 pts
        // - 3 consecutive full fill-ups: 20 pts
        // - Odometer ratio is 100%: 20 pts
        // - Active trips without speed anomalies: 30 pts
        // Total score = 100 pts -> VERY_HIGH
        val now = 1000000000L
        val fillUps = listOf(
            FillUp("f1", "v1", now - 10000, 10.0, 30.0, 1000.0, true),
            FillUp("f2", "v1", now - 100000, 12.0, 35.0, 800.0, true),
            FillUp("f3", "v1", now - 200000, 11.0, 32.0, 600.0, true)
        )
        val trips = listOf(
            Trip("t1", "v1", now - 5000, 20.0, 1200L, 60L, 60.0, DrivingType.HIGHWAY, 1.0)
        )

        val confidence = ConfidenceEngine.calculateConfidence(
            fillUps = fillUps,
            trips = trips,
            currentTimeMs = now
        )
        assertEquals(ConfidenceLevel.VERY_HIGH, confidence)
    }

    @Test
    fun testPartialFillupsReduceConfidence() {
        // Last fill-up is partial and 5 days ago: 10 pts
        // 0 consecutive full fills: 0 pts
        // No odometer provided: 0 pts
        // No trips (baseline 10 pts)
        // Total score = 20 pts -> LOW
        val now = 1000000000L
        val fillUps = listOf(
            FillUp("f1", "v1", now - 5 * 24 * 60 * 60 * 1000L, 5.0, 15.0, null, false)
        )

        val confidence = ConfidenceEngine.calculateConfidence(
            fillUps = fillUps,
            trips = emptyList(),
            currentTimeMs = now
        )
        assertEquals(ConfidenceLevel.LOW, confidence)
    }
}
