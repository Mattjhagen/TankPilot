package com.tankpilot.android.auto.mapper

import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarFuelSnapshot
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import kotlin.math.roundToInt

/**
 * Builds a [CarFuelSnapshot] from the shared, production [FuelStateUseCase] and
 * [FuelRescueUseCase] — the same use cases the phone app reads. No fuel/confidence/
 * reachability math is reimplemented here.
 *
 * [carFuelPreviewProvider] is checked first, but it is not a "no vehicle yet" fallback
 * on its own — it only ever returns non-null in debug builds, and only when a Test Lab
 * DHU scenario has explicitly opted in (phases/phase-03a-android-auto-foundation.md,
 * Phase 3A.3). Release's implementation always returns null, so production data is
 * always what release Android Auto shows.
 */
fun buildCarFuelSnapshot(
    fuelStateUseCase: FuelStateUseCase,
    fuelRescueUseCase: FuelRescueUseCase,
    carFuelPreviewProvider: CarFuelPreviewProvider
): CarFuelSnapshot {
    carFuelPreviewProvider.previewSnapshot()?.let { return it }

    val vehicle = fuelStateUseCase.currentVehicle.value
        ?: return CarFuelSnapshot.unavailable()

    val remaining = fuelStateUseCase.estimatedFuelRemaining.value
    val fuelPercent = if (vehicle.tankCapacity > 0.0) {
        ((remaining.value / vehicle.tankCapacity) * 100.0).roundToInt().coerceIn(0, 100)
    } else {
        null
    }

    return CarFuelSnapshot(
        vehicleLabel = "${vehicle.year} ${vehicle.make} ${vehicle.model}",
        fuelPercent = fuelPercent,
        gallonsRemaining = remaining.value,
        safeRangeMiles = fuelStateUseCase.safeRange.value.value,
        confidencePercent = fuelStateUseCase.confidencePercent.value,
        confidenceLevel = fuelStateUseCase.confidence.value,
        fuelStatus = fuelStateUseCase.fuelStatus.value,
        reachableStationCount = fuelRescueUseCase.reachableSafeStationCount.value,
        drivingPattern = null,
        mpgValue = null,
        mpgSource = null,
        alertsText = null,
        isPreviewFixture = false
    )
}
