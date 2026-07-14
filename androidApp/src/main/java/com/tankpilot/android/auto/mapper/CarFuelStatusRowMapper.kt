package com.tankpilot.android.auto.mapper

import com.tankpilot.android.auto.model.CarFuelSnapshot
import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.FuelStatus
import kotlin.math.roundToInt

private const val UNAVAILABLE = "Unavailable"

/**
 * Pure, platform-independent row content for the root Fuel Status screen — no
 * androidx.car.app types here so this stays unit-testable on plain JVM (those types
 * throw outside Robolectric/instrumentation; see CarFuelSnapshotPaneMapper.kt, which
 * does the thin Row/Pane translation this feeds).
 */
data class CarFuelStatusRowContent(val title: String, val text: String)

fun buildFuelStatusRowContents(snapshot: CarFuelSnapshot): List<CarFuelStatusRowContent> = with(snapshot) {
    listOf(
        CarFuelStatusRowContent(
            title = "Estimated Fuel",
            text = if (fuelPercent != null && gallonsRemaining != null) {
                "$fuelPercent% · ${formatGallons(gallonsRemaining)} gal remaining"
            } else {
                UNAVAILABLE
            }
        ),
        CarFuelStatusRowContent(
            title = "Safe Range",
            text = safeRangeMiles?.let { "${it.roundToInt()} mi" } ?: UNAVAILABLE
        ),
        CarFuelStatusRowContent(
            title = "Confidence",
            text = if (confidenceLevel != null && confidencePercent != null) {
                "${confidenceLevel.toDisplayLabel()} ($confidencePercent%)"
            } else {
                UNAVAILABLE
            }
        ),
        CarFuelStatusRowContent(
            title = "Fuel Status",
            text = fuelStatus.toDisplayLabel()
        ),
        CarFuelStatusRowContent(
            title = "Fuel Rescue",
            text = when (reachableStationCount) {
                null -> UNAVAILABLE
                0 -> "No safe stations nearby"
                1 -> "1 safe station nearby"
                else -> "$reachableStationCount safe stations nearby"
            }
        )
    )
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
