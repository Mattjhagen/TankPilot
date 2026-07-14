package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.FuelType
import kotlinx.coroutines.flow.Flow

interface FuelStationRepository {
    fun getCachedStations(): Flow<List<FuelStation>>
    
    suspend fun refreshStations(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double,
        fuelType: FuelType,
        forceRefresh: Boolean
    ): List<FuelStation>
    
    suspend fun clearCache()
}
