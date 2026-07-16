package com.tankpilot.trip.domain

import com.tankpilot.location.domain.RoadContext
import kotlin.test.*

class DrivingPatternClassifierTest {

    private lateinit var classifier: DrivingPatternClassifier

    @BeforeTest
    fun setUp() {
        classifier = DrivingPatternClassifier(
            minSamples = 5,
            hysteresisDurationMs = 1000L
        )
    }

    @Test
    fun testDefaultIsUnknown() {
        assertEquals(DrivingPattern.UNKNOWN, classifier.drivingPattern.value)
    }

    @Test
    fun testMinSamplesThreshold() {
        // Feed 4 samples (min is 5)
        for (i in 1..4) {
            classifier.onSpeedUpdate(50.0, RoadContext.URBAN_LIKELY, timestampMs = i * 1000L)
        }
        assertEquals(DrivingPattern.UNKNOWN, classifier.drivingPattern.value)

        // Feed 5th sample (raw pattern becomes URBAN_FLOW, starts candidate)
        classifier.onSpeedUpdate(50.0, RoadContext.URBAN_LIKELY, timestampMs = 5 * 1000L)
        assertEquals(DrivingPattern.UNKNOWN, classifier.drivingPattern.value)

        // Feed 6th sample (1000ms later to satisfy hysteresis duration)
        classifier.onSpeedUpdate(50.0, RoadContext.URBAN_LIKELY, timestampMs = 6 * 1000L)
        assertEquals(DrivingPattern.URBAN_FLOW, classifier.drivingPattern.value)
    }

    @Test
    fun testHysteresisPreventsImmediateFlapping() {
        // Initialize as URBAN_FLOW (5 samples + 1 extra to trigger hysteresis transition)
        for (i in 1..5) {
            classifier.onSpeedUpdate(40.0, RoadContext.URBAN_LIKELY, timestampMs = i * 1000L)
        }
        classifier.onSpeedUpdate(40.0, RoadContext.URBAN_LIKELY, timestampMs = 6 * 1000L)
        assertEquals(DrivingPattern.URBAN_FLOW, classifier.drivingPattern.value)

        // Feed 1 high speed update
        classifier.onSpeedUpdate(90.0, RoadContext.HIGHWAY_LIKELY, timestampMs = 7 * 1000L)
        // Still URBAN_FLOW because average speed is still low, and hysteresis is active
        assertEquals(DrivingPattern.URBAN_FLOW, classifier.drivingPattern.value)

        // Feed multiple high speed updates to shift average above 72 km/h and satisfy hysteresis duration
        for (i in 8..20) {
            classifier.onSpeedUpdate(95.0, RoadContext.HIGHWAY_LIKELY, timestampMs = i * 1000L)
        }
        // Now it transitions to SUSTAINED_HIGH_SPEED
        assertEquals(DrivingPattern.SUSTAINED_HIGH_SPEED, classifier.drivingPattern.value)
    }
}
