package com.tankpilot.telemetry.data

import com.tankpilot.obd.domain.ObdConnectionState
import com.tankpilot.obd.domain.ObdTelemetrySnapshot
import com.tankpilot.telemetry.domain.ConnectionStatus
import com.tankpilot.telemetry.domain.ObdCapabilities
import com.tankpilot.telemetry.domain.TelemetryData
import com.tankpilot.telemetry.domain.TelemetryMetadata
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ObdTelemetryCompatibilityAdapter(
    val snapshotFlow: kotlinx.coroutines.flow.Flow<ObdTelemetrySnapshot>,
    val connectionStateFlow: kotlinx.coroutines.flow.Flow<ObdConnectionState>,
    val scope: CoroutineScope
) : VehicleTelemetryProvider {

    private val _telemetryFlow = MutableStateFlow(TelemetryData())
    override val telemetryFlow: StateFlow<TelemetryData> = _telemetryFlow.asStateFlow()

    private val _metadataFlow = MutableStateFlow(TelemetryMetadata())
    override val metadataFlow: StateFlow<TelemetryMetadata> = _metadataFlow.asStateFlow()

    private val _capabilitiesFlow = MutableStateFlow<ObdCapabilities?>(null)
    override val capabilitiesFlow: StateFlow<ObdCapabilities?> = _capabilitiesFlow.asStateFlow()

    init {
        snapshotFlow.onEach { snapshot ->
            _telemetryFlow.update {
                it.copy(
                    speedKmh = snapshot.speedKmh,
                    engineRpm = snapshot.rpm,
                    engineLoadPercent = snapshot.engineLoadPercent,
                    coolantTempCelsius = snapshot.coolantTemperatureC,
                    intakeAirTempCelsius = snapshot.intakeAirTemperatureC,
                    batteryVoltage = snapshot.batteryVoltage,
                    massAirFlowGps = snapshot.massAirFlowGramsPerSecond,
                    throttlePositionPercent = snapshot.throttlePositionPercent
                )
            }
        }.launchIn(scope)

        connectionStateFlow.onEach { state ->
            val status = when (state) {
                ObdConnectionState.DISCONNECTED,
                ObdConnectionState.BLUETOOTH_UNAVAILABLE,
                ObdConnectionState.PERMISSION_DENIED,
                ObdConnectionState.ERROR -> ConnectionStatus.DISCONNECTED
                
                ObdConnectionState.SCANNING,
                ObdConnectionState.PERIPHERAL_CONNECTING,
                ObdConnectionState.PERIPHERAL_CONNECTED,
                ObdConnectionState.DISCOVERING_SERVICES,
                ObdConnectionState.RESOLVING_CHARACTERISTICS,
                ObdConnectionState.ADAPTER_INITIALIZING,
                ObdConnectionState.SEARCHING_FOR_PROTOCOL -> ConnectionStatus.CONNECTING
                
                ObdConnectionState.VEHICLE_CONNECTED -> ConnectionStatus.CONNECTED
                
                ObdConnectionState.ADAPTER_CONNECTED_VEHICLE_UNAVAILABLE -> ConnectionStatus.CONNECTING
                ObdConnectionState.DISCONNECTING -> ConnectionStatus.DISCONNECTED
            }
            
            _metadataFlow.update { it.copy(connectionStatus = status) }
        }.launchIn(scope)
    }

    override suspend fun connect() {
        // Managed by iOS BLE Manager. No-op here.
    }

    override suspend fun disconnect() {
        // Managed by iOS BLE Manager. No-op here.
    }
}
