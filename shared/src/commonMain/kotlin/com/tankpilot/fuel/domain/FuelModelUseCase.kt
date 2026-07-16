package com.tankpilot.fuel.domain

import com.tankpilot.core.FuelStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class FuelModelUseCase(
    private val persistedFuelRemaining: StateFlow<Double>,
    private val activeFuelBurn: StateFlow<Double>,
    private val efficiencyProvider: VehicleEfficiencyProvider,
    private val confidencePercent: StateFlow<Int>,
    private val alertEngine: AlertEngine,
    private val scope: CoroutineScope
) {
    val displayedFuelRemainingGallons: StateFlow<Double> = combine(
        persistedFuelRemaining,
        activeFuelBurn
    ) { persisted, activeBurn ->
        maxOf(0.0, persisted - activeBurn)
    }.stateIn(scope, SharingStarted.Eagerly, 0.0)

    val displayedFuelPercent: StateFlow<Double?> = combine(
        displayedFuelRemainingGallons,
        efficiencyProvider.currentTankCapacityGallons
    ) { remaining, capacity ->
        if (capacity != null && capacity > 0.0) {
            (remaining / capacity).coerceIn(0.0, 1.0)
        } else {
            null
        }
    }.stateIn(scope, SharingStarted.Eagerly, null)

    // Probabilistic Ranges
    val expectedRangeMiles: StateFlow<Double> = combine(
        displayedFuelRemainingGallons,
        efficiencyProvider.currentLearnedMpg
    ) { remaining, mpg ->
        val mpgVal = mpg ?: 25.0
        remaining * mpgVal
    }.stateIn(scope, SharingStarted.Eagerly, 0.0)

    val conservativeRangeMiles: StateFlow<Double> = combine(
        displayedFuelRemainingGallons,
        efficiencyProvider.currentLearnedMpg,
        confidencePercent
    ) { remaining, mpg, confidence ->
        val mpgVal = mpg ?: 25.0
        val safetyFactor = when {
            confidence >= 90 -> 0.95
            confidence >= 75 -> 0.90
            confidence >= 60 -> 0.85
            else -> 0.80
        }
        remaining * mpgVal * safetyFactor
    }.stateIn(scope, SharingStarted.Eagerly, 0.0)

    val optimisticRangeMiles: StateFlow<Double> = combine(
        displayedFuelRemainingGallons,
        efficiencyProvider.currentLearnedMpg
    ) { remaining, mpg ->
        val mpgVal = mpg ?: 25.0
        remaining * mpgVal * 1.10
    }.stateIn(scope, SharingStarted.Eagerly, 0.0)

    val fuelStatus: StateFlow<FuelStatus> = combine(
        displayedFuelRemainingGallons,
        efficiencyProvider.currentTankCapacityGallons,
        efficiencyProvider.currentReserveFuelGallons,
        efficiencyProvider.currentLowFuelThresholdPercent
    ) { remaining, capacity, reserve, lowThreshold ->
        if (capacity == null || capacity <= 0.0) return@combine FuelStatus.UNKNOWN
        val resVal = reserve ?: 1.0
        val lowPct = lowThreshold ?: 0.15
        
        when {
            remaining <= resVal -> FuelStatus.CRITICAL
            (remaining / capacity) <= lowPct -> FuelStatus.LOW
            else -> FuelStatus.NORMAL
        }
    }.stateIn(scope, SharingStarted.Eagerly, FuelStatus.UNKNOWN)

    val warningText: StateFlow<String> = combine(
        fuelStatus,
        conservativeRangeMiles,
        confidencePercent
    ) { status, range, confidence ->
        alertEngine.determineAlertText(status, range, confidence)
    }.stateIn(scope, SharingStarted.Eagerly, "All Good")
}
