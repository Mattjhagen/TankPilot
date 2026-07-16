package com.tankpilot.trip.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * Real-time driving-type classifier that uses GPS speed data to determine whether
 * the vehicle is in city, highway, or mixed driving. Designed to work without OBD —
 * GPS speed alone is sufficient.
 *
 * Classification rules (all speeds in km/h):
 * - **Highway/Interstate:** average speed ≥ 72 km/h (45 mph) AND sustained
 *   segments above 88 km/h (55 mph) lasting ≥ 60 seconds AND ≤ 1 stop per km.
 * - **City:** average speed < 56 km/h (35 mph) OR > 3 stops per km.
 * - **Mixed:** everything in between.
 *
 * The classifier requires a minimum of 10 speed samples before it emits a
 * non-null classification to avoid premature labeling.
 */
class DrivingClassifier(
    private val analyzer: SpeedSegmentAnalyzer = SpeedSegmentAnalyzer(),
    /** Minimum samples before producing a classification. */
    private val minSamples: Int = 10,
    /** Average speed below which driving is classified as CITY (km/h). */
    private val cityAvgSpeedCeiling: Double = 56.0, // ~35 mph
    /** Average speed above which driving may be classified as HIGHWAY (km/h). */
    private val highwayAvgSpeedFloor: Double = 72.0, // ~45 mph
    /** Stops-per-km above which driving is classified as CITY regardless of speed. */
    private val cityStopsPerKmThreshold: Double = 3.0,
    /** Stops-per-km below which highway classification is allowed. */
    private val highwayMaxStopsPerKm: Double = 1.0,
    /** Minimum fraction of samples at highway speed to qualify as HIGHWAY. */
    private val highwayMinHighSpeedFraction: Double = 0.40
) {
    private val _drivingType = MutableStateFlow(DrivingType.MIXED)
    val drivingType: StateFlow<DrivingType> = _drivingType.asStateFlow()

    /** Percentage of current trip classified as highway (0.0–1.0). */
    private val _highwayPercentage = MutableStateFlow(0.0)
    val highwayPercentage: StateFlow<Double> = _highwayPercentage.asStateFlow()

    /** Exposes the underlying analyzer's max speed for trip recording. */
    val maxSpeedKmh: Double get() = analyzer.maxSpeedKmh

    /**
     * Feed a new GPS speed reading. Call this at 1–2 Hz for best results.
     */
    fun onSpeedUpdate(speedKmh: Double, timestampMs: Long = Clock.System.now().toEpochMilliseconds()) {
        analyzer.addSample(speedKmh, timestampMs)

        if (analyzer.sampleCount < minSamples) return

        val type = classify()
        _drivingType.value = type
        _highwayPercentage.value = analyzer.highSpeedPercentage
    }

    /**
     * Reset for a new trip.
     */
    fun reset() {
        analyzer.reset()
        _drivingType.value = DrivingType.MIXED
        _highwayPercentage.value = 0.0
    }

    // ── Internal ──────────────────────────────────────────────────────────

    private fun classify(): DrivingType {
        val avgSpeed = analyzer.averageSpeedKmh
        val stopsPerKm = analyzer.stopsPerKm
        val highSpeedPct = analyzer.highSpeedPercentage
        val hasSustainedHighway = analyzer.sustainedHighwayMs > 0

        // City: low average speed OR very frequent stops
        if (avgSpeed < cityAvgSpeedCeiling || stopsPerKm > cityStopsPerKmThreshold) {
            // But if there's significant highway driving mixed in, call it MIXED
            if (highSpeedPct > 0.20 && hasSustainedHighway) {
                return DrivingType.MIXED
            }
            return DrivingType.CITY
        }

        // Highway: high average speed, few stops, significant time at highway speed
        if (avgSpeed >= highwayAvgSpeedFloor &&
            stopsPerKm <= highwayMaxStopsPerKm &&
            highSpeedPct >= highwayMinHighSpeedFraction &&
            hasSustainedHighway
        ) {
            return DrivingType.HIGHWAY
        }

        return DrivingType.MIXED
    }
}
