package com.tankpilot.fuel.domain

import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.trip.domain.Trip
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.trip.domain.DrivingType
import com.tankpilot.vehicle.domain.Vehicle
import com.tankpilot.vehicle.domain.VehicleRepository
import com.tankpilot.core.FuelType
import com.tankpilot.core.UnitSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class CalibrationSegmentTest {

    private class TestVehicleRepository(private var vehicle: Vehicle) : VehicleRepository {
        var lastUpdatedLearnedMpg: Double? = null
        override fun getVehicles(): Flow<List<Vehicle>> = flowOf(listOf(vehicle))
        override suspend fun getVehicleById(id: String): Vehicle? = vehicle
        override suspend fun saveVehicle(vehicle: Vehicle) { this.vehicle = vehicle }
        override suspend fun updateLearnedMpg(vehicleId: String, learnedMpg: Double) {
            lastUpdatedLearnedMpg = learnedMpg
            vehicle = vehicle.copy(learnedMpg = learnedMpg)
        }
        override suspend fun deleteVehicle(id: String) {}
    }

    private class TestTripRepository : com.tankpilot.trip.domain.TripRepository {
        val trips = mutableListOf<Trip>()
        override fun getTrips(vehicleId: String): Flow<List<Trip>> = flowOf(trips)
        override fun getRecentTrips(vehicleId: String, limit: Long): Flow<List<Trip>> = flowOf(trips.take(limit.toInt()))
        override suspend fun saveTrip(trip: Trip) { trips.add(trip) }
        override suspend fun deleteTrip(id: String) { trips.removeIf { it.id == id } }
        override suspend fun saveTripRoutePoints(tripId: String, points: List<com.tankpilot.location.domain.LocationSample>, startIndex: Int) {}
        override suspend fun saveTripAndFinalRoute(trip: Trip, points: List<com.tankpilot.location.domain.LocationSample>, startIndex: Int) { saveTrip(trip); saveTripRoutePoints(trip.id, points, startIndex) }
        override fun getTripRoute(tripId: String): Flow<List<com.tankpilot.location.domain.LocationSample>> = kotlinx.coroutines.flow.emptyFlow()
    }

    private lateinit var vehicle: Vehicle
    private lateinit var vehicleRepo: TestVehicleRepository
    private lateinit var tripRepo: TestTripRepository
    private lateinit var engine: CalibrationEngine

    @BeforeTest
    fun setUp() {
        vehicle = Vehicle(
            id = "v1", year = 2020, make = "Honda", model = "Civic", trim = null, color = null,
            engine = "2.0L", engineDisplacementLiters = 2.0, cylinderCount = 4,
            tankCapacity = 12.0, factoryCityMpg = 30.0, factoryHwyMpg = 38.0, learnedMpg = 32.0,
            preferredFuelType = FuelType.REGULAR, preferredFuelGrade = "regular",
            unitSystem = UnitSystem.IMPERIAL, reserveFuelGallons = 1.5, lowFuelThresholdPercent = 0.15
        )
        vehicleRepo = TestVehicleRepository(vehicle)
        tripRepo = TestTripRepository()
        engine = CalibrationEngine(vehicleRepo, tripRepo)
    }

    @Test
    fun testFullToFullCalibrationUpdatesLearnedMpg() = runBlocking {
        val prevFill = FillUp("f1", "v1", 1000L, 10.0, 3.50, odometer = 10000.0, isFull = true)
        val currentFill = FillUp("f2", "v1", 2000L, 10.0, 3.50, odometer = 10300.0, isFull = true)

        val segment = engine.calibrate(vehicle, currentFill, listOf(prevFill))
        
        assertNotNull(segment)
        assertEquals(300.0, segment.accumulatedDistance)
        assertEquals(10.0, segment.allGallonsAddedBetween)
        
        // Measured MPG: 300 / 10 = 30 MPG
        // newLearnedMpg = (0.3 * 30) + (0.7 * 32) = 9.0 + 22.4 = 31.4 MPG
        assertEquals(31.4, vehicleRepo.lastUpdatedLearnedMpg)
    }

    @Test
    fun testPartialFillsAccumulateBeforeClosing() = runBlocking {
        val prevFill = FillUp("f1", "v1", 1000L, 10.0, 3.50, odometer = 10000.0, isFull = true)
        val partialFill = FillUp("f2", "v1", 1500L, 4.0, 3.50, odometer = 10120.0, isFull = false)
        val currentFill = FillUp("f3", "v1", 2000L, 6.0, 3.50, odometer = 10300.0, isFull = true)

        // Running calibration with partial fill doesn't close it yet (needs full fill at end)
        val segment = engine.calibrate(vehicle, currentFill, listOf(prevFill, partialFill))
        
        assertNotNull(segment)
        assertEquals(300.0, segment.accumulatedDistance)
        // total gallons: partial (4.0) + ending full (6.0) = 10.0 gallons
        assertEquals(10.0, segment.allGallonsAddedBetween)
        assertEquals(31.4, vehicleRepo.lastUpdatedLearnedMpg)
    }

    @Test
    fun testPartialFillWithoutPriorFullDoesNotCalibrate() = runBlocking {
        val partialFill = FillUp("f1", "v1", 1000L, 5.0, 3.50, odometer = 10000.0, isFull = false)
        val currentFill = FillUp("f2", "v1", 2000L, 5.0, 3.50, odometer = 10150.0, isFull = true)

        val segment = engine.calibrate(vehicle, currentFill, listOf(partialFill))
        assertNull(segment) // No prior full fill
    }

    @Test
    fun testImplausibleMpgRejected() = runBlocking {
        val prevFill = FillUp("f1", "v1", 1000L, 1.0, 3.50, odometer = 10000.0, isFull = true)
        val currentFill = FillUp("f2", "v1", 2000L, 1.0, 3.50, odometer = 10200.0, isFull = true) // 200 MPG!

        val segment = engine.calibrate(vehicle, currentFill, listOf(prevFill))
        assertNull(segment) // Implausible (> 80 MPG) rejected
    }
}
