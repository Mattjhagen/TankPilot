package com.tankpilot.fuel.domain

import com.tankpilot.core.Gallons
import com.tankpilot.core.Miles
import com.tankpilot.core.MilesPerGallon
import com.tankpilot.fuel.FuelEngine
import com.tankpilot.trip.domain.DrivingClassifier
import com.tankpilot.trip.domain.DrivingType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * Combines speed, driving classification, and optional OBD data to produce
 * live MPG estimates. Designed to degrade gracefully: uses MAF sensor data
 * when an OBD adapter is connected, otherwise falls back to factory MPG
 * curves adjusted by driving type.
 *
 * Feed data via [onUpdate] at 1–2 Hz from whatever data source is available.
 */
class LiveMpgTracker(
    private val factoryMpgProvider: () -> Pair<Double, Double>,
    /** Rolling window duration for the short-term average (ms). */
    private val rollingWindowMs: Long = 30_000L
) {
    private data class MpgSample(val mpg: Double, val timestampMs: Long)

    private val _instantMpg = MutableStateFlow<Double?>(null)
    val instantMpg: StateFlow<Double?> = _instantMpg.asStateFlow()

    private val _provenance = MutableStateFlow<com.tankpilot.fuel.MpgProvenance>(com.tankpilot.fuel.MpgProvenance.UNKNOWN)
    val provenance: StateFlow<com.tankpilot.fuel.MpgProvenance> = _provenance.asStateFlow()

    private val _tripAverageMpg = MutableStateFlow<Double?>(null)
    val tripAverageMpg: StateFlow<Double?> = _tripAverageMpg.asStateFlow()

    private val _rollingAverageMpg = MutableStateFlow<Double?>(null)
    val rollingAverageMpg: StateFlow<Double?> = _rollingAverageMpg.asStateFlow()

    // Internal accumulators
    private val recentSamples = ArrayDeque<MpgSample>()
    private var tripMpgSum = 0.0
    private var tripSampleCount = 0L

    /**
     * Feed a new data point. All parameters except [speedKmh] and [drivingType]
     * are optional — the tracker will use the best available data.
     *
     * @param speedKmh       Current vehicle speed from GPS or OBD.
     * @param drivingType    Current classification from [DrivingClassifier].
     * @param engineLoadPct  Engine load percentage from OBD PID 0104 (null if no OBD).
     * @param massAirFlowGps Mass Air Flow in grams/sec from OBD PID 0110 (null if no OBD).
     */
    fun onUpdate(
        speedKmh: Double,
        drivingType: DrivingType,
        engineLoadPct: Double? = null,
        massAirFlowGps: Double? = null,
        timestampMs: Long = Clock.System.now().toEpochMilliseconds()
    ) {
        val (factoryCityMpg, factoryHwyMpg) = factoryMpgProvider()
        val mpgResult = FuelEngine.estimateInstantMpg(
            speedKmh = speedKmh,
            drivingType = drivingType,
            engineLoadPercent = engineLoadPct,
            massAirFlowGps = massAirFlowGps,
            factoryCityMpg = factoryCityMpg,
            factoryHwyMpg = factoryHwyMpg
        )

        val mpgVal = mpgResult?.value
        _instantMpg.value = mpgVal
        _provenance.value = mpgResult?.provenance ?: com.tankpilot.fuel.MpgProvenance.UNKNOWN

        if (mpgVal != null) {
            // Trip lifetime average
            tripMpgSum += mpgVal
            tripSampleCount++
            _tripAverageMpg.value = tripMpgSum / tripSampleCount

            // Rolling window average
            recentSamples.addLast(MpgSample(mpgVal, timestampMs))
            pruneOldSamples(timestampMs)
            _rollingAverageMpg.value = if (recentSamples.isNotEmpty()) {
                recentSamples.sumOf { it.mpg } / recentSamples.size
            } else null
        }
    }

    /**
     * Reset for a new trip.
     */
    fun reset() {
        _instantMpg.value = null
        _provenance.value = com.tankpilot.fuel.MpgProvenance.UNKNOWN
        _tripAverageMpg.value = null
        _rollingAverageMpg.value = null
        recentSamples.clear()
        tripMpgSum = 0.0
        tripSampleCount = 0
    }

    /**
     * Convenience: get the best current MPG for fuel predictions.
     * Prefers trip average (more stable), falls back to rolling, then instant.
     */
    fun bestEstimateMpg(): Double? = _tripAverageMpg.value
        ?: _rollingAverageMpg.value
        ?: _instantMpg.value

    private fun pruneOldSamples(now: Long) {
        val cutoff = now - rollingWindowMs
        while (recentSamples.isNotEmpty() && recentSamples.first().timestampMs < cutoff) {
            recentSamples.removeFirst()
        }
    }
}
