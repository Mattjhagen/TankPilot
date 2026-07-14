package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.FuelType

interface FuelStationProvider {
    suspend fun getNearbyStations(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double,
        fuelType: FuelType
    ): List<FuelStation>
}
