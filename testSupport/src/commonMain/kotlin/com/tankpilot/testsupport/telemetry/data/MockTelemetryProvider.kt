package com.tankpilot.testsupport.telemetry.data

import com.tankpilot.telemetry.domain.*
import com.tankpilot.testsupport.TestFixtures
import com.tankpilot.testsupport.MockSpeedScenario
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin

class MockTelemetryProvider : VehicleTelemetryProvider {

    private val _telemetryFlow = MutableStateFlow(TelemetryData())
    override val telemetryFlow: StateFlow<TelemetryData> = _telemetryFlow.asStateFlow()

    private val _capabilitiesFlow = MutableStateFlow<ObdCapabilities?>(null)
    override val capabilitiesFlow: StateFlow<ObdCapabilities?> = _capabilitiesFlow.asStateFlow()

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
        
        if (!TestFixtures.obdConnected.value) {
            _metadataFlow.value = _metadataFlow.value.copy(connectionStatus = ConnectionStatus.DISCONNECTED)
            return
        }

        _metadataFlow.value = _metadataFlow.value.copy(connectionStatus = ConnectionStatus.CONNECTED)

        _capabilitiesFlow.value = ObdCapabilities(
            supportedMode01Pids = setOf(0x0C, 0x0D, 0x04, 0x05, 0x0F, 0x42),
            supportsVin = true,
            supportsStoredDtcs = true,
            supportsPendingDtcs = false,
            supportsPermanentDtcs = false,
            detectedProtocol = "ISO 15765-4 CAN (11 bit ID, 500 kbaud)",
            adapterVersion = "ELM327 v2.2"
        )

        job = scope.launch {
            var ticks = 0
            while (isActive) {
                if (!TestFixtures.obdConnected.value) {
                    disconnect()
                    break
                }
                
                ticks++
                
                // Speed simulations based on scenario
                val (speedSim, rpmSim) = when (TestFixtures.speedScenario.value) {
                    MockSpeedScenario.IDLE -> Pair(0.0, 800.0)
                    MockSpeedScenario.BRIEF_SPIKE -> {
                        // 10 mph briefly for 2 ticks then idle
                        if (ticks % 10 < 2) Pair(16.0, 1500.0) else Pair(0.0, 800.0)
                    }
                    MockSpeedScenario.SUSTAINED_SPEED -> Pair(45.0 + sin(ticks * 0.05) * 5.0, 2000.0)
                    MockSpeedScenario.CITY_DRIVING -> Pair(35.0 + sin(ticks * 0.1) * 15.0, 1800.0)
                    MockSpeedScenario.HIGHWAY_DRIVING -> Pair(110.0 + sin(ticks * 0.05) * 5.0, 2500.0)
                    MockSpeedScenario.SHORT_STOP -> {
                        // Stop for 5 ticks, then move
                        if (ticks % 20 < 5) Pair(0.0, 800.0) else Pair(30.0, 1600.0)
                    }
                    MockSpeedScenario.SUSTAINED_STOP -> Pair(0.0, 800.0)
                }

                // Coolant temp warming up to 92C
                val coolantSim = minOf(92.0, 70.0 + ticks * 0.2)
                
                val ambientTemp = if (TestFixtures.ambientTemperatureAvailable.value) 22.0 else null
                
                _telemetryFlow.value = TelemetryData(
                    speedKmh = speedSim,
                    engineRpm = rpmSim,
                    engineLoadPercent = 25.0 + sin(ticks * 0.15) * 10.0,
                    coolantTempCelsius = coolantSim,
                    intakeAirTempCelsius = ambientTemp,
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
