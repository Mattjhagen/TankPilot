package com.tankpilot.fuel

import com.tankpilot.core.Gallons
import com.tankpilot.core.Miles
import com.tankpilot.core.MilesPerGallon
import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.trip.domain.DrivingType
import kotlin.math.max

enum class MpgProvenance {
    OBD_MAF_ESTIMATE,
    GPS_FACTORY_ESTIMATE,
    LEARNED_TRIP_ESTIMATE,
    UNKNOWN
}

data class MpgResult(val value: Double, val provenance: MpgProvenance)

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

    /**
     * Estimates instant MPG using the best available data.
     * When MAF (Mass Air Flow, grams/sec) is available from OBD, uses the standard
     * automotive formula: MPG = (speed_mph × 7718) / MAF_gps.
     * When MAF is not available, interpolates between factory city/highway MPG
     * based on the current driving type and engine load (if available).
     *
     * Returns null if speed is zero or negative (MPG is undefined when stationary).
     */
    fun estimateInstantMpg(
        speedKmh: Double,
        drivingType: DrivingType,
        engineLoadPercent: Double?,
        massAirFlowGps: Double?,
        factoryCityMpg: Double,
        factoryHwyMpg: Double
    ): MpgResult? {
        if (speedKmh <= 0.0) return null
        val speedMph = speedKmh / 1.609344

        // Prefer MAF-based calculation when OBD data is available
        if (massAirFlowGps != null && massAirFlowGps > 0.0) {
            val mafMpg = estimateMpgFromMaf(massAirFlowGps, speedKmh)
            return if (mafMpg != null) MpgResult(mafMpg, MpgProvenance.OBD_MAF_ESTIMATE) else null
        }

        // Fallback: interpolate using factory MPG and driving type
        val baseMpg = when (drivingType) {
            DrivingType.CITY -> factoryCityMpg
            DrivingType.HIGHWAY -> factoryHwyMpg
            DrivingType.MIXED -> (factoryCityMpg + factoryHwyMpg) / 2.0
        }

        // Adjust for engine load if available (higher load = worse MPG)
        val loadFactor = if (engineLoadPercent != null && engineLoadPercent > 0.0) {
            // Nominal engine load ~30-40%. Higher load degrades MPG proportionally.
            val normalizedLoad = (engineLoadPercent / 100.0).coerceIn(0.0, 1.0)
            when {
                normalizedLoad < 0.30 -> 1.05  // Light load — slightly better
                normalizedLoad < 0.50 -> 1.0   // Moderate — baseline
                normalizedLoad < 0.70 -> 0.90  // Heavy — degraded
                else -> 0.75                   // Very heavy — significantly degraded
            }
        } else 1.0

        val estimatedVal = kotlin.math.max(1.0, baseMpg * loadFactor)
        return MpgResult(estimatedVal, MpgProvenance.GPS_FACTORY_ESTIMATE)
    }

    /**
     * Calculates MPG from the MAF sensor using the standard automotive formula:
     *   MPG = (speed_mph × 11.427) / MAF_grams_per_second
     *
     * The constant 11.427 derives from the stoichiometric ratio of gasoline and density:
     *   Constant = (stoichiometric_ratio [14.7] × fuel_density_g_per_gal [2798.6]) / 3600 sec_per_hr ≈ 11.427
     *
     * Returns null if inputs are invalid.
     */
    fun estimateMpgFromMaf(massAirFlowGps: Double, speedKmh: Double): Double? {
        if (massAirFlowGps <= 0.0 || speedKmh <= 0.0) return null
        val speedMph = speedKmh / 1.609344
        val mpg = (speedMph * 11.427) / massAirFlowGps
        // Sanity-clamp: MPG should be between 1 and 200 for any real vehicle
        return mpg.coerceIn(1.0, 200.0)
    }

    /**
     * Predicts remaining drivable miles based on current fuel and MPG.
     */
    fun predictMilesToEmpty(remainingFuel: Gallons, currentMpg: MilesPerGallon): Miles {
        val miles = remainingFuel.value * currentMpg.value
        return Miles(max(0.0, miles))
    }

    /**
     * Predicts approximate minutes until fuel is depleted at the current speed.
     * Returns null if speed is zero (stationary — time to empty is undefined).
     */
    fun predictMinutesToEmpty(milesToEmpty: Miles, currentSpeedKmh: Double): Double? {
        if (currentSpeedKmh <= 0.0) return null
        val speedMph = currentSpeedKmh / 1.609344
        return (milesToEmpty.value / speedMph) * 60.0
    }
}

