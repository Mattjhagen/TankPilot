package com.tankpilot.telemetry.data

import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.telemetry.domain.TemperatureSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAmbientTemperatureProvider(
    private val isDeveloperMode: Boolean = false
) : AmbientTemperatureProvider {
    private val _temperature = MutableStateFlow<TemperatureSample?>(null)
    override val temperature: StateFlow<TemperatureSample?> = _temperature.asStateFlow()

    init {
        if (isDeveloperMode) {
            _temperature.value = TemperatureSample(
                celsius = 22.0,
                timestamp = kotlinx.datetime.Clock.System.now(),
                source = "MOCK"
            )
        }
    }
}
