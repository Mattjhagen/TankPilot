package com.tankpilot.android.auto.model

import com.tankpilot.core.GeoCoordinate

/**
 * Current vehicle position for the Android Auto Fuel Rescue flow. Variant-bound like
 * [CarFuelPreviewProvider]: debug returns a fixed preview coordinate, release attempts
 * a real device location and returns null (never a fabricated coordinate) when one
 * isn't available.
 */
interface CarLocationSource {
    fun currentLocationOrNull(): GeoCoordinate?
}
