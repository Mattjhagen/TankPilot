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
        queries.transaction {
            queries.insertTrip(
                id = trip.id,
                vehicleId = trip.vehicleId,
                timestamp = trip.timestamp,
                distance = trip.distance,
                duration = trip.duration,
                idleTime = trip.idleTime,
                averageSpeed = trip.averageSpeed,
                drivingType = trip.drivingType.name,
                fuelBurned = trip.fuelBurned,
                maxSpeedKmh = trip.maxSpeedKmh,
                highwayPercentage = trip.highwayPercentage
            )
        }
    }

    override suspend fun deleteTrip(id: String) = withContext(dispatcher) {
        queries.deleteTrip(id)
    }

    override suspend fun saveTripRoutePoints(
        tripId: String,
        points: List<com.tankpilot.location.domain.LocationSample>,
        startIndex: Int
    ) = withContext(dispatcher) {
        queries.transaction {
            points.forEachIndexed { index, point ->
                queries.insertTripRoutePoint(
                    tripId = tripId,
                    sequenceIndex = (startIndex + index).toLong(),
                    timestamp = point.timestamp.toEpochMilliseconds(),
                    latitude = point.latitude,
                    longitude = point.longitude,
                    horizontalAccuracyMeters = point.horizontalAccuracyMeters,
                    speedKmh = point.speedKmh
                )
            }
        }
    }
    
    override suspend fun saveTripAndFinalRoute(
        trip: Trip,
        points: List<com.tankpilot.location.domain.LocationSample>,
        startIndex: Int
    ) = withContext(dispatcher) {
        queries.transaction {
            queries.insertTrip(
                id = trip.id,
                vehicleId = trip.vehicleId,
                timestamp = trip.timestamp,
                distance = trip.distance,
                duration = trip.duration,
                idleTime = trip.idleTime,
                averageSpeed = trip.averageSpeed,
                drivingType = trip.drivingType.name,
                fuelBurned = trip.fuelBurned,
                maxSpeedKmh = trip.maxSpeedKmh,
                highwayPercentage = trip.highwayPercentage
            )
            points.forEachIndexed { index, point ->
                queries.insertTripRoutePoint(
                    tripId = trip.id,
                    sequenceIndex = (startIndex + index).toLong(),
                    timestamp = point.timestamp.toEpochMilliseconds(),
                    latitude = point.latitude,
                    longitude = point.longitude,
                    horizontalAccuracyMeters = point.horizontalAccuracyMeters,
                    speedKmh = point.speedKmh
                )
            }
        }
    }

    override fun getTripRoute(tripId: String): Flow<List<com.tankpilot.location.domain.LocationSample>> {
        return queries.getRouteForTrip(tripId)
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { routePoint ->
                    com.tankpilot.location.domain.LocationSample(
                        timestamp = kotlinx.datetime.Instant.fromEpochMilliseconds(routePoint.timestamp),
                        latitude = routePoint.latitude,
                        longitude = routePoint.longitude,
                        speedKmh = routePoint.speedKmh,
                        speedAccuracyMps = null,
                        horizontalAccuracyMeters = routePoint.horizontalAccuracyMeters,
                        bearingDegrees = null
                    )
                }
            }
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
        fuelBurned = fuelBurned,
        maxSpeedKmh = maxSpeedKmh,
        highwayPercentage = highwayPercentage
    )
}
