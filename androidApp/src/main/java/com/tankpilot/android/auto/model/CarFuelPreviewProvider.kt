package com.tankpilot.android.auto.model

/**
 * Fallback fuel snapshot used only when no vehicle has been configured yet
 * (production data from [com.tankpilot.fuel.domain.FuelStateUseCase] always takes
 * priority over this). Variant-bound: debug returns a fixture, release returns null.
 */
interface CarFuelPreviewProvider {
    fun previewSnapshot(): CarFuelSnapshot?
}
