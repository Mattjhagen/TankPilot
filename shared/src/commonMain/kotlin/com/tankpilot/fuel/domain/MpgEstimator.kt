package com.tankpilot.fuel.domain

import com.tankpilot.trip.domain.DrivingPattern
import kotlinx.datetime.Clock

enum class MpgEstimateSource {
    OBD_MAF,
    LEARNED_TRIP,
    GPS_FACTORY_MODEL,
    UNKNOWN
}

data class MpgEstimate(
    val value: Double?,
    val source: MpgEstimateSource,
    val timestampMs: Long,
    val confidence: Double
)

class MpgEstimator(
    private val efficiencyProvider: VehicleEfficiencyProvider
) {
    fun estimateInstantMpg(
        speedKmh: Double?,
        pattern: DrivingPattern,
        massAirFlowGps: Double?,
        engineLoadPercent: Double?,
        timestampMs: Long = Clock.System.now().toEpochMilliseconds()
    ): MpgEstimate {
        val learnedMpg = efficiencyProvider.currentLearnedMpg.value ?: 25.0
        val factoryCity = efficiencyProvider.currentFactoryCityMpg.value ?: 25.0
        val factoryHwy = efficiencyProvider.currentFactoryHighwayMpg.value ?: 32.0

        if (speedKmh == null || speedKmh <= 0.0) {
            // Undefined/Idle when stationary
            return MpgEstimate(
                value = null,
                source = MpgEstimateSource.UNKNOWN,
                timestampMs = timestampMs,
                confidence = 1.0
            )
        }

        // 1. Prioritize OBD MAF if available
        if (massAirFlowGps != null && massAirFlowGps > 0.0) {
            val speedMph = speedKmh / 1.609344
            val mafMpg = (speedMph * 11.427) / massAirFlowGps
            val clampedMpg = mafMpg.coerceIn(1.0, 200.0)
            return MpgEstimate(
                value = clampedMpg,
                source = MpgEstimateSource.OBD_MAF,
                timestampMs = timestampMs,
                confidence = 0.95
            )
        }

        // 2. GPS/Factory model fallback
        val baseMpg = when (pattern) {
            DrivingPattern.URBAN_FLOW -> factoryCity
            DrivingPattern.SUSTAINED_HIGH_SPEED -> factoryHwy
            DrivingPattern.STOP_AND_GO -> factoryCity * 0.8
            DrivingPattern.MIXED -> (factoryCity + factoryHwy) / 2.0
            DrivingPattern.UNKNOWN -> learnedMpg
        }

        // Adjust for load if OBD is partially connected
        val loadFactor = if (engineLoadPercent != null && engineLoadPercent > 0.0) {
            val normalizedLoad = (engineLoadPercent / 100.0).coerceIn(0.0, 1.0)
            when {
                normalizedLoad < 0.30 -> 1.05
                normalizedLoad < 0.50 -> 1.0
                normalizedLoad < 0.70 -> 0.90
                else -> 0.75
            }
        } else 1.0

        val estimatedMpg = maxOf(1.0, baseMpg * loadFactor)
        return MpgEstimate(
            value = estimatedMpg,
            source = MpgEstimateSource.GPS_FACTORY_MODEL,
            timestampMs = timestampMs,
            confidence = 0.70
        )
    }
}
