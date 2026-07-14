package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.Gallons

/**
 * Debug-only testing hook for FuelRescueUseCase's *inputs* (remaining fuel,
 * confidence) — never for FuelRescueEngine's ranking math, which always runs
 * unmodified. Release always binds NoOpFuelRescueScenarioOverrideProvider.
 */
data class FuelRescueScenarioOverride(
    val estimatedRemaining: Gallons,
    val confidenceLevel: ConfidenceLevel
)

interface FuelRescueScenarioOverrideProvider {
    fun overrideOrNull(): FuelRescueScenarioOverride?
}
