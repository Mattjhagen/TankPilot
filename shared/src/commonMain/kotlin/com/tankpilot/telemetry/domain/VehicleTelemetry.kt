package com.tankpilot.telemetry.domain

import kotlinx.datetime.Instant

data class VehicleTelemetry(
    val speedKmh: Double?,
    val engineRpm: Double?,
    val massAirFlowGps: Double?,
    val coolantTempCelsius: Double?,
    val batteryVoltage: Double?,
    val odometerKm: Double?,
    val fuelRateLph: Double?,
    val timestamp: Instant
)
