package com.tankpilot.telemetry.domain

import com.tankpilot.obd.domain.ObdConnectionState

data class ArbitratedSpeed(
    val speedKmh: Double?,
    val source: TelemetrySource,
    val transitionReason: String? = null
)

class SpeedArbitrator {
    private var lastObdSpeed: Double? = null
    private var lastObdTimestamp: Long = 0
    private var lastGpsSpeed: Double? = null
    private var lastGpsTimestamp: Long = 0
    
    private var currentSource: TelemetrySource = TelemetrySource.GPS
    
    private var consecutiveValidObdSamples = 0
    private val OBD_FRESHNESS_MS = 2000L
    private val GPS_FRESHNESS_MS = 5000L
    private val MAX_PLAUSIBLE_SPEED = 260.0
    
    fun onGpsSpeedUpdate(speedKmh: Double, timestampMs: Long): ArbitratedSpeed {
        lastGpsSpeed = speedKmh
        lastGpsTimestamp = timestampMs
        return evaluate(timestampMs, ObdConnectionState.VEHICLE_CONNECTED) // We assume if calling without state we just evaluate freshness
    }
    
    fun onObdSpeedUpdate(speedKmh: Double, timestampMs: Long, connectionState: ObdConnectionState): ArbitratedSpeed {
        if (speedKmh in 0.0..MAX_PLAUSIBLE_SPEED) {
            lastObdSpeed = speedKmh
            lastObdTimestamp = timestampMs
            consecutiveValidObdSamples++
        } else {
            consecutiveValidObdSamples = 0
        }
        return evaluate(timestampMs, connectionState)
    }
    
    fun evaluate(currentWallClockMs: Long, obdConnectionState: ObdConnectionState): ArbitratedSpeed {
        val obdIsFresh = (currentWallClockMs - lastObdTimestamp) <= OBD_FRESHNESS_MS
        val gpsIsFresh = (currentWallClockMs - lastGpsTimestamp) <= GPS_FRESHNESS_MS
        
        val obdIsReady = obdConnectionState == ObdConnectionState.VEHICLE_CONNECTED
        var transitionReason: String? = null
        
        if (currentSource == TelemetrySource.GPS) {
            if (obdIsReady && obdIsFresh && consecutiveValidObdSamples >= 2) {
                currentSource = TelemetrySource.OBD
                transitionReason = "OBD became stable and fresh"
            }
        } else if (currentSource == TelemetrySource.OBD) {
            if (!obdIsReady) {
                currentSource = TelemetrySource.GPS
                consecutiveValidObdSamples = 0
                transitionReason = "OBD disconnected"
            } else if (!obdIsFresh) {
                currentSource = TelemetrySource.GPS
                consecutiveValidObdSamples = 0
                transitionReason = "OBD signal became stale"
            }
        }
        
        val chosenSpeed = when(currentSource) {
            TelemetrySource.OBD -> if (obdIsFresh) lastObdSpeed else null
            TelemetrySource.GPS -> if (gpsIsFresh) lastGpsSpeed else null
        }
        
        // If chosen source is stale, fallback to whatever is fresh, even if we are officially in OBD mode?
        // Actually, the state machine above handles switching. If we are in GPS and GPS is stale, we output null.
        
        return ArbitratedSpeed(speedKmh = chosenSpeed, source = currentSource, transitionReason = transitionReason)
    }
    
    fun onObdDisconnect() {
        consecutiveValidObdSamples = 0
    }
}
