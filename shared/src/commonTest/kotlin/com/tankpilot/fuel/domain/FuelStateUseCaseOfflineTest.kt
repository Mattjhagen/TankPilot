package com.tankpilot.fuel.domain

import com.tankpilot.core.FuelStatus
import com.tankpilot.core.FuelType
import com.tankpilot.core.UnitSystem
import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.fillup.domain.FillUpRepository
import com.tankpilot.trip.domain.Trip
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.vehicle.domain.Vehicle
import com.tankpilot.vehicle.domain.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Proves fuel status is derivable purely from local repositories — no
 * FuelStationRepository, no network-touching code anywhere in the dependency graph —
 * i.e. "offline mode still displays fuel status" holds structurally, not just by
 * accident of a particular provider being swapped out.
 */
class FuelStateUseCaseOfflineTest {

    private class InMemoryVehicleRepository(vehicle: Vehicle) : VehicleRepository {
        private val vehicles = MutableStateFlow(listOf(vehicle))
        override fun getVehicles(): Flow<List<Vehicle>> = vehicles
        override suspend fun getVehicleById(id: String): Vehicle? = vehicles.value.firstOrNull { it.id == id }
        override suspend fun saveVehicle(vehicle: Vehicle) { vehicles.value = listOf(vehicle) }
        override suspend fun updateLearnedMpg(vehicleId: String, learnedMpg: Double) {}
        override suspend fun deleteVehicle(id: String) { vehicles.value = emptyList() }
    }

    private class InMemoryTripRepository(trips: List<Trip>) : TripRepository {
        private val flow = MutableStateFlow(trips)
        override fun getTrips(vehicleId: String): Flow<List<Trip>> = flow
        override fun getRecentTrips(vehicleId: String, limit: Long): Flow<List<Trip>> = flow
        override suspend fun saveTrip(trip: Trip) {}
        override suspend fun deleteTrip(id: String) {}
    }

    private class InMemoryFillUpRepository(fillUps: List<FillUp>) : FillUpRepository {
        private val flow = MutableStateFlow(fillUps)
        override fun getFillUps(vehicleId: String): Flow<List<FillUp>> = flow
        override fun getRecentFillUps(vehicleId: String, limit: Long): Flow<List<FillUp>> = flow
        override suspend fun saveFillUp(fillUp: FillUp) {}
        override suspend fun deleteFillUp(id: String) {}
    }

    @Test
    fun fuelStatusComputesWithoutAnyStationOrNetworkDependency() = runBlocking {
        val vehicle = Vehicle(
            id = "v1", year = 2020, make = "Honda", model = "Civic", trim = null, color = null,
            engine = "2.0L", engineDisplacementLiters = 2.0, cylinderCount = 4,
            tankCapacity = 12.0, factoryCityMpg = 30.0, factoryHwyMpg = 38.0, learnedMpg = 32.0,
            preferredFuelType = FuelType.REGULAR, preferredFuelGrade = "regular",
            unitSystem = UnitSystem.IMPERIAL, reserveFuelGallons = 1.0, lowFuelThresholdPercent = 0.2
        )
        val fillUp = FillUp(
            id = "f1", vehicleId = "v1", timestamp = 1000L, gallonsAdded = 12.0,
            price = 3.50, odometer = 10000.0, isFull = true
        )

        val scope = CoroutineScope(Dispatchers.Unconfined)
        val useCase = FuelStateUseCase(
            vehicleRepository = InMemoryVehicleRepository(vehicle),
            tripRepository = InMemoryTripRepository(emptyList()),
            fillUpRepository = InMemoryFillUpRepository(listOf(fillUp)),
            scope = scope
        )

        val remaining = useCase.estimatedFuelRemaining.first()
        val status = useCase.fuelStatus.first()

        assertEquals(12.0, remaining.value)
        assertEquals(FuelStatus.NORMAL, status)
    }
}
