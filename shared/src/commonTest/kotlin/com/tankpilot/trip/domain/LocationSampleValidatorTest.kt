package com.tankpilot.trip.domain

import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.LocationSampleSource
import com.tankpilot.location.domain.RoadContext
import kotlinx.datetime.Instant
import kotlin.test.*

class LocationSampleValidatorTest {

    private lateinit var validator: LocationSampleValidator

    @BeforeTest
    fun setUp() {
        validator = LocationSampleValidator()
    }

    @Test
    fun testValidSampleAccepted() {
        val now = Instant.fromEpochSeconds(1000L)
        val sample = LocationSample(
            timestamp = now,
            latitude = 41.2565,
            longitude = -95.9345,
            speedKmh = 50.0,
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 5.0,
            bearingDegrees = 180.0,
            roadContext = RoadContext.UNKNOWN,
            source = LocationSampleSource.GPS
        )

        val result = validator.validate(sample, now)
        assertTrue(result is LocationValidationResult.Accepted)
        assertEquals(sample, (result as LocationValidationResult.Accepted).sample)
    }

    @Test
    fun testInvalidCoordinatesRejected() {
        val now = Instant.fromEpochSeconds(1000L)
        val sample = LocationSample(
            timestamp = now,
            latitude = 95.0, // Invalid latitude (>90)
            longitude = -95.9345,
            speedKmh = 50.0,
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 5.0,
            bearingDegrees = 180.0
        )

        val result = validator.validate(sample, now)
        assertTrue(result is LocationValidationResult.Rejected)
        assertEquals(LocationRejectionReason.INVALID_COORDINATES, (result as LocationValidationResult.Rejected).reason)
    }

    @Test
    fun testPoorAccuracyRejected() {
        val now = Instant.fromEpochSeconds(1000L)
        val sample = LocationSample(
            timestamp = now,
            latitude = 41.2565,
            longitude = -95.9345,
            speedKmh = 50.0,
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 51.0, // > 50m
            bearingDegrees = 180.0
        )

        val result = validator.validate(sample, now)
        assertTrue(result is LocationValidationResult.Rejected)
        assertEquals(LocationRejectionReason.POOR_ACCURACY, (result as LocationValidationResult.Rejected).reason)
    }

    @Test
    fun testStaleTimestampRejected() {
        val now = Instant.fromEpochSeconds(1000L)
        val sample = LocationSample(
            timestamp = Instant.fromEpochSeconds(990L), // 10 seconds ago (stale > 5s)
            latitude = 41.2565,
            longitude = -95.9345,
            speedKmh = 50.0,
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 5.0,
            bearingDegrees = 180.0
        )

        val result = validator.validate(sample, now)
        assertTrue(result is LocationValidationResult.Rejected)
        assertEquals(LocationRejectionReason.STALE, (result as LocationValidationResult.Rejected).reason)
    }

    @Test
    fun testImpossibleSpeedRejected() {
        val now = Instant.fromEpochSeconds(1000L)
        val sample = LocationSample(
            timestamp = now,
            latitude = 41.2565,
            longitude = -95.9345,
            speedKmh = 260.0, // > 250 km/h
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 5.0,
            bearingDegrees = 180.0
        )

        val result = validator.validate(sample, now)
        assertTrue(result is LocationValidationResult.Rejected)
        assertEquals(LocationRejectionReason.IMPOSSIBLE_SPEED, (result as LocationValidationResult.Rejected).reason)
    }

    @Test
    fun testDuplicateTimestampRejected() {
        val now = Instant.fromEpochSeconds(1000L)
        val sample1 = LocationSample(
            timestamp = now,
            latitude = 41.2565,
            longitude = -95.9345,
            speedKmh = 50.0,
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 5.0,
            bearingDegrees = 180.0
        )
        val sample2 = sample1.copy(latitude = 41.2566) // same timestamp, different lat

        assertTrue(validator.validate(sample1, now) is LocationValidationResult.Accepted)
        val result = validator.validate(sample2, now)
        assertTrue(result is LocationValidationResult.Rejected)
        assertEquals(LocationRejectionReason.DUPLICATE_TIMESTAMP, (result as LocationValidationResult.Rejected).reason)
    }

    @Test
    fun testNonMonotonicTimestampRejected() {
        val now = Instant.fromEpochSeconds(1000L)
        val sample1 = LocationSample(
            timestamp = now,
            latitude = 41.2565,
            longitude = -95.9345,
            speedKmh = 50.0,
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 5.0,
            bearingDegrees = 180.0
        )
        val sample2 = sample1.copy(timestamp = Instant.fromEpochSeconds(999L)) // moving backward in time

        assertTrue(validator.validate(sample1, now) is LocationValidationResult.Accepted)
        val result = validator.validate(sample2, now)
        assertTrue(result is LocationValidationResult.Rejected)
        assertEquals(LocationRejectionReason.NON_MONOTONIC_TIMESTAMP, (result as LocationValidationResult.Rejected).reason)
    }

    @Test
    fun testImplausibleJumpRejected() {
        val start = Instant.fromEpochSeconds(1000L)
        val sample1 = LocationSample(
            timestamp = start,
            latitude = 41.2565,
            longitude = -95.9345,
            speedKmh = 50.0,
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 5.0,
            bearingDegrees = 180.0
        )
        
        // Coordinates shifted by approx 1000 meters in 1 second
        val sample2 = LocationSample(
            timestamp = Instant.fromEpochSeconds(1001L),
            latitude = 41.2665, // ~1.1km jump
            longitude = -95.9345,
            speedKmh = 50.0,
            speedAccuracyMps = 1.0,
            horizontalAccuracyMeters = 5.0,
            bearingDegrees = 180.0
        )

        assertTrue(validator.validate(sample1, start) is LocationValidationResult.Accepted)
        val result = validator.validate(sample2, Instant.fromEpochSeconds(1001L))
        assertTrue(result is LocationValidationResult.Rejected)
        assertEquals(LocationRejectionReason.IMPLAUSIBLE_DISTANCE_JUMP, (result as LocationValidationResult.Rejected).reason)
    }
}
