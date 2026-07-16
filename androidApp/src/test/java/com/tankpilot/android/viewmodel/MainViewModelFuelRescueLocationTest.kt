package com.tankpilot.android.viewmodel

import com.tankpilot.core.FuelStatus
import com.tankpilot.core.FuelType
import com.tankpilot.core.UnitSystem
import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.fillup.domain.FillUpRepository
import com.tankpilot.fuel.domain.CalibrationEngine
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuelrescue.domain.FuelStation
import com.tankpilot.fuelrescue.domain.FuelStationRepository
import com.tankpilot.location.domain.LocationSample
import com.tankpilot.trip.domain.LocationPipeline
import com.tankpilot.trip.domain.Trip
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.vehicle.domain.Vehicle
import com.tankpilot.vehicle.domain.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

private class FakeVehicleRepository(initial: List<Vehicle>) : VehicleRepository {
    private val flow = MutableStateFlow(initial)
    override fun getVehicles(): Flow<List<Vehicle>> = flow
    override suspend fun getVehicleById(id: String): Vehicle? = flow.value.find { it.id == id }
    override suspend fun saveVehicle(vehicle: Vehicle) { flow.value = flow.value + vehicle }
    override suspend fun updateLearnedMpg(vehicleId: String, learnedMpg: Double) {}
    override suspend fun deleteVehicle(id: String) { flow.value = flow.value.filterNot { it.id == id } }
}

private class FakeTripRepository : TripRepository {
    override fun getTrips(vehicleId: String) = flowOf(emptyList<Trip>())
    override fun getRecentTrips(vehicleId: String, limit: Long) = flowOf(emptyList<Trip>())
    override suspend fun saveTrip(trip: Trip) {}
    override suspend fun deleteTrip(id: String) {}
}

private class FakeFillUpRepository : FillUpRepository {
    override fun getFillUps(vehicleId: String) = flowOf(emptyList<FillUp>())
    override fun getRecentFillUps(vehicleId: String, limit: Long) = flowOf(emptyList<FillUp>())
    override suspend fun saveFillUp(fillUp: FillUp) {}
    override suspend fun deleteFillUp(id: String) {}
}

/** Records every call so tests can assert exactly what coordinates (if any) reached the repository. */
private class RecordingFuelStationRepository : FuelStationRepository {
    var refreshCallCount = 0
        private set
    var lastRefreshCoordinates: Pair<Double, Double>? = null
        private set

    override fun getCachedStations() = flowOf(emptyList<FuelStation>())

    override suspend fun refreshStations(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double,
        fuelType: FuelType,
        forceRefresh: Boolean
    ): List<FuelStation> {
        refreshCallCount++
        lastRefreshCoordinates = latitude to longitude
        return emptyList()
    }

    override suspend fun clearCache() {}
}

private fun sampleVehicle() = Vehicle(
    id = "v1",
    year = 2003,
    make = "Chevrolet",
    model = "Impala",
    trim = null,
    color = null,
    engine = "3.4L V6",
    engineDisplacementLiters = 3.4,
    cylinderCount = 6,
    tankCapacity = 17.0,
    factoryCityMpg = 20.0,
    factoryHwyMpg = 29.0,
    learnedMpg = 24.0,
    preferredFuelType = FuelType.REGULAR,
    preferredFuelGrade = null,
    unitSystem = UnitSystem.IMPERIAL,
    reserveFuelGallons = 1.5,
    lowFuelThresholdPercent = 0.15
)

/**
 * Fuel Rescue on the phone must never fall back to a hardcoded coordinate (previously
 * San Francisco). Instead it must read the latest real validated GPS location and
 * expose an "unavailable" state when there isn't one yet.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainViewModelFuelRescueLocationTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(
        stationRepository: RecordingFuelStationRepository,
        locationPipeline: LocationPipeline,
        vehicle: Vehicle
    ): MainViewModel {
        val vehicleRepository = FakeVehicleRepository(listOf(vehicle))
        val tripRepository = FakeTripRepository()
        val fillUpRepository = FakeFillUpRepository()
        val scope = CoroutineScope(testDispatcher + SupervisorJob())
        val fuelStateUseCase = FuelStateUseCase(vehicleRepository, tripRepository, fillUpRepository, scope)
        val calibrationEngine = CalibrationEngine(vehicleRepository, tripRepository)
        return MainViewModel(
            vehicleRepository,
            tripRepository,
            fillUpRepository,
            stationRepository,
            fuelStateUseCase,
            calibrationEngine,
            locationPipeline
        )
    }

    @Test
    fun `refreshRescue never falls back to a hardcoded coordinate when no location is available`() {
        val stationRepository = RecordingFuelStationRepository()
        val locationPipeline = LocationPipeline(scope = CoroutineScope(testDispatcher + SupervisorJob()))
        val viewModel = buildViewModel(stationRepository, locationPipeline, sampleVehicle())

        viewModel.refreshRescue(force = false)

        assertEquals(0, stationRepository.refreshCallCount)
        assertNull(stationRepository.lastRefreshCoordinates)
        assertTrue(viewModel.isRescueLocationUnavailable.value)
        assertTrue(viewModel.recommendations.value.isEmpty())
    }

    @Test
    fun `refreshRescue uses the real validated GPS location once one is available`() {
        val stationRepository = RecordingFuelStationRepository()
        val locationPipeline = LocationPipeline(scope = CoroutineScope(testDispatcher + SupervisorJob()))
        val viewModel = buildViewModel(stationRepository, locationPipeline, sampleVehicle())

        val now = Clock.System.now()
        locationPipeline.onRawLocationUpdate(
            LocationSample(
                timestamp = now,
                latitude = 44.9778,
                longitude = -93.2650,
                speedKmh = 0.0,
                speedAccuracyMps = 0.5,
                horizontalAccuracyMeters = 5.0,
                bearingDegrees = null
            ),
            currentWallClockTime = now
        )

        viewModel.refreshRescue(force = false)

        assertEquals(1, stationRepository.refreshCallCount)
        assertEquals(44.9778 to -93.2650, stationRepository.lastRefreshCoordinates)
        assertFalse(viewModel.isRescueLocationUnavailable.value)
    }
}
