package com.tankpilot.trip.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class DrivingClassifierTest {

    @Test
    fun testInsufficientSamplesEmitsMixed() {
        val classifier = DrivingClassifier(minSamples = 10)
        // Feed 5 high-speed samples
        for (i in 0 until 5) {
            classifier.onSpeedUpdate(100.0, timestampMs = i * 1000L)
        }
        assertEquals(DrivingType.MIXED, classifier.drivingType.value)
    }

    @Test
    fun testSustainedHighSpeedClassifiesAsHighway() {
        val classifier = DrivingClassifier(minSamples = 10)
        // Feed 15 high speed samples (100 km/h) at 5-second intervals to span 70 seconds (> 60s sustained highway threshold)
        for (i in 0 until 15) {
            classifier.onSpeedUpdate(100.0, timestampMs = i * 5000L)
        }
        assertEquals(DrivingType.HIGHWAY, classifier.drivingType.value)
    }

    @Test
    fun testStopAndGoClassifiesAsCity() {
        val classifier = DrivingClassifier(minSamples = 10)
        // Feed low speed samples (15 km/h)
        for (i in 0 until 15) {
            classifier.onSpeedUpdate(15.0, timestampMs = i * 1000L)
        }
        assertEquals(DrivingType.CITY, classifier.drivingType.value)
    }

    @Test
    fun testOneSpeedSpikeDoesNotFlipToHighway() {
        val classifier = DrivingClassifier(minSamples = 10)
        // Start with stop-and-go (city) pattern
        for (i in 0 until 10) {
            classifier.onSpeedUpdate(10.0, timestampMs = i * 1000L)
        }
        assertEquals(DrivingType.CITY, classifier.drivingType.value)

        // Single high speed spike (120 km/h) should not immediately flip to HIGHWAY
        classifier.onSpeedUpdate(120.0, timestampMs = 10 * 1000L)
        assertEquals(DrivingType.CITY, classifier.drivingType.value)
    }
}
