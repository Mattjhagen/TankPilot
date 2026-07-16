package com.tankpilot.trip.domain

import com.tankpilot.location.domain.LocationSample
import com.tankpilot.core.GeoCoordinate
import kotlinx.datetime.Instant
import kotlin.math.*

enum class LocationRejectionReason {
    STALE,
    POOR_ACCURACY,
    DUPLICATE_TIMESTAMP,
    NON_MONOTONIC_TIMESTAMP,
    IMPOSSIBLE_SPEED,
    IMPLAUSIBLE_DISTANCE_JUMP,
    INVALID_COORDINATES
}

sealed interface LocationValidationResult {
    data class Accepted(val sample: LocationSample) : LocationValidationResult
    data class Rejected(val reason: LocationRejectionReason) : LocationValidationResult
}

class LocationSampleValidator(
    private val maxAccuracyMeters: Double = 50.0,
    private val maxSpeedKmh: Double = 250.0,
    private val maxJumpDistanceMeters: Double = 100.0,
    private val maxStaleAgeSeconds: Long = 5
) {
    private var lastSample: LocationSample? = null

    fun validate(
        sample: LocationSample,
        currentWallClockTime: Instant
    ): LocationValidationResult {
        if (!GeoCoordinate.isValid(sample.latitude, sample.longitude)) {
            return LocationValidationResult.Rejected(LocationRejectionReason.INVALID_COORDINATES)
        }

        val accuracy = sample.horizontalAccuracyMeters
        if (accuracy != null && accuracy > maxAccuracyMeters) {
            return LocationValidationResult.Rejected(LocationRejectionReason.POOR_ACCURACY)
        }

        val ageSeconds = currentWallClockTime.epochSeconds - sample.timestamp.epochSeconds
        if (ageSeconds > maxStaleAgeSeconds) {
            return LocationValidationResult.Rejected(LocationRejectionReason.STALE)
        }

        val speed = sample.speedKmh
        if (speed != null && speed > maxSpeedKmh) {
            return LocationValidationResult.Rejected(LocationRejectionReason.IMPOSSIBLE_SPEED)
        }

        val last = lastSample
        if (last != null) {
            val timeDiffMs = sample.timestamp.toEpochMilliseconds() - last.timestamp.toEpochMilliseconds()
            
            if (timeDiffMs == 0L) {
                return LocationValidationResult.Rejected(LocationRejectionReason.DUPLICATE_TIMESTAMP)
            }
            
            if (timeDiffMs < 0L) {
                return LocationValidationResult.Rejected(LocationRejectionReason.NON_MONOTONIC_TIMESTAMP)
            }

            val distance = calculateDistanceMeters(
                last.latitude, last.longitude,
                sample.latitude, sample.longitude
            )
            val timeDiffSec = timeDiffMs / 1000.0
            if (timeDiffSec <= 2.0 && distance > maxJumpDistanceMeters) {
                return LocationValidationResult.Rejected(LocationRejectionReason.IMPLAUSIBLE_DISTANCE_JUMP)
            }
        }

        lastSample = sample
        return LocationValidationResult.Accepted(sample)
    }

    fun reset() {
        lastSample = null
    }

    private fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val lat1Rad = lat1 * PI / 180.0
        val lat2Rad = lat2 * PI / 180.0
        val a = sin(dLat / 2).pow(2.0) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return 6371000.0 * c
    }
}
