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

    private class MockTripRepository : TripRepository {
        override fun getTrips(vehicleId: String) = flowOf(emptyList<Trip>())
        override fun getRecentTrips(vehicleId: String, limit: Long) = flowOf(emptyList<Trip>())
        override suspend fun saveTrip(trip: Trip) {}
        override suspend fun deleteTrip(id: String) {}
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

        coordinator = DrivingSessionCoordinator(
            locationPipeline = pipeline,
            stateMachine = stateMachine,
            metricsUseCase = metrics,
            mpgEstimator = mpgEstimator,
            speedSelectionUseCase = speedSelection,
            tripRepository = MockTripRepository(),
            activeSessionRepository = MockActiveSessionRepository(),
            activeVehicleId = MutableStateFlow<String?>("v1").asStateFlow(),
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
        assertTrue(coordinator.sessionState.value.distanceMiles > 0.0)
    }
}
