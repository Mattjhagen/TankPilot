package com.tankpilot.telemetry.data

import com.tankpilot.telemetry.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Production provider for when no OBD adapter is connected.
 * Reports disconnected state and emits null for all telemetry fields.
 * Never fabricates zero values or fake data.
 */
class UnavailableTelemetryProvider : VehicleTelemetryProvider {

    override val telemetryFlow: StateFlow<TelemetryData> = MutableStateFlow(TelemetryData())

    override val metadataFlow: StateFlow<TelemetryMetadata> = MutableStateFlow(
        TelemetryMetadata(
            adapterName = null,
            signalStrengthDbm = null,
            connectionStatus = ConnectionStatus.DISCONNECTED
        )
    )

    override val capabilitiesFlow: StateFlow<ObdCapabilities?> = MutableStateFlow(null)

    override suspend fun connect() {
        // No-op: no adapter to connect to
    }

    override suspend fun disconnect() {
        // No-op: no adapter to disconnect from
    }
}
