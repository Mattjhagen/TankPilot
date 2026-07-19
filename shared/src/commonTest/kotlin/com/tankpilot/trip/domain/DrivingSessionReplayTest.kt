package com.tankpilot.trip.domain

import com.tankpilot.core.FakeAppClock
import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.RoadContext
import com.tankpilot.telemetry.domain.TelemetryData
import com.tankpilot.fuel.domain.MpgEstimator
import com.tankpilot.fuel.domain.VehicleEfficiencyProvider
import com.tankpilot.testsupport.location.LocationReplayHarness
import com.tankpilot.testsupport.location.ReplayCoordinate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.datetime.Instant
import kotlin.test.*

class DrivingSessionReplayTest {

    private class MockVehicleEfficiencyProvider : VehicleEfficiencyProvider {
        override val currentFactoryCityMpg = MutableStateFlow<Double?>(20.0).asStateFlow()
        override val currentFactoryHighwayMpg = MutableStateFlow<Double?>(30.0).asStateFlow()
        override val currentLearnedMpg = MutableStateFlow<Double?>(25.0).asStateFlow()
        override val currentTankCapacityGallons = MutableStateFlow<Double?>(15.0).asStateFlow()
        override val currentReserveFuelGallons = MutableStateFlow<Double?>(2.0).asStateFlow()
        override val currentLowFuelThresholdPercent = MutableStateFlow<Double?>(0.15).asStateFlow()
    }

    private    class MockTripRepository : com.tankpilot.trip.domain.TripRepository {
        var tripSaved = false
        var savedTrips = mutableListOf<Trip>()

        override fun getTrips(vehicleId: String): Flow<List<Trip>> = emptyFlow()
        override fun getRecentTrips(vehicleId: String, limit: Long): Flow<List<Trip>> = emptyFlow()
        
        override suspend fun saveTrip(trip: Trip) {
            tripSaved = true
            savedTrips.add(trip)
        }
        
        override suspend fun deleteTrip(id: String) {}
        override suspend fun saveTripRoutePoints(tripId: String, points: List<com.tankpilot.location.domain.LocationSample>, startIndex: Int) {}
        override suspend fun saveTripAndFinalRoute(trip: Trip, points: List<com.tankpilot.location.domain.LocationSample>, startIndex: Int) { saveTrip(trip); saveTripRoutePoints(trip.id, points, startIndex) }
        override fun getTripRoute(tripId: String): Flow<List<com.tankpilot.location.domain.LocationSample>> = kotlinx.coroutines.flow.emptyFlow()
    }

    private class MockActiveSessionRepository : ActiveSessionRepository {
        override suspend fun saveSession(session: ActiveSession) {}
        override suspend fun getSession(vehicleId: String): ActiveSession? = null
        override suspend fun deleteSession(vehicleId: String) {}
    }

    private lateinit var scope: CoroutineScope
    private lateinit var coordinator: DrivingSessionCoordinator
    private lateinit var harness: LocationReplayHarness

    @BeforeTest
    fun setUp() {
        scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        
        val clock = FakeAppClock(Instant.fromEpochSeconds(1000L))
        val pipeline = LocationPipeline(scope = scope)
        val stateMachine = ActiveTripStateMachine()
        val metrics = ActiveTripMetricsUseCase()
        val effProvider = MockVehicleEfficiencyProvider()
        val mpgEstimator = MpgEstimator(effProvider)
        
        val telemetryFlow = MutableStateFlow(TelemetryData())
        val gpsSpeedFlow = pipeline.validatedLocation.map { it?.speedKmh }.stateIn(scope, SharingStarted.Eagerly, null)
        
        val speedSelection = SpeedSelectionUseCase(
            telemetryFlow = telemetryFlow,
            gpsSpeedFlow = gpsSpeedFlow,
            clock = clock,
            scope = scope
        )

        val tripRepo = MockTripRepository()
        val sessionRepo = MockActiveSessionRepository()
        val activeVehicleFlow = MutableStateFlow<String?>("v1").asStateFlow()

        val routeRecorder = TripRouteRecorder(tripRepo, scope)
        coordinator = DrivingSessionCoordinator(
            locationPipeline = pipeline,
            stateMachine = stateMachine,
            metricsUseCase = metrics,
            mpgEstimator = mpgEstimator,
            speedSelectionUseCase = speedSelection,
            tripRepository = tripRepo,
            activeSessionRepository = sessionRepo,
            activeVehicleId = activeVehicleFlow,
            routeRecorder = routeRecorder,
            scope = scope
        )

        harness = LocationReplayHarness(coordinator)
    }

    @Test
    fun testHighwayReplayClassification() {
        val samples = (1..15).map { i ->
            ReplayCoordinate(
                timestampMs = 1000L + i * 1000L,
                latitude = 41.25 + (i * 0.0002),
                longitude = -95.93,
                speedKmh = 90.0,
                roadContext = RoadContext.HIGHWAY_LIKELY
            )
        }

        coordinator.startTripManually()
        harness.replay(samples)

        assertEquals(ActiveTripState.ACTIVE, coordinator.sessionState.value.activeTripState)
        assertTrue(coordinator.sessionState.value.distanceMeters > 0.0)
    }
}
