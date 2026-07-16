package com.tankpilot.fuel.domain

import com.tankpilot.core.FuelStatus

class AlertEngine {
    fun determineAlertText(
        status: FuelStatus,
        rangeMiles: Double,
        confidencePercent: Int
    ): String = when {
        status == FuelStatus.CRITICAL -> "Critical range. Refuel soon."
        status == FuelStatus.LOW -> "Low Fuel: Approx ${rangeMiles.toInt()} mi remaining."
        confidencePercent < 60 -> "Fuel estimate is becoming uncertain. Recording a full fill-up will recalibrate."
        else -> "All Good"
    }
}
