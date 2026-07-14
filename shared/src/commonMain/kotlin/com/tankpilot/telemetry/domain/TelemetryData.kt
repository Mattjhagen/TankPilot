package com.tankpilot.telemetry.domain

import kotlinx.coroutines.flow.StateFlow

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING
}

data class TelemetryData(
    val speedKmh: Double? = null,
    val engineRpm: Double? = null,
    val engineLoadPercent: Double? = null,
    val coolantTempCelsius: Double? = null,
    val intakeAirTempCelsius: Double? = null,
    val batteryVoltage: Double? = null,
    val vin: String? = null,
    val engineRuntimeSeconds: Long? = null,
    val checkEngineLightOn: Boolean? = null,
    val diagnosticTroubleCodes: List<String> = emptyList(),
    val fuelTrimPercent: Double? = null,
    val massAirFlowGps: Double? = null
)

data class TelemetryMetadata(
    val adapterName: String? = null,
    val signalStrengthDbm: Int? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED
)

interface VehicleTelemetryProvider {
    val telemetryFlow: StateFlow<TelemetryData>
    val metadataFlow: StateFlow<TelemetryMetadata>
    
    suspend fun connect()
    suspend fun disconnect()
}
