package com.tankpilot.telemetry.data

import com.tankpilot.telemetry.domain.*
import com.tankpilot.telemetry.domain.bluetooth.ObdTransport
import com.tankpilot.telemetry.domain.bluetooth.ObdTransportState
import com.tankpilot.telemetry.domain.obd.Elm327Driver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ObdTelemetryProvider(
    private val transport: ObdTransport,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) : VehicleTelemetryProvider {

    private val driver = Elm327Driver(transport, scope)
    
    val rawLogs: StateFlow<List<String>> = driver.rawLogs
    private var pollingJob: Job? = null

    private val _telemetryFlow = MutableStateFlow(TelemetryData())
    override val telemetryFlow: StateFlow<TelemetryData> = _telemetryFlow.asStateFlow()

    private val _metadataFlow = MutableStateFlow(TelemetryMetadata())
    override val metadataFlow: StateFlow<TelemetryMetadata> = _metadataFlow.asStateFlow()

    private val _capabilitiesFlow = MutableStateFlow<ObdCapabilities?>(null)
    override val capabilitiesFlow: StateFlow<ObdCapabilities?> = _capabilitiesFlow.asStateFlow()

    override suspend fun connect() {
        if (_metadataFlow.value.connectionStatus == ConnectionStatus.CONNECTED || _metadataFlow.value.connectionStatus == ConnectionStatus.CONNECTING) return
        
        _metadataFlow.update { it.copy(connectionStatus = ConnectionStatus.CONNECTING) }
        
        try {
            if (transport.connectionState.value != ObdTransportState.CONNECTED) {
                transport.connect()
            }
            driver.initialize()
            
            val adapterVersion = driver.getBanner()
            val detectedProtocol = driver.getProtocol()
            
            _capabilitiesFlow.value = ObdCapabilities(
                supportedMode01Pids = emptySet(), // Not fully implemented yet
                supportsVin = false,
                supportsStoredDtcs = false,
                supportsPendingDtcs = false,
                supportsPermanentDtcs = false,
                detectedProtocol = detectedProtocol,
                adapterVersion = adapterVersion
            )
            
            _metadataFlow.update { it.copy(connectionStatus = ConnectionStatus.CONNECTED, adapterName = adapterVersion) }
            startPolling()
        } catch (e: Exception) {
            _metadataFlow.update { it.copy(connectionStatus = ConnectionStatus.DISCONNECTED) }
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun disconnect() {
        pollingJob?.cancel()
        driver.stop()
        transport.disconnect()
        _metadataFlow.update { it.copy(connectionStatus = ConnectionStatus.DISCONNECTED) }
        _telemetryFlow.value = TelemetryData()
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive && _metadataFlow.value.connectionStatus == ConnectionStatus.CONNECTED) {
                try {
                    pollMetrics()
                    delay(500) // Poll twice a second
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    _metadataFlow.update { it.copy(connectionStatus = ConnectionStatus.DISCONNECTED) }
                    transport.disconnect()
                    break
                }
            }
        }
    }

    private suspend fun pollMetrics() {
        var currentData = _telemetryFlow.value

        // 010D Vehicle Speed (km/h)
        val speedRaw = driver.requestPid("01", "0D")
        if (speedRaw.isNotEmpty()) {
            val speed = speedRaw.toIntOrNull(16)?.toDouble()
            currentData = currentData.copy(speedKmh = speed)
        }

        // 010C Engine RPM (rpm * 4)
        val rpmRaw = driver.requestPid("01", "0C")
        if (rpmRaw.length >= 4) {
            val a = rpmRaw.substring(0, 2).toIntOrNull(16) ?: 0
            val b = rpmRaw.substring(2, 4).toIntOrNull(16) ?: 0
            val rpm = (((a * 256) + b) / 4).toDouble()
            currentData = currentData.copy(engineRpm = rpm)
        }

        // 0105 Engine Coolant Temperature (°C + 40)
        val coolantRaw = driver.requestPid("01", "05")
        if (coolantRaw.isNotEmpty()) {
            val coolant = ((coolantRaw.toIntOrNull(16) ?: 40) - 40).toDouble()
            currentData = currentData.copy(coolantTempCelsius = coolant)
        }

        // ATRV Voltage
        val voltageRaw = driver.requestPid("AT", "RV")
        if (voltageRaw.isNotEmpty() && voltageRaw.endsWith("V")) {
            val voltage = voltageRaw.dropLast(1).toDoubleOrNull()
            currentData = currentData.copy(batteryVoltage = voltage)
        }
        
        _telemetryFlow.value = currentData
    }
}
