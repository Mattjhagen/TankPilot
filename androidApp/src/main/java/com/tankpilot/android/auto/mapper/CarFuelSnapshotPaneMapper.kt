package com.tankpilot.android.auto.mapper

import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.Row
import com.tankpilot.android.auto.model.CarFuelSnapshot
import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.FuelStatus
import kotlin.math.roundToInt

private const val UNAVAILABLE = "Unavailable"

/**
 * Maps a [CarFuelSnapshot] to a glanceable [Pane]. Formatting only — every value
 * already comes resolved (or explicitly null/unavailable) from the shared use case.
 */
fun CarFuelSnapshot.toPane(onFindFuelClick: () -> Unit): Pane {
    val fuelRow = Row.Builder()
        .setTitle("Estimated Fuel")
        .addText(
            if (fuelPercent != null && gallonsRemaining != null) {
                "$fuelPercent% · ${formatGallons(gallonsRemaining)} gal remaining"
            } else {
                UNAVAILABLE
            }
        )
        .build()

    val rangeRow = Row.Builder()
        .setTitle("Safe Range")
        .addText(safeRangeMiles?.let { "${it.roundToInt()} mi" } ?: UNAVAILABLE)
        .build()

    val confidenceRow = Row.Builder()
        .setTitle("Confidence")
        .addText(
            if (confidenceLevel != null && confidencePercent != null) {
                "${confidenceLevel.toDisplayLabel()} ($confidencePercent%)"
            } else {
                UNAVAILABLE
            }
        )
        .build()

    val statusRow = Row.Builder()
        .setTitle("Fuel Status")
        .addText(fuelStatus.toDisplayLabel())
        .build()

    return Pane.Builder()
        .addRow(fuelRow)
        .addRow(rangeRow)
        .addRow(confidenceRow)
        .addRow(statusRow)
        .addAction(
            Action.Builder()
                .setTitle("Find Fuel")
                .setOnClickListener(onFindFuelClick)
                .build()
        )
        .build()
}

private fun formatGallons(gallons: Double): String {
    val rounded = (gallons * 10.0).roundToInt() / 10.0
    return rounded.toString()
}

private fun ConfidenceLevel.toDisplayLabel(): String = when (this) {
    ConfidenceLevel.VERY_HIGH -> "Very High"
    ConfidenceLevel.HIGH -> "High"
    ConfidenceLevel.MEDIUM -> "Medium"
    ConfidenceLevel.LOW -> "Low"
}

private fun FuelStatus.toDisplayLabel(): String = when (this) {
    FuelStatus.NORMAL -> "Normal"
    FuelStatus.LOW -> "Low Fuel"
    FuelStatus.CRITICAL -> "Critical — Refuel Soon"
    FuelStatus.UNKNOWN -> UNAVAILABLE
}
