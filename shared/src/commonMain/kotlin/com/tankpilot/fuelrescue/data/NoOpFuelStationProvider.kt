package com.tankpilot.fuelrescue.data

import com.tankpilot.core.FuelType
import com.tankpilot.fuelrescue.domain.FuelStation
import com.tankpilot.fuelrescue.domain.FuelStationProvider

/**
 * Release provider for fuel station lookup when no map API is configured.
 * Always returns an empty list. FuelRescueScreen will display a "no stations found" state.
 * Never crashes or throws — a missing API key must not break app startup.
 */
class NoOpFuelStationProvider : FuelStationProvider {
    override suspend fun getNearbyStations(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double,
        fuelType: FuelType
    ): List<FuelStation> = emptyList()
}
