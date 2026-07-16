package com.tankpilot.android.auto.model

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.FuelStatus

/**
 * Presentation-ready snapshot for the Android Auto root screen. All fields are
 * nullable except [fuelStatus] and [isPreviewFixture] — a null field must render as
 * "Unavailable" in the car UI, never as zero.
 */
data class CarFuelSnapshot(
    val vehicleLabel: String?,
    val fuelPercent: Int?,
    val gallonsRemaining: Double?,
    val conservativeRangeMiles: Double?,
    val expectedRangeMiles: Double?,
    val confidencePercent: Int?,
    val confidenceLevel: ConfidenceLevel?,
    val fuelStatus: FuelStatus,
    val reachableStationCount: Int?,
    val drivingPattern: String?,
    val mpgValue: Double?,
    val mpgSource: String?,
    val alertsText: String?,
    val isTrackingActive: Boolean,
    val isLocationUnavailable: Boolean,
    val isPreviewFixture: Boolean
) {
    companion object {
        fun unavailable(): CarFuelSnapshot = CarFuelSnapshot(
            vehicleLabel = null,
            fuelPercent = null,
            gallonsRemaining = null,
            conservativeRangeMiles = null,
            expectedRangeMiles = null,
            confidencePercent = null,
            confidenceLevel = null,
            fuelStatus = FuelStatus.UNKNOWN,
            reachableStationCount = null,
            drivingPattern = null,
            mpgValue = null,
            mpgSource = null,
            alertsText = null,
            isTrackingActive = false,
            isLocationUnavailable = true,
            isPreviewFixture = false
        )
    }
}
