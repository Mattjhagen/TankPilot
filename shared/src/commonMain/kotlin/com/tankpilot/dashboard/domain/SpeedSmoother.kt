package com.tankpilot.dashboard.domain

import kotlinx.datetime.Clock

class SpeedSmoother(
    private val maxLatencyMs: Long = 1000,
    private val smoothingFactor: Double = 0.3
) {
    private var lastOutputKmh: Double? = null
    private var lastUpdateTimeMs: Long = 0

    fun filter(currentKmh: Double?, source: SpeedSource): SpeedDisplay {
        val now = Clock.System.now().toEpochMilliseconds()
        
        if (currentKmh == null) {
            // Rapid reset if source goes null
            lastOutputKmh = null
            lastUpdateTimeMs = now
            return SpeedDisplay(null, source)
        }
        
        if (lastOutputKmh == null || (now - lastUpdateTimeMs > maxLatencyMs)) {
            // No previous data or data is too old, accept current value immediately
            lastOutputKmh = currentKmh
        } else {
            // Low-pass filter to smooth jitter
            val newOutput = lastOutputKmh!! + smoothingFactor * (currentKmh - lastOutputKmh!!)
            
            // If the difference is large (e.g. rapid acceleration/braking), reduce smoothing
            // to ensure responsiveness and avoid lag.
            val diff = kotlin.math.abs(currentKmh - newOutput)
            lastOutputKmh = if (diff > 5.0) {
                currentKmh // immediate update for large changes
            } else {
                newOutput
            }
        }
        
        // Exact zero handling
        if (currentKmh == 0.0) {
            lastOutputKmh = 0.0
        }
        
        lastUpdateTimeMs = now
        return SpeedDisplay(lastOutputKmh!!.toInt(), source)
    }
    
    fun reset() {
        lastOutputKmh = null
        lastUpdateTimeMs = 0
    }
}
