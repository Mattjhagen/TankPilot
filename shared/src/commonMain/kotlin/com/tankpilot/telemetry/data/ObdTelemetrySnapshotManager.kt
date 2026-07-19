package com.tankpilot.telemetry.data

import com.tankpilot.obd.domain.ObdConnectionState
import com.tankpilot.obd.domain.ObdPid
import com.tankpilot.obd.domain.ObdTelemetrySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ObdTelemetrySnapshotManager {
    private val _snapshotFlow = MutableStateFlow(ObdTelemetrySnapshot(timestampEpochMs = 0))
    val snapshotFlow: StateFlow<ObdTelemetrySnapshot> = _snapshotFlow.asStateFlow()

    private val _connectionStateFlow = MutableStateFlow(ObdConnectionState.DISCONNECTED)
    val connectionStateFlow: StateFlow<ObdConnectionState> = _connectionStateFlow.asStateFlow()

    fun updateSnapshot(snapshot: ObdTelemetrySnapshot) {
        _snapshotFlow.value = snapshot
    }
    
    fun updateValue(pid: ObdPid, value: Double) {
        val current = _snapshotFlow.value
        val timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        _snapshotFlow.value = when (pid) {
            ObdPid.VEHICLE_SPEED -> current.copy(speedKmh = value, timestampEpochMs = timestamp)
            ObdPid.ENGINE_RPM -> current.copy(rpm = value, timestampEpochMs = timestamp)
            ObdPid.COOLANT_TEMPERATURE -> current.copy(coolantTemperatureC = value, timestampEpochMs = timestamp)
            ObdPid.ENGINE_LOAD -> current.copy(engineLoadPercent = value, timestampEpochMs = timestamp)
            ObdPid.THROTTLE_POSITION -> current.copy(throttlePositionPercent = value, timestampEpochMs = timestamp)
            ObdPid.INTAKE_AIR_TEMPERATURE -> current.copy(intakeAirTemperatureC = value, timestampEpochMs = timestamp)
            ObdPid.MASS_AIR_FLOW -> current.copy(massAirFlowGramsPerSecond = value, timestampEpochMs = timestamp)
            ObdPid.FUEL_LEVEL -> current.copy(fuelLevelPercent = value, timestampEpochMs = timestamp)
            else -> current
        }
    }

    fun updateConnectionState(state: ObdConnectionState) {
        _connectionStateFlow.value = state
    }
}
