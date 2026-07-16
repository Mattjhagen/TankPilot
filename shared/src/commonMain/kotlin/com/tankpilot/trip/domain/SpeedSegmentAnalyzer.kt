package com.tankpilot.trip.domain

import kotlinx.datetime.Clock

/**
 * Maintains a rolling buffer of GPS speed samples and computes driving pattern
 * metrics used by [DrivingClassifier] to distinguish city from highway driving.
 *
 * Not thread-safe — call from a single coroutine or protect externally.
 */
class SpeedSegmentAnalyzer(
    /** Maximum samples to retain in the buffer. */
    private val maxBufferSize: Int = 300,
    /** Speed (km/h) below which a sample counts as a "stop." */
    private val stopThresholdKmh: Double = 5.0,
    /** Minimum speed (km/h) to qualify as sustained highway driving. */
    private val highwaySpeedThresholdKmh: Double = 88.0, // ~55 mph
    /** Minimum consecutive milliseconds at highway speed to count as a "sustained segment." */
    private val sustainedHighwayDurationMs: Long = 60_000L
) {
    data class SpeedSample(val speedKmh: Double, val timestampMs: Long)

    private val buffer = ArrayDeque<SpeedSample>(maxBufferSize)

    // Cached analytics — recomputed on each addSample call
    private var _averageSpeedKmh: Double = 0.0
    private var _speedVariance: Double = 0.0
    private var _stopsCount: Int = 0
    private var _sustainedHighwayDurationMs: Long = 0
    private var _maxSpeedKmh: Double = 0.0
    private var _totalDistanceKm: Double = 0.0
    private var _highSpeedPercentage: Double = 0.0

    val averageSpeedKmh: Double get() = _averageSpeedKmh
    val speedVariance: Double get() = _speedVariance
    val stopsCount: Int get() = _stopsCount
    val sustainedHighwayMs: Long get() = _sustainedHighwayDurationMs
    val maxSpeedKmh: Double get() = _maxSpeedKmh
    val totalDistanceKm: Double get() = _totalDistanceKm

    /** Fraction of samples above the highway speed threshold (0.0–1.0). */
    val highSpeedPercentage: Double get() = _highSpeedPercentage

    /** Number of samples currently in the buffer. */
    val sampleCount: Int get() = buffer.size

    /**
     * Approximate stop frequency — stops per kilometer driven.
     * Returns 0.0 if insufficient distance has been accumulated.
     */
    val stopsPerKm: Double
        get() = if (_totalDistanceKm > 0.1) _stopsCount / _totalDistanceKm else 0.0

    fun addSample(speedKmh: Double, timestampMs: Long = Clock.System.now().toEpochMilliseconds()) {
        if (buffer.size >= maxBufferSize) {
            buffer.removeFirst()
        }
        buffer.addLast(SpeedSample(speedKmh, timestampMs))
        recompute()
    }

    fun reset() {
        buffer.clear()
        _averageSpeedKmh = 0.0
        _speedVariance = 0.0
        _stopsCount = 0
        _sustainedHighwayDurationMs = 0
        _maxSpeedKmh = 0.0
        _totalDistanceKm = 0.0
        _highSpeedPercentage = 0.0
    }

    // ── Internal ──────────────────────────────────────────────────────────

    private fun recompute() {
        if (buffer.isEmpty()) return

        var sumSpeed = 0.0
        var maxSpeed = 0.0
        var highSpeedCount = 0
        var stops = 0
        var wasAboveStop = buffer.first().speedKmh > stopThresholdKmh
        var highwaySegmentStart: Long? = null
        var totalHighwayMs = 0L
        var totalDist = 0.0

        for (i in buffer.indices) {
            val sample = buffer[i]
            sumSpeed += sample.speedKmh
            if (sample.speedKmh > maxSpeed) maxSpeed = sample.speedKmh

            // Count high-speed samples
            if (sample.speedKmh >= highwaySpeedThresholdKmh) highSpeedCount++

            // Count stops: transition from moving to stopped
            val isAboveStop = sample.speedKmh > stopThresholdKmh
            if (wasAboveStop && !isAboveStop) stops++
            wasAboveStop = isAboveStop

            // Sustained highway detection
            if (sample.speedKmh >= highwaySpeedThresholdKmh) {
                if (highwaySegmentStart == null) highwaySegmentStart = sample.timestampMs
            } else {
                if (highwaySegmentStart != null) {
                    val segmentDuration = sample.timestampMs - highwaySegmentStart
                    if (segmentDuration >= sustainedHighwayDurationMs) {
                        totalHighwayMs += segmentDuration
                    }
                    highwaySegmentStart = null
                }
            }

            // Accumulate distance (trapezoidal approximation between consecutive samples)
            if (i > 0) {
                val prev = buffer[i - 1]
                val dtHours = (sample.timestampMs - prev.timestampMs) / 3_600_000.0
                val avgSeg = (prev.speedKmh + sample.speedKmh) / 2.0
                totalDist += avgSeg * dtHours
            }
        }

        // Close an open highway segment at the end of the buffer
        if (highwaySegmentStart != null) {
            val segmentDuration = buffer.last().timestampMs - highwaySegmentStart
            if (segmentDuration >= sustainedHighwayDurationMs) {
                totalHighwayMs += segmentDuration
            }
        }

        val n = buffer.size.toDouble()
        _averageSpeedKmh = sumSpeed / n
        _maxSpeedKmh = maxSpeed
        _stopsCount = stops
        _sustainedHighwayDurationMs = totalHighwayMs
        _totalDistanceKm = totalDist
        _highSpeedPercentage = highSpeedCount / n

        // Variance
        var sumSqDiff = 0.0
        for (sample in buffer) {
            val diff = sample.speedKmh - _averageSpeedKmh
            sumSqDiff += diff * diff
        }
        _speedVariance = sumSqDiff / n
    }
}
