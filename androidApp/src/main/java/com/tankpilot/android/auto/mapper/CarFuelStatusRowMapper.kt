package com.tankpilot.android.auto.mapper

import com.tankpilot.android.auto.model.CarFuelSnapshot
import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.FuelStatus
import kotlin.math.roundToInt

private const val UNAVAILABLE = "Unavailable"
private const val START_DRIVE_INSTRUCTION = "Open TankPilot on phone and tap Start Drive."

/**
 * Pure, platform-independent row content for the root Fuel Status screen — no
 * androidx.car.app types here so this stays unit-testable on plain JVM (those types
 * throw outside Robolectric/instrumentation; see CarFuelSnapshotPaneMapper.kt, which
 * does the thin Row/Pane translation this feeds).
 *
 * Exactly 4 rows, matching the Car App Library's distraction-safe row guidance — no
 * fuel/range/MPG/alert math happens here, only formatting of values [CarFuelSnapshot]
 * already carries from FuelModelUseCase/FuelStateUseCase/FuelRescueUseCase.
 */
data class CarFuelStatusRowContent(val title: String, val text: String)

fun buildFuelStatusRowContents(snapshot: CarFuelSnapshot): List<CarFuelStatusRowContent> = with(snapshot) {
    listOf(
        canKeepDrivingRow(this),
        conservativeRangeRow(this),
        drivingRow(this),
        fuelRescueOrTrackingRow(this)
    )
}

private fun canKeepDrivingRow(snapshot: CarFuelSnapshot): CarFuelStatusRowContent = with(snapshot) {
    val text = if (fuelPercent != null && gallonsRemaining != null) {
        val statusLabel = fuelStatus.toDisplayLabel()
        val alert = alertsText?.takeIf { it.isNotBlank() }
        buildString {
            append("$fuelPercent% (${formatGallons(gallonsRemaining)} gal) — $statusLabel")
            if (alert != null) append(". $alert")
        }
    } else {
        UNAVAILABLE
    }
    CarFuelStatusRowContent(title = "Can I Keep Driving?", text = text)
}

private fun conservativeRangeRow(snapshot: CarFuelSnapshot): CarFuelStatusRowContent = with(snapshot) {
    val text = if (conservativeRangeMiles != null) {
        buildString {
            append("${conservativeRangeMiles.roundToInt()} mi")
            expectedRangeMiles?.let { append(", up to ${it.roundToInt()} mi") }
            if (confidenceLevel != null && confidencePercent != null) {
                append(" — ${confidenceLevel.toDisplayLabel()} confidence ($confidencePercent%)")
            }
        }
    } else {
        UNAVAILABLE
    }
    CarFuelStatusRowContent(title = "Conservative Range", text = text)
}

private fun drivingRow(snapshot: CarFuelSnapshot): CarFuelStatusRowContent = with(snapshot) {
    val patternText = drivingPattern ?: UNAVAILABLE
    val mpgText = mpgValue?.let { "${formatGallons(it)} MPG (${mpgSource ?: "Unknown"})" } ?: UNAVAILABLE
    CarFuelStatusRowContent(title = "Driving", text = "$patternText · $mpgText")
}

private fun fuelRescueOrTrackingRow(snapshot: CarFuelSnapshot): CarFuelStatusRowContent = with(snapshot) {
    val text = when {
        !isTrackingActive -> START_DRIVE_INSTRUCTION
        isLocationUnavailable -> "Location unavailable"
        else -> when (reachableStationCount) {
            null -> UNAVAILABLE
            0 -> "No safe stations nearby"
            1 -> "1 safe station nearby"
            else -> "$reachableStationCount safe stations nearby"
        }
    }
    CarFuelStatusRowContent(title = "Fuel Rescue", text = text)
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
