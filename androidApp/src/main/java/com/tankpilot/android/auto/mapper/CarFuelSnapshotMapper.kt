package com.tankpilot.android.auto.mapper

import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarFuelSnapshot
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.fuel.domain.FuelModelUseCase
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import com.tankpilot.trip.domain.ActiveTripState
import com.tankpilot.trip.domain.DrivingSessionState
import kotlin.math.roundToInt

/**
 * Builds a [CarFuelSnapshot] from the shared, production [FuelModelUseCase] (the same
 * canonical fuel/range/status/alert source the phone Dashboard reads — see
 * DashboardViewModel), [FuelStateUseCase] (vehicle identity and confidence — the actual
 * canonical source for those), and [FuelRescueUseCase] — no fuel/range/MPG/alert math is
 * reimplemented here.
 *
 * [carFuelPreviewProvider] is checked first, but it is not a "no vehicle yet" fallback
 * on its own — it only ever returns non-null in debug builds, and only when a Test Lab
 * DHU scenario has explicitly opted in (phases/phase-03a-android-auto-foundation.md,
 * Phase 3A.3). Release's implementation always returns null, so production data is
 * always what release Android Auto shows.
 */
fun buildCarFuelSnapshot(
    fuelStateUseCase: FuelStateUseCase,
    fuelModelUseCase: FuelModelUseCase,
    fuelRescueUseCase: FuelRescueUseCase,
    carFuelPreviewProvider: CarFuelPreviewProvider,
    carLocationSource: CarLocationSource,
    sessionState: DrivingSessionState?
): CarFuelSnapshot {
    carFuelPreviewProvider.previewSnapshot()?.let { return it }

    val vehicle = fuelStateUseCase.currentVehicle.value
        ?: return CarFuelSnapshot.unavailable()

    val isTrackingActive = sessionState?.activeTripState == ActiveTripState.ACTIVE ||
        sessionState?.activeTripState == ActiveTripState.START_CANDIDATE

    return CarFuelSnapshot(
        vehicleLabel = "${vehicle.year} ${vehicle.make} ${vehicle.model}",
        fuelPercent = fuelModelUseCase.displayedFuelPercent.value?.let {
            (it * 100.0).roundToInt().coerceIn(0, 100)
        },
        gallonsRemaining = fuelModelUseCase.displayedFuelRemainingGallons.value,
        conservativeRangeMiles = fuelModelUseCase.conservativeRangeMiles.value,
        expectedRangeMiles = fuelModelUseCase.expectedRangeMiles.value,
        confidencePercent = fuelStateUseCase.confidencePercent.value,
        confidenceLevel = fuelStateUseCase.confidence.value,
        fuelStatus = fuelModelUseCase.fuelStatus.value,
        reachableStationCount = fuelRescueUseCase.reachableSafeStationCount.value,
        drivingPattern = sessionState?.drivingPattern?.name,
        mpgValue = sessionState?.mpgEstimate?.value,
        mpgSource = sessionState?.mpgEstimate?.source?.name,
        alertsText = fuelModelUseCase.warningText.value,
        isTrackingActive = isTrackingActive,
        isLocationUnavailable = carLocationSource.currentLocationOrNull() == null,
        isPreviewFixture = false
    )
}
