package com.tankpilot.trip.domain

import com.tankpilot.location.domain.RoadContext
import com.tankpilot.location.domain.SelectedSpeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock

class DrivingPatternClassifier(
    private val analyzer: SpeedSegmentAnalyzer = SpeedSegmentAnalyzer(),
    private val minSamples: Int = 10,
    private val cityAvgSpeedCeiling: Double = 56.0,
    private val highwayAvgSpeedFloor: Double = 72.0,
    private val stopAndGoStopsPerKm: Double = 2.0,
    private val urbanFlowMaxStopsPerKm: Double = 2.0,
    private val hysteresisDurationMs: Long = 5000L
) {
    private val _drivingPattern = MutableStateFlow(DrivingPattern.UNKNOWN)
    val drivingPattern: StateFlow<DrivingPattern> = _drivingPattern.asStateFlow()

    // Tracking for hysteresis
    private var candidatePattern = DrivingPattern.UNKNOWN
    private var candidateFirstSeenMs: Long = 0L

    fun onSpeedUpdate(
        speedKmh: Double,
        roadContext: RoadContext = RoadContext.UNKNOWN,
        timestampMs: Long = Clock.System.now().toEpochMilliseconds()
    ) {
        analyzer.addSample(speedKmh, timestampMs)

        val rawPattern = when {
            analyzer.sampleCount < minSamples -> DrivingPattern.UNKNOWN
            else -> classify(roadContext)
        }

        // Apply hysteresis
        val now = timestampMs
        if (rawPattern == _drivingPattern.value) {
            candidatePattern = rawPattern
            candidateFirstSeenMs = 0L
        } else if (rawPattern == candidatePattern) {
            if (candidateFirstSeenMs == 0L) {
                candidateFirstSeenMs = now
            } else if (now - candidateFirstSeenMs >= hysteresisDurationMs) {
                _drivingPattern.value = rawPattern
                candidateFirstSeenMs = 0L
            }
        } else {
            candidatePattern = rawPattern
            candidateFirstSeenMs = now
        }
    }

    fun reset() {
        analyzer.reset()
        _drivingPattern.value = DrivingPattern.UNKNOWN
        candidatePattern = DrivingPattern.UNKNOWN
        candidateFirstSeenMs = 0L
    }

    private fun classify(roadContext: RoadContext): DrivingPattern {
        val avgSpeed = analyzer.averageSpeedKmh
        val stopsPerKm = analyzer.stopsPerKm
        val highSpeedPct = analyzer.highSpeedPercentage
        val hasSustainedHighway = analyzer.sustainedHighwayMs >= 60000L

        // 1. SUSTAINED_HIGH_SPEED
        val isHighwayLikely = roadContext == RoadContext.HIGHWAY_LIKELY || 
            (avgSpeed >= highwayAvgSpeedFloor && hasSustainedHighway && highSpeedPct >= 0.40 && stopsPerKm <= 1.0)
        
        if (isHighwayLikely && avgSpeed >= highwayAvgSpeedFloor && stopsPerKm <= 1.0) {
            return DrivingPattern.SUSTAINED_HIGH_SPEED
        }

        // 2. STOP_AND_GO
        // Requires repeated stops, not just a single stop.
        val hasRepeatedStops = stopsPerKm >= stopAndGoStopsPerKm && analyzer.stopsCount >= 2
        if (hasRepeatedStops && avgSpeed < cityAvgSpeedCeiling) {
            return DrivingPattern.STOP_AND_GO
        }

        // 3. URBAN_FLOW
        val isUrbanLikely = roadContext == RoadContext.URBAN_LIKELY || 
            (avgSpeed < cityAvgSpeedCeiling && avgSpeed >= 24.0 && stopsPerKm < urbanFlowMaxStopsPerKm)
        
        if (isUrbanLikely && avgSpeed < cityAvgSpeedCeiling && stopsPerKm < urbanFlowMaxStopsPerKm) {
            return DrivingPattern.URBAN_FLOW
        }

        return DrivingPattern.MIXED
    }
}
