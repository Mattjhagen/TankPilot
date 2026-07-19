package com.tankpilot.obd.domain

import com.tankpilot.telemetry.domain.TelemetrySource

data class ObdTelemetrySnapshot(
    val speedKmh: Double? = null,
    val rpm: Double? = null,
    val coolantTemperatureC: Double? = null,
    val engineLoadPercent: Double? = null,
    val throttlePositionPercent: Double? = null,
    val intakeAirTemperatureC: Double? = null,
    val massAirFlowGramsPerSecond: Double? = null,
    val fuelLevelPercent: Double? = null,
    val batteryVoltage: Double? = null,
    val timestampEpochMs: Long,
    val source: TelemetrySource = TelemetrySource.OBD
)
