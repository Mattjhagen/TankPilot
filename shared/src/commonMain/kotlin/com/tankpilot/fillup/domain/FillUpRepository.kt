package com.tankpilot.fillup.domain

import kotlinx.coroutines.flow.Flow

interface FillUpRepository {
    fun getFillUps(vehicleId: String): Flow<List<FillUp>>
    fun getRecentFillUps(vehicleId: String, limit: Long): Flow<List<FillUp>>
    suspend fun saveFillUp(fillUp: FillUp)
    suspend fun deleteFillUp(id: String)
}
