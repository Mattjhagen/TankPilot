package com.tankpilot.telemetry.data

import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.telemetry.domain.TemperatureSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Production provider for when no trustworthy ambient temperature source exists.
 * Emits null permanently. The dashboard will display — for outside temperature.
 * Never fabricates a plausible-looking number.
 */
class UnavailableAmbientTemperatureProvider : AmbientTemperatureProvider {
    override val temperature: StateFlow<TemperatureSample?> = MutableStateFlow(null)
}
