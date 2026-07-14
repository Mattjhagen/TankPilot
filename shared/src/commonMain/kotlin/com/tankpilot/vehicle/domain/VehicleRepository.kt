package com.tankpilot.vehicle.domain

import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: String): Vehicle?
    suspend fun saveVehicle(vehicle: Vehicle)
    suspend fun updateLearnedMpg(vehicleId: String, learnedMpg: Double)
    suspend fun deleteVehicle(id: String)
}
