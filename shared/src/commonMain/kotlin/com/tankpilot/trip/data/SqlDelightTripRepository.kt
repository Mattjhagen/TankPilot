package com.tankpilot.trip.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.tankpilot.db.TankPilotDb
import com.tankpilot.trip.domain.DrivingType
import com.tankpilot.trip.domain.Trip
import com.tankpilot.trip.domain.TripRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightTripRepository(
    private val db: TankPilotDb,
    private val dispatcher: CoroutineDispatcher
) : TripRepository {
    
    private val queries = db.tankPilotDbQueries

    override fun getTrips(vehicleId: String): Flow<List<Trip>> {
        return queries.getTripsForVehicle(vehicleId)
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override fun getRecentTrips(vehicleId: String, limit: Long): Flow<List<Trip>> {
        return queries.getRecentTripsForVehicle(vehicleId, limit)
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun saveTrip(trip: Trip) = withContext(dispatcher) {
        queries.insertTrip(
            id = trip.id,
            vehicleId = trip.vehicleId,
            timestamp = trip.timestamp,
            distance = trip.distance,
            duration = trip.duration,
            idleTime = trip.idleTime,
            averageSpeed = trip.averageSpeed,
            drivingType = trip.drivingType.name,
            fuelBurned = trip.fuelBurned
        )
    }

    override suspend fun deleteTrip(id: String) = withContext(dispatcher) {
        queries.deleteTrip(id)
    }
}

private fun com.tankpilot.db.Trip.toDomain(): Trip {
    return Trip(
        id = id,
        vehicleId = vehicleId,
        timestamp = timestamp,
        distance = distance,
        duration = duration,
        idleTime = idleTime,
        averageSpeed = averageSpeed,
        drivingType = DrivingType.valueOf(drivingType),
        fuelBurned = fuelBurned
    )
}
