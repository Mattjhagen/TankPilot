package com.tankpilot.telemetry.data

import com.tankpilot.obd.domain.ObdConnectionState
import com.tankpilot.obd.domain.ObdTelemetrySnapshot
import com.tankpilot.telemetry.domain.ConnectionStatus
import com.tankpilot.telemetry.domain.TelemetrySource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObdTelemetryCompatibilityAdapterTest {

    private val testScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    @Test
    fun `adapter maps OBD snapshot to legacy TelemetryData`() = runBlocking {
        val snapshotFlow = MutableStateFlow(ObdTelemetrySnapshot(timestampEpochMs = 0))
        val connectionFlow = MutableStateFlow(ObdConnectionState.DISCONNECTED)
        
        val adapter = ObdTelemetryCompatibilityAdapter(
            snapshotFlow = snapshotFlow,
            connectionStateFlow = connectionFlow,
            scope = testScope
        )

        assertEquals(ConnectionStatus.DISCONNECTED, adapter.metadataFlow.value.connectionStatus)

        connectionFlow.value = ObdConnectionState.VEHICLE_CONNECTED
        assertEquals(ConnectionStatus.CONNECTED, adapter.metadataFlow.value.connectionStatus)

        snapshotFlow.value = ObdTelemetrySnapshot(
            speedKmh = 120.5,
            rpm = 3000.0,
            coolantTemperatureC = 90.0,
            engineLoadPercent = 45.0,
            throttlePositionPercent = 15.0,
            intakeAirTemperatureC = 30.0,
            massAirFlowGramsPerSecond = 14.5,
            batteryVoltage = 14.1,
            timestampEpochMs = 1000,
            source = TelemetrySource.OBD
        )

        val telemetry = adapter.telemetryFlow.value
        assertEquals(120.5, telemetry.speedKmh)
        assertEquals(3000.0, telemetry.engineRpm)
        assertEquals(90.0, telemetry.coolantTempCelsius)
        assertEquals(45.0, telemetry.engineLoadPercent)
        assertEquals(15.0, telemetry.throttlePositionPercent)
        assertEquals(30.0, telemetry.intakeAirTempCelsius)
        assertEquals(14.5, telemetry.massAirFlowGps)
        assertEquals(14.1, telemetry.batteryVoltage)
        assertNull(telemetry.fuelTrimPercent)
    }
}
