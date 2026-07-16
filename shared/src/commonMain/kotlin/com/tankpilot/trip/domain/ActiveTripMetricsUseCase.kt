package com.tankpilot.trip.domain

import com.tankpilot.location.domain.LocationSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant
import kotlin.math.*

class ActiveTripMetricsUseCase {
    private val _accumulatedDistanceMiles = MutableStateFlow(0.0)
    val accumulatedDistanceMiles: StateFlow<Double> = _accumulatedDistanceMiles.asStateFlow()

    private val _elapsedTimeSeconds = MutableStateFlow(0L)
    val elapsedTimeSeconds: StateFlow<Long> = _elapsedTimeSeconds.asStateFlow()

    private val _idleTimeSeconds = MutableStateFlow(0L)
    val idleTimeSeconds: StateFlow<Long> = _idleTimeSeconds.asStateFlow()

    private val _averageSpeedMph = MutableStateFlow<Double?>(null)
    val averageSpeedMph: StateFlow<Double?> = _averageSpeedMph.asStateFlow()

    private val _maxSpeedKmh = MutableStateFlow(0.0)
    val maxSpeedKmh: StateFlow<Double> = _maxSpeedKmh.asStateFlow()

    private var startTimestamp: Instant? = null
    private var lastSample: LocationSample? = null
    private var accumulatedIdleMs: Long = 0L

    fun onLocationUpdate(sample: LocationSample, isTripActive: Boolean) {
        if (!isTripActive) {
            lastSample = null
            return
        }

        if (startTimestamp == null) {
            startTimestamp = sample.timestamp
        }

        val last = lastSample
        if (last != null) {
            // 1. Accumulate distance
            val deltaMiles = calculateDistanceMiles(
                last.latitude, last.longitude,
                sample.latitude, sample.longitude
            )
            // Sanity check to ignore jumps that validator might have missed
            if (deltaMiles < 2.0) { // < 2 miles per sample (e.g. 1 sec update)
                _accumulatedDistanceMiles.value += deltaMiles
            }

            // 2. Track elapsed time
            val elapsed = sample.timestamp.epochSeconds - startTimestamp!!.epochSeconds
            _elapsedTimeSeconds.value = maxOf(0L, elapsed)

            // 3. Track idle time
            val timeDiffMs = sample.timestamp.toEpochMilliseconds() - last.timestamp.toEpochMilliseconds()
            if (timeDiffMs > 0 && sample.speedKmh != null && sample.speedKmh < 5.0) {
                accumulatedIdleMs += timeDiffMs
                _idleTimeSeconds.value = accumulatedIdleMs / 1000L
            }

            // 4. Max speed
            val speed = sample.speedKmh ?: 0.0
            if (speed > _maxSpeedKmh.value) {
                _maxSpeedKmh.value = speed
            }

            // 5. Average speed
            val elapsedHours = _elapsedTimeSeconds.value / 3600.0
            if (elapsedHours > 0.0) {
                _averageSpeedMph.value = _accumulatedDistanceMiles.value / elapsedHours
            }
        }

        lastSample = sample
    }

    fun restoreSession(
        distanceMiles: Double,
        elapsedSeconds: Long,
        idleSeconds: Long,
        maxSpeed: Double,
        startTimestampEpochMs: Long
    ) {
        _accumulatedDistanceMiles.value = distanceMiles
        _elapsedTimeSeconds.value = elapsedSeconds
        _idleTimeSeconds.value = idleSeconds
        _maxSpeedKmh.value = maxSpeed
        accumulatedIdleMs = idleSeconds * 1000L
        startTimestamp = Instant.fromEpochMilliseconds(startTimestampEpochMs)
        
        val elapsedHours = elapsedSeconds / 3600.0
        if (elapsedHours > 0.0) {
            _averageSpeedMph.value = distanceMiles / elapsedHours
        }
    }

    fun reset() {
        _accumulatedDistanceMiles.value = 0.0
        _elapsedTimeSeconds.value = 0L
        _idleTimeSeconds.value = 0L
        _averageSpeedMph.value = null
        _maxSpeedKmh.value = 0.0
        startTimestamp = null
        lastSample = null
        accumulatedIdleMs = 0L
    }

    private fun calculateDistanceMiles(
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
        return 3958.8 * c
    }
}
