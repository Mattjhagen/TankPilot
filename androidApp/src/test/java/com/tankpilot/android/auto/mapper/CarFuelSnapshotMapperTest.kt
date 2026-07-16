package com.tankpilot.android.auto.mapper

import com.tankpilot.android.auto.model.CarFuelPreviewProvider
import com.tankpilot.android.auto.model.CarFuelSnapshot
import com.tankpilot.android.auto.model.CarLocationSource
import com.tankpilot.core.FuelType
import com.tankpilot.core.GeoCoordinate
import com.tankpilot.core.UnitSystem
import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.fillup.domain.FillUpRepository
import com.tankpilot.fuel.domain.AlertEngine
import com.tankpilot.fuel.domain.FuelModelUseCase
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.fuel.domain.MpgEstimate
import com.tankpilot.fuel.domain.MpgEstimateSource
import com.tankpilot.fuel.domain.VehicleEfficiencyProvider
import com.tankpilot.fuelrescue.domain.FuelRescueScenarioOverride
import com.tankpilot.fuelrescue.domain.FuelRescueScenarioOverrideProvider
import com.tankpilot.fuelrescue.domain.FuelRescueUseCase
import com.tankpilot.fuelrescue.domain.FuelStation
import com.tankpilot.fuelrescue.domain.FuelStationRepository
import com.tankpilot.location.domain.SelectedSpeed
import com.tankpilot.location.domain.SpeedSource
import com.tankpilot.trip.domain.ActiveTripState
import com.tankpilot.trip.domain.DrivingPattern
import com.tankpilot.trip.domain.DrivingSessionState
import com.tankpilot.trip.domain.Trip
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.vehicle.domain.Vehicle
import com.tankpilot.vehicle.domain.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeVehicleRepository(initial: List<Vehicle>) : VehicleRepository {
    private val flow = MutableStateFlow(initial)
    override fun getVehicles() = flow
    override suspend fun getVehicleById(id: String) = flow.value.find { it.id == id }
    override suspend fun saveVehicle(vehicle: Vehicle) {}
    override suspend fun updateLearnedMpg(vehicleId: String, learnedMpg: Double) {}
    override suspend fun deleteVehicle(id: String) {}
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

private class FakeFuelStationRepository : FuelStationRepository {
    override fun getCachedStations() = flowOf(emptyList<FuelStation>())
    override suspend fun refreshStations(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double,
        fuelType: FuelType,
        forceRefresh: Boolean
    ) = emptyList<FuelStation>()
    override suspend fun clearCache() {}
}

private class NoOpScenarioOverrideProvider : FuelRescueScenarioOverrideProvider {
    override fun overrideOrNull(): FuelRescueScenarioOverride? = null
}

private class FakeVehicleEfficiencyProvider : VehicleEfficiencyProvider {
    override val currentFactoryCityMpg = MutableStateFlow<Double?>(20.0).asStateFlow()
    override val currentFactoryHighwayMpg = MutableStateFlow<Double?>(30.0).asStateFlow()
    override val currentLearnedMpg = MutableStateFlow<Double?>(25.0).asStateFlow()
    override val currentTankCapacityGallons = MutableStateFlow<Double?>(15.0).asStateFlow()
    override val currentReserveFuelGallons = MutableStateFlow<Double?>(2.0).asStateFlow()
    override val currentLowFuelThresholdPercent = MutableStateFlow<Double?>(0.15).asStateFlow()
}

private class NoPreviewProvider : CarFuelPreviewProvider {
    override fun previewSnapshot(): CarFuelSnapshot? = null
}

private class FakeCarLocationSource(private val coordinate: GeoCoordinate?) : CarLocationSource {
    override fun currentLocationOrNull(): GeoCoordinate? = coordinate
}

private fun sampleVehicle() = Vehicle(
    id = "v1", year = 2003, make = "Chevrolet", model = "Impala", trim = null, color = null,
    engine = "3.4L V6", engineDisplacementLiters = 3.4, cylinderCount = 6, tankCapacity = 17.0,
    factoryCityMpg = 20.0, factoryHwyMpg = 29.0, learnedMpg = 24.0,
    preferredFuelType = FuelType.REGULAR, preferredFuelGrade = null, unitSystem = UnitSystem.IMPERIAL,
    reserveFuelGallons = 1.5, lowFuelThresholdPercent = 0.15
)

private fun activeSessionState() = DrivingSessionState(
    selectedSpeed = SelectedSpeed(96.5, SpeedSource.GPS, null, true),
    drivingPattern = DrivingPattern.SUSTAINED_HIGH_SPEED,
    activeTripState = ActiveTripState.ACTIVE,
    tripId = "trip-1",
    distanceMiles = 12.0,
    elapsedTimeSeconds = 600L,
    idleTimeSeconds = 0L,
    averageSpeedMph = 45.0,
    maxSpeedKmh = 100.0,
    activeFuelBurn = 1.5,
    mpgEstimate = MpgEstimate(28.5, MpgEstimateSource.GPS_FACTORY_MODEL, 1_000L, 0.7)
)

/**
 * Verifies buildCarFuelSnapshot() reads the same canonical FuelModelUseCase/
 * DrivingSessionCoordinator state the phone Dashboard reads, and never recalculates
 * fuel/range/MPG/alert math itself.
 */
class CarFuelSnapshotMapperTest {

    private class Harness(vehicle: Vehicle?) {
        val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        val vehicleRepository = FakeVehicleRepository(listOfNotNull(vehicle))
        val fuelStateUseCase = FuelStateUseCase(vehicleRepository, FakeTripRepository(), FakeFillUpRepository(), scope)
        val fuelStationRepository = FakeFuelStationRepository()
        val fuelRescueUseCase = FuelRescueUseCase(fuelStationRepository, fuelStateUseCase, NoOpScenarioOverrideProvider(), scope)

        // Deliberately independent of FuelStateUseCase.estimatedFuelRemaining, so tests can
        // prove the mapper reads FuelModelUseCase's displayed (burn-adjusted) value, not
        // FuelStateUseCase's raw persisted one.
        val persistedFuelRemaining = MutableStateFlow(10.0)
        val activeFuelBurn = MutableStateFlow(0.0)
        val fuelModelUseCase = FuelModelUseCase(
            persistedFuelRemaining = persistedFuelRemaining.asStateFlow(),
            activeFuelBurn = activeFuelBurn.asStateFlow(),
            efficiencyProvider = FakeVehicleEfficiencyProvider(),
            confidencePercent = fuelStateUseCase.confidencePercent,
            alertEngine = AlertEngine(),
            scope = scope
        )

        init {
            // Warm up the WhileSubscribed/Lazily-shared FuelStateUseCase flows so .value
            // reflects the fake repository's data by the time the test reads it — mirrors
            // production, where DashboardViewModel/MainViewModel keep these flows alive.
            scope.launch { fuelStateUseCase.currentVehicle.collect {} }
            scope.launch { fuelStateUseCase.confidencePercent.collect {} }
            scope.launch { fuelStateUseCase.confidence.collect {} }
            scope.launch { fuelStateUseCase.estimatedFuelRemaining.collect {} }
        }
    }

    @Test
    fun mapperUsesFuelModelUseCaseNotFuelStateUseCaseForDisplayedFuel() {
        val h = Harness(sampleVehicle())
        h.persistedFuelRemaining.value = 10.0
        h.activeFuelBurn.value = 2.0

        val snapshot = buildCarFuelSnapshot(
            fuelStateUseCase = h.fuelStateUseCase,
            fuelModelUseCase = h.fuelModelUseCase,
            fuelRescueUseCase = h.fuelRescueUseCase,
            carFuelPreviewProvider = NoPreviewProvider(),
            carLocationSource = FakeCarLocationSource(GeoCoordinate(44.98, -93.26)),
            sessionState = activeSessionState()
        )

        // FuelStateUseCase.estimatedFuelRemaining would report a full tank (17.0, no
        // fillups/trips yet) for this vehicle — proving the snapshot's 8.0 came from
        // FuelModelUseCase (10.0 persisted - 2.0 active burn), not FuelStateUseCase.
        assertEquals(17.0, h.fuelStateUseCase.estimatedFuelRemaining.value.value, 0.001)
        assertEquals(8.0, snapshot.gallonsRemaining!!, 0.001)
    }

    @Test
    fun activeGpsTrackingValuesReachTheSnapshot() {
        val h = Harness(sampleVehicle())

        val snapshot = buildCarFuelSnapshot(
            fuelStateUseCase = h.fuelStateUseCase,
            fuelModelUseCase = h.fuelModelUseCase,
            fuelRescueUseCase = h.fuelRescueUseCase,
            carFuelPreviewProvider = NoPreviewProvider(),
            carLocationSource = FakeCarLocationSource(GeoCoordinate(44.98, -93.26)),
            sessionState = activeSessionState()
        )

        assertTrue(snapshot.isTrackingActive)
        assertEquals("SUSTAINED_HIGH_SPEED", snapshot.drivingPattern)
        assertEquals(28.5, snapshot.mpgValue!!, 0.001)
        assertEquals("GPS_FACTORY_MODEL", snapshot.mpgSource)
    }

    @Test
    fun inactiveTrackingIsReflectedOnTheSnapshot() {
        val h = Harness(sampleVehicle())

        val snapshotNullSession = buildCarFuelSnapshot(
            fuelStateUseCase = h.fuelStateUseCase,
            fuelModelUseCase = h.fuelModelUseCase,
            fuelRescueUseCase = h.fuelRescueUseCase,
            carFuelPreviewProvider = NoPreviewProvider(),
            carLocationSource = FakeCarLocationSource(GeoCoordinate(44.98, -93.26)),
            sessionState = null
        )
        assertFalse(snapshotNullSession.isTrackingActive)

        val idleSession = activeSessionState().copy(activeTripState = ActiveTripState.IDLE)
        val snapshotIdle = buildCarFuelSnapshot(
            fuelStateUseCase = h.fuelStateUseCase,
            fuelModelUseCase = h.fuelModelUseCase,
            fuelRescueUseCase = h.fuelRescueUseCase,
            carFuelPreviewProvider = NoPreviewProvider(),
            carLocationSource = FakeCarLocationSource(GeoCoordinate(44.98, -93.26)),
            sessionState = idleSession
        )
        assertFalse(snapshotIdle.isTrackingActive)
    }

    @Test
    fun canonicalAlertTextReachesTheSnapshotUnchanged() {
        val h = Harness(sampleVehicle())
        h.persistedFuelRemaining.value = 0.5
        h.activeFuelBurn.value = 0.0

        val snapshot = buildCarFuelSnapshot(
            fuelStateUseCase = h.fuelStateUseCase,
            fuelModelUseCase = h.fuelModelUseCase,
            fuelRescueUseCase = h.fuelRescueUseCase,
            carFuelPreviewProvider = NoPreviewProvider(),
            carLocationSource = FakeCarLocationSource(GeoCoordinate(44.98, -93.26)),
            sessionState = activeSessionState()
        )

        assertEquals(h.fuelModelUseCase.warningText.value, snapshot.alertsText)
    }

    @Test
    fun locationUnavailableIsReflectedOnTheSnapshot() {
        val h = Harness(sampleVehicle())

        val snapshot = buildCarFuelSnapshot(
            fuelStateUseCase = h.fuelStateUseCase,
            fuelModelUseCase = h.fuelModelUseCase,
            fuelRescueUseCase = h.fuelRescueUseCase,
            carFuelPreviewProvider = NoPreviewProvider(),
            carLocationSource = FakeCarLocationSource(null),
            sessionState = activeSessionState()
        )

        assertTrue(snapshot.isLocationUnavailable)
    }

    @Test
    fun noVehicleConfiguredReturnsUnavailableSnapshot() {
        val h = Harness(vehicle = null)

        val snapshot = buildCarFuelSnapshot(
            fuelStateUseCase = h.fuelStateUseCase,
            fuelModelUseCase = h.fuelModelUseCase,
            fuelRescueUseCase = h.fuelRescueUseCase,
            carFuelPreviewProvider = NoPreviewProvider(),
            carLocationSource = FakeCarLocationSource(null),
            sessionState = null
        )

        assertNull(snapshot.vehicleLabel)
        assertNull(snapshot.gallonsRemaining)
        assertFalse(snapshot.isTrackingActive)
    }
}
