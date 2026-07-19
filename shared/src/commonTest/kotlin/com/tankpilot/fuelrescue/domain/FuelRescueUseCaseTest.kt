package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.FuelType
import com.tankpilot.core.UnitSystem
import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.fillup.domain.FillUpRepository
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuelrescue.data.NoOpFuelRescueScenarioOverrideProvider
import com.tankpilot.trip.domain.Trip
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.vehicle.domain.Vehicle
import com.tankpilot.vehicle.domain.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.flowOf

/**
 * "Missing station data does not crash" from phases/phase-03a-android-auto
 * -foundation.md — a station-provider failure (offline, provider error) must not
 * propagate out of FuelRescueUseCase.refresh() and must not fabricate results.
 */
class FuelRescueUseCaseTest {

    private class InMemoryVehicleRepository(vehicle: Vehicle) : VehicleRepository {
        private val vehicles = MutableStateFlow(listOf(vehicle))
        override fun getVehicles(): Flow<List<Vehicle>> = vehicles
        override suspend fun getVehicleById(id: String): Vehicle? = vehicles.value.firstOrNull { it.id == id }
        override suspend fun saveVehicle(vehicle: Vehicle) { vehicles.value = listOf(vehicle) }
        override suspend fun updateLearnedMpg(vehicleId: String, learnedMpg: Double) {}
        override suspend fun deleteVehicle(id: String) { vehicles.value = emptyList() }
    }

    private class InMemoryTripRepository : com.tankpilot.trip.domain.TripRepository {
        val trips = mutableListOf<Trip>()
        override fun getTrips(vehicleId: String): Flow<List<Trip>> = flowOf(trips.filter { it.vehicleId == vehicleId })
        override fun getRecentTrips(vehicleId: String, limit: Long): Flow<List<Trip>> = flowOf(trips.filter { it.vehicleId == vehicleId }.take(limit.toInt()))
        override suspend fun saveTrip(trip: Trip) { trips.add(trip) }
        override suspend fun deleteTrip(id: String) { trips.removeIf { it.id == id } }
        override suspend fun saveTripRoutePoints(tripId: String, points: List<com.tankpilot.location.domain.LocationSample>, startIndex: Int) {}
        override suspend fun saveTripAndFinalRoute(trip: Trip, points: List<com.tankpilot.location.domain.LocationSample>, startIndex: Int) { saveTrip(trip); saveTripRoutePoints(trip.id, points, startIndex) }
        override fun getTripRoute(tripId: String): Flow<List<com.tankpilot.location.domain.LocationSample>> = kotlinx.coroutines.flow.emptyFlow()
    }

    private class InMemoryFillUpRepository : FillUpRepository {
        override fun getFillUps(vehicleId: String): Flow<List<FillUp>> = MutableStateFlow(emptyList())
        override fun getRecentFillUps(vehicleId: String, limit: Long): Flow<List<FillUp>> = emptyFlow()
        override suspend fun saveFillUp(fillUp: FillUp) {}
        override suspend fun deleteFillUp(id: String) {}
    }

    private class ThrowingFuelStationRepository : FuelStationRepository {
        override fun getCachedStations(): Flow<List<FuelStation>> = MutableStateFlow(emptyList())
        override suspend fun refreshStations(
            latitude: Double,
            longitude: Double,
            radiusMiles: Double,
            fuelType: FuelType,
            forceRefresh: Boolean
        ): List<FuelStation> {
            throw RuntimeException("Simulated offline — no network available")
        }
        override suspend fun clearCache() {}
    }

    private class EmptyFuelStationRepository : FuelStationRepository {
        override fun getCachedStations(): Flow<List<FuelStation>> = MutableStateFlow(emptyList())
        override suspend fun refreshStations(
            latitude: Double,
            longitude: Double,
            radiusMiles: Double,
            fuelType: FuelType,
            forceRefresh: Boolean
        ): List<FuelStation> = emptyList()
        override suspend fun clearCache() {}
    }

    private fun testVehicle() = Vehicle(
        id = "v1", year = 2020, make = "Honda", model = "Civic", trim = null, color = null,
        engine = "2.0L", engineDisplacementLiters = 2.0, cylinderCount = 4,
        tankCapacity = 12.0, factoryCityMpg = 30.0, factoryHwyMpg = 38.0, learnedMpg = 32.0,
        preferredFuelType = FuelType.REGULAR, preferredFuelGrade = "regular",
        unitSystem = UnitSystem.IMPERIAL, reserveFuelGallons = 1.0, lowFuelThresholdPercent = 0.2
    )

    @Test
    fun missingStationDataDoesNotCrashAndLeavesRecommendationsEmpty() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val fuelStateUseCase = FuelStateUseCase(
            vehicleRepository = InMemoryVehicleRepository(testVehicle()),
            tripRepository = InMemoryTripRepository(),
            fillUpRepository = InMemoryFillUpRepository(),
            scope = scope
        )
        val useCase = FuelRescueUseCase(
            fuelStationRepository = ThrowingFuelStationRepository(),
            fuelStateUseCase = fuelStateUseCase,
            scenarioOverrideProvider = NoOpFuelRescueScenarioOverrideProvider(),
            scope = scope
        )

        // currentVehicle is SharingStarted.Lazily — force real collection to start
        // before relying on .value, or it never leaves its initial null.
        fuelStateUseCase.currentVehicle.first { it != null }

        // Must not throw.
        useCase.refresh(latitude = 37.7749, longitude = -122.4194, forceRefresh = true)

        assertTrue(useCase.recommendations.value.isEmpty(), "recommendations should stay empty")
        assertFalse(useCase.isRefreshing.value, "isRefreshing should be false after completion")
        assertTrue(useCase.hasLoadedOnce.value, "hasLoadedOnce should be true after completion")
        // A fetch failure must read as "unavailable," never "confirmed zero stations."
        // reachableSafeStationCount is WhileSubscribed — .first() forces a real read
        // of the current computed value instead of an unsubscribed stale default.
        assertEquals(null, useCase.reachableSafeStationCount.first())
    }

    @Test
    fun successfulRefreshWithNoStationsReportsConfirmedZeroNotUnavailable() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val fuelStateUseCase = FuelStateUseCase(
            vehicleRepository = InMemoryVehicleRepository(testVehicle()),
            tripRepository = InMemoryTripRepository(),
            fillUpRepository = InMemoryFillUpRepository(),
            scope = scope
        )
        val useCase = FuelRescueUseCase(
            fuelStationRepository = EmptyFuelStationRepository(),
            fuelStateUseCase = fuelStateUseCase,
            scenarioOverrideProvider = NoOpFuelRescueScenarioOverrideProvider(),
            scope = scope
        )

        fuelStateUseCase.currentVehicle.first { it != null }
        useCase.refresh(latitude = 37.7749, longitude = -122.4194, forceRefresh = true)

        // Unlike the failure case above, a clean fetch that genuinely found nothing
        // is a confirmed 0, not an unavailable null.
        assertEquals(0, useCase.reachableSafeStationCount.first())
    }
}
