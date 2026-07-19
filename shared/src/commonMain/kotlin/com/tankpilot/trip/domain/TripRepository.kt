package com.tankpilot.trip.domain

import kotlinx.coroutines.flow.Flow

import com.tankpilot.location.domain.LocationSample

interface TripRepository {
    fun getTrips(vehicleId: String): Flow<List<Trip>>
    fun getRecentTrips(vehicleId: String, limit: Long): Flow<List<Trip>>
    suspend fun saveTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
    
    suspend fun saveTripRoutePoints(tripId: String, points: List<LocationSample>, startIndex: Int)
    suspend fun saveTripAndFinalRoute(trip: Trip, points: List<LocationSample>, startIndex: Int)
    fun getTripRoute(tripId: String): Flow<List<LocationSample>>
}
