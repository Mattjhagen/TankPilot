package com.tankpilot.vehicle.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.tankpilot.db.TankPilotDb
import com.tankpilot.vehicle.domain.Vehicle
import com.tankpilot.vehicle.domain.VehicleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightVehicleRepository(
    private val db: TankPilotDb,
    private val dispatcher: CoroutineDispatcher
) : VehicleRepository {
    
    private val queries = db.tankPilotDbQueries

    override fun getVehicles(): Flow<List<Vehicle>> {
        return queries.getVehicles()
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun getVehicleById(id: String): Vehicle? = withContext(dispatcher) {
        queries.getVehicleById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun saveVehicle(vehicle: Vehicle) = withContext(dispatcher) {
        queries.insertVehicle(
            id = vehicle.id,
            year = vehicle.year.toLong(),
            make = vehicle.make,
            model = vehicle.model,
            engine = vehicle.engine,
            tankCapacity = vehicle.tankCapacity,
            factoryCityMpg = vehicle.factoryCityMpg,
            factoryHwyMpg = vehicle.factoryHwyMpg,
            learnedMpg = vehicle.learnedMpg
        )
    }

    override suspend fun updateLearnedMpg(vehicleId: String, learnedMpg: Double) = withContext(dispatcher) {
        queries.updateLearnedMpg(learnedMpg, vehicleId)
    }

    override suspend fun deleteVehicle(id: String) = withContext(dispatcher) {
        queries.deleteVehicle(id)
    }
}

private fun com.tankpilot.db.Vehicle.toDomain(): Vehicle {
    return Vehicle(
        id = id,
        year = year.toInt(),
        make = make,
        model = model,
        engine = engine,
        tankCapacity = tankCapacity,
        factoryCityMpg = factoryCityMpg,
        factoryHwyMpg = factoryHwyMpg,
        learnedMpg = learnedMpg
    )
}
