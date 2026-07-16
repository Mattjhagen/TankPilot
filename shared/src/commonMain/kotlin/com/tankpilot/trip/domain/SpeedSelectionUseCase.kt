package com.tankpilot.trip.domain

import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.SpeedSource
import com.tankpilot.location.domain.SelectedSpeed
import com.tankpilot.telemetry.domain.TelemetryData
import com.tankpilot.core.AppClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant

class SpeedSelectionUseCase(
    private val telemetryFlow: StateFlow<TelemetryData>,
    private val gpsSpeedFlow: StateFlow<Double?>,
    private val clock: AppClock,
    private val scope: CoroutineScope,
    private val freshnessThresholdMs: Long = 2000L
) {
    private var lastObdTimestamp: Instant? = null
    private var lastObdSpeed: Double? = null

    val selectedSpeed: StateFlow<SelectedSpeed> = combine(
        telemetryFlow,
        gpsSpeedFlow
    ) { telemetry, gpsSpeed ->
        val now = clock.now()
        
        // Track when OBD speed changes/updates
        val obdSpeed = telemetry.speedKmh
        if (obdSpeed != null) {
            if (obdSpeed != lastObdSpeed) {
                lastObdSpeed = obdSpeed
                lastObdTimestamp = now
            } else if (lastObdTimestamp == null) {
                lastObdTimestamp = now
            }
        } else {
            lastObdSpeed = null
            lastObdTimestamp = null
        }

        // Evaluate OBD freshness
        val obdFresh = lastObdTimestamp?.let {
            (now.toEpochMilliseconds() - it.toEpochMilliseconds()) <= freshnessThresholdMs
        } ?: false

        when {
            obdSpeed != null && obdFresh -> SelectedSpeed(
                valueKmh = obdSpeed,
                source = SpeedSource.OBD,
                timestamp = lastObdTimestamp,
                isFresh = true
            )
            gpsSpeed != null -> SelectedSpeed(
                valueKmh = gpsSpeed,
                source = SpeedSource.GPS,
                timestamp = now,
                isFresh = true
            )
            else -> SelectedSpeed(
                valueKmh = null,
                source = SpeedSource.UNKNOWN,
                timestamp = null,
                isFresh = false
            )
        }
    }.stateIn(scope, SharingStarted.Eagerly, SelectedSpeed(null, SpeedSource.UNKNOWN, null, false))
}
