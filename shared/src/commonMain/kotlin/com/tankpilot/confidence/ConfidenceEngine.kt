package com.tankpilot.confidence

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.trip.domain.Trip
import kotlinx.datetime.Clock

object ConfidenceEngine {

    /**
     * Calculates the confidence level for the current fuel/range estimations.
     * Score is from 0 to 100 based on recency of fill-ups, calibration history, GPS quality, and odometer tracking.
     */
    fun calculateConfidence(
        fillUps: List<FillUp>,
        trips: List<Trip>,
        currentTimeMs: Long = Clock.System.now().toEpochMilliseconds()
    ): ConfidenceLevel {
        if (fillUps.isEmpty()) {
            return ConfidenceLevel.LOW
        }

        var score = 0

        // 1. Fill-up Recency (max 30 pts)
        val lastFillUp = fillUps.firstOrNull() // Assumes sorted descending by timestamp
        if (lastFillUp != null) {
            val diffMs = currentTimeMs - lastFillUp.timestamp
            val diffDays = diffMs.toDouble() / (1000.0 * 60.0 * 60.0 * 24.0)
            
            if (lastFillUp.isFull) {
                when {
                    diffDays <= 3.0 -> score += 30
                    diffDays <= 7.0 -> score += 25
                    diffDays <= 14.0 -> score += 15
                    else -> score += 10
                }
            } else {
                // Partial fill-ups carry a lower confidence
                when {
                    diffDays <= 3.0 -> score += 15
                    diffDays <= 7.0 -> score += 10
                    else -> score += 5
                }
            }
        }

        // 2. Calibration Count (max 20 pts)
        // Count consecutive "isFull" fill-ups. Calibrations require consecutive full fill-ups.
        var consecutiveFullFills = 0
        for (fill in fillUps) {
            if (fill.isFull) {
                consecutiveFullFills++
            } else {
                break // Broken chain
            }
        }
        when {
            consecutiveFullFills >= 3 -> score += 20
            consecutiveFullFills == 2 -> score += 15
            consecutiveFullFills == 1 -> score += 10
            else -> score += 0
        }

        // 3. Odometer Consistency (max 20 pts)
        val fillsWithOdo = fillUps.count { it.odometer != null && it.odometer > 0.0 }
        val odoRatio = fillsWithOdo.toDouble() / fillUps.size.toDouble()
        when {
            odoRatio >= 0.9 -> score += 20
            odoRatio >= 0.5 -> score += 10
            odoRatio > 0.0 -> score += 5
            else -> score += 0
        }

        // 4. Trip Quality / GPS coverage (max 30 pts)
        // If we have trips, assess if they are manual or have gaps.
        // For Phase 1 manual logs, trip validity is assessed by how frequently the driver logs.
        if (trips.isEmpty()) {
            score += 10 // Baseline if no trips recorded (e.g. just setup or fill-up only)
        } else {
            // Check average speeds and distance ranges
            val hasInvalidSpeeds = trips.any { it.averageSpeed <= 0.0 || it.averageSpeed > 150.0 }
            if (!hasInvalidSpeeds) {
                score += 30
            } else {
                score += 15
            }
        }

        // Map score to ConfidenceLevel
        return when {
            score >= 80 -> ConfidenceLevel.VERY_HIGH
            score >= 60 -> ConfidenceLevel.HIGH
            score >= 40 -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }
    }
}
