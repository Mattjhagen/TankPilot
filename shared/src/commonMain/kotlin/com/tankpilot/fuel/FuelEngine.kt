package com.tankpilot.fuel

import com.tankpilot.core.Gallons
import com.tankpilot.core.Miles
import com.tankpilot.core.MilesPerGallon
import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.trip.domain.DrivingType
import kotlin.math.max

object FuelEngine {

    /**
     * Estimates the idle fuel consumption rate in Gallons per Hour.
     * Prioritizes engine displacement (liters) if available, falling back to cylinder count.
     */
    fun estimateIdleFuelRate(displacementLiters: Double?, cylinderCount: Long?): Gallons {
        val gph = when {
            displacementLiters != null && displacementLiters > 0.0 -> {
                // Heuristic: ~0.08 Gallons per hour per liter of displacement
                displacementLiters * 0.08
            }
            cylinderCount != null && cylinderCount > 0L -> {
                when (cylinderCount) {
                    3L, 4L -> 0.15
                    5L, 6L -> 0.30
                    8L -> 0.50
                    else -> 0.25
                }
            }
            else -> 0.20 // Default fallback
        }
        return Gallons(gph)
    }

    /**
     * Estimates fuel burned during a single trip.
     */
    fun estimateFuelBurned(
        distance: Miles,
        durationSeconds: Long,
        idleTimeSeconds: Long,
        drivingType: DrivingType,
        displacementLiters: Double?,
        cylinderCount: Long?,
        learnedMpg: MilesPerGallon,
        factoryCityMpg: Double,
        factoryHwyMpg: Double
    ): Gallons {
        // 1. Calculate Idle Fuel Burned
        val idleHours = max(0.0, idleTimeSeconds.toDouble() / 3600.0)
        val idleRateGph = estimateIdleFuelRate(displacementLiters, cylinderCount)
        val idleFuelBurned = idleRateGph.value * idleHours

        // 2. Calculate Effective MPG based on driving type
        val effectiveMpgVal = if (factoryCityMpg > 0.0 && factoryHwyMpg > 0.0) {
            val factoryCombined = 0.55 * factoryCityMpg + 0.45 * factoryHwyMpg
            val scale = if (factoryCombined > 0.0) learnedMpg.value / factoryCombined else 1.0
            
            when (drivingType) {
                DrivingType.CITY -> factoryCityMpg * scale
                DrivingType.HIGHWAY -> factoryHwyMpg * scale
                DrivingType.MIXED -> learnedMpg.value
            }
        } else {
            // Fallback ratios
            when (drivingType) {
                DrivingType.CITY -> learnedMpg.value * 0.9
                DrivingType.HIGHWAY -> learnedMpg.value * 1.1
                DrivingType.MIXED -> learnedMpg.value
            }
        }
        
        val effectiveMpg = max(1.0, effectiveMpgVal)

        // 3. Calculate Driving Fuel Burned
        val drivingFuelBurned = distance.value / effectiveMpg

        return Gallons(drivingFuelBurned + idleFuelBurned)
    }

    /**
     * Returns the safety scaling factor for range estimations based on confidence level.
     */
    fun getConfidenceSafetyFactor(confidenceLevel: ConfidenceLevel): Double {
        return when (confidenceLevel) {
            ConfidenceLevel.VERY_HIGH -> 0.95
            ConfidenceLevel.HIGH -> 0.90
            ConfidenceLevel.MEDIUM -> 0.85
            ConfidenceLevel.LOW -> 0.80
        }
    }

    /**
     * Calculates the safe range in Miles.
     */
    fun calculateSafeRange(
        remainingFuel: Gallons,
        learnedMpg: MilesPerGallon,
        confidenceLevel: ConfidenceLevel
    ): Miles {
        val factor = getConfidenceSafetyFactor(confidenceLevel)
        val range = remainingFuel.value * learnedMpg.value * factor
        return Miles(max(0.0, range))
    }

    /**
     * Recalibrates Learned MPG using either odometer difference or cumulative GPS distance.
     * Uses an exponentially weighted moving average (EWMA) with alpha = 0.3.
     */
    fun recalibrateMpg(
        currentLearnedMpg: MilesPerGallon,
        distanceTraveled: Miles,
        totalGallonsAdded: Gallons,
        learningRate: Double = 0.3
    ): MilesPerGallon {
        if (totalGallonsAdded.value <= 0.0 || distanceTraveled.value <= 0.0) {
            return currentLearnedMpg
        }
        val calculatedMpg = distanceTraveled.value / totalGallonsAdded.value
        val newMpg = (learningRate * calculatedMpg) + ((1.0 - learningRate) * currentLearnedMpg.value)
        return MilesPerGallon(newMpg)
    }
}
