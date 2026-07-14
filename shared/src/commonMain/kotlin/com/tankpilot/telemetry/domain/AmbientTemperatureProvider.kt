package com.tankpilot.telemetry.domain

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

data class TemperatureSample(
    val celsius: Double,
    val timestamp: Instant,
    val source: String
)

interface AmbientTemperatureProvider {
    val temperature: StateFlow<TemperatureSample?>
}
