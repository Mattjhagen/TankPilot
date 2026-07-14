package com.tankpilot.telemetry.data

import com.tankpilot.telemetry.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin

class MockTelemetryProvider : VehicleTelemetryProvider {

    private val _telemetryFlow = MutableStateFlow(TelemetryData())
    override val telemetryFlow: StateFlow<TelemetryData> = _telemetryFlow.asStateFlow()

    private val _metadataFlow = MutableStateFlow(
        TelemetryMetadata(
            adapterName = "vLinker MC+ BLE",
            signalStrengthDbm = -65,
            connectionStatus = ConnectionStatus.DISCONNECTED
        )
    )
    override val metadataFlow: StateFlow<TelemetryMetadata> = _metadataFlow.asStateFlow()

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override suspend fun connect() {
        if (_metadataFlow.value.connectionStatus == ConnectionStatus.CONNECTED) return
        
        _metadataFlow.value = _metadataFlow.value.copy(connectionStatus = ConnectionStatus.CONNECTING)
        delay(1000)
        _metadataFlow.value = _metadataFlow.value.copy(connectionStatus = ConnectionStatus.CONNECTED)

        job = scope.launch {
            var ticks = 0
            while (isActive) {
                ticks++
                // Simulate vehicle RPM oscillating gently
                val rpmSim = 800.0 + sin(ticks * 0.1) * 200.0
                // Simulate vehicle speed oscillating around 45 km/h
                val speedSim = 45.0 + sin(ticks * 0.05) * 15.0
                // Coolant temp warming up to 92C
                val coolantSim = minOf(92.0, 70.0 + ticks * 0.2)
                
                _telemetryFlow.value = TelemetryData(
                    speedKmh = speedSim,
                    engineRpm = rpmSim,
                    engineLoadPercent = 25.0 + sin(ticks * 0.15) * 10.0,
                    coolantTempCelsius = coolantSim,
                    intakeAirTempCelsius = 22.0,
                    batteryVoltage = 14.2,
                    vin = "1G1JC54F73H123456",
                    engineRuntimeSeconds = ticks.toLong() * 2L,
                    checkEngineLightOn = false,
                    diagnosticTroubleCodes = emptyList(),
                    fuelTrimPercent = 0.98,
                    massAirFlowGps = 4.5 + sin(ticks * 0.1) * 0.5
                )
                delay(1000)
            }
        }
    }

    override suspend fun disconnect() {
        job?.cancel()
        job = null
        _metadataFlow.value = _metadataFlow.value.copy(connectionStatus = ConnectionStatus.DISCONNECTED)
        _telemetryFlow.value = TelemetryData()
    }
}
