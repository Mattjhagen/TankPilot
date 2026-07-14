package com.tankpilot.trip.domain

import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTrips(vehicleId: String): Flow<List<Trip>>
    fun getRecentTrips(vehicleId: String, limit: Long): Flow<List<Trip>>
    suspend fun saveTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
}
