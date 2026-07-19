package com.tankpilot.trip.domain

import com.tankpilot.core.FakeAppClock
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

/**
 * Covers the Start/Stop Drive manual-stop path: requesting canonical trip completion
 * must persist the completed trip exactly once and clear the active session, matching
 * DrivingTrackingCoordinator.stopTracking()'s call to endTripManually().
 */
class DrivingSessionCoordinatorManualStopTest {

    private class MockVehicleEfficiencyProvider : VehicleEfficiencyProvider {
        override val currentFactoryCityMpg = MutableStateFlow<Double?>(20.0).asStateFlow()
        override val currentFactoryHighwayMpg = MutableStateFlow<Double?>(30.0).asStateFlow()
        override val currentLearnedMpg = MutableStateFlow<Double?>(25.0).asStateFlow()
        override val currentTankCapacityGallons = MutableStateFlow<Double?>(15.0).asStateFlow()
        override val currentReserveFuelGallons = MutableStateFlow<Double?>(2.0).asStateFlow()
        override val currentLowFuelThresholdPercent = MutableStateFlow<Double?>(0.15).asStateFlow()
    }

    private class CountingTripRepository : com.tankpilot.trip.domain.TripRepository {
        var saveCount = 0
        var savedTrips = mutableListOf<Trip>()

        override fun getTrips(vehicleId: String): Flow<List<Trip>> = emptyFlow()
        override fun getRecentTrips(vehicleId: String, limit: Long): Flow<List<Trip>> = emptyFlow()
        
        override suspend fun saveTrip(trip: Trip) {
            saveCount++
            savedTrips.add(trip)
        }
        
        override suspend fun deleteTrip(id: String) {}
        override suspend fun saveTripRoutePoints(tripId: String, points: List<com.tankpilot.location.domain.LocationSample>, startIndex: Int) {}
        override suspend fun saveTripAndFinalRoute(trip: Trip, points: List<com.tankpilot.location.domain.LocationSample>, startIndex: Int) { saveTrip(trip); saveTripRoutePoints(trip.id, points, startIndex) }
        override fun getTripRoute(tripId: String): Flow<List<com.tankpilot.location.domain.LocationSample>> = kotlinx.coroutines.flow.emptyFlow()
    }

    private class CountingActiveSessionRepository : ActiveSessionRepository {
        var deleteCount = 0
            private set
        override suspend fun saveSession(session: ActiveSession) {}
        override suspend fun getSession(vehicleId: String): ActiveSession? = null
        override suspend fun deleteSession(vehicleId: String) { deleteCount++ }
    }

    private lateinit var scope: CoroutineScope
    private lateinit var coordinator: DrivingSessionCoordinator
    private lateinit var harness: LocationReplayHarness
    private lateinit var tripRepository: CountingTripRepository
    private lateinit var activeSessionRepository: CountingActiveSessionRepository

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

        tripRepository = CountingTripRepository()
        activeSessionRepository = CountingActiveSessionRepository()

        val routeRecorder = TripRouteRecorder(tripRepository, scope)
        coordinator = DrivingSessionCoordinator(
            locationPipeline = pipeline,
            stateMachine = stateMachine,
            metricsUseCase = metrics,
            mpgEstimator = mpgEstimator,
            speedSelectionUseCase = speedSelection,
            tripRepository = tripRepository,
            activeSessionRepository = activeSessionRepository,
            activeVehicleId = MutableStateFlow<String?>("v1").asStateFlow(),
            routeRecorder = routeRecorder,
            scope = scope
        )

        harness = LocationReplayHarness(coordinator)
    }

    private fun driveSamples() = (1..15).map { i ->
        ReplayCoordinate(
            timestampMs = 1000L + i * 1000L,
            latitude = 41.25 + (i * 0.0002),
            longitude = -95.93,
            speedKmh = 90.0,
            roadContext = RoadContext.HIGHWAY_LIKELY
        )
    }

    @Test
    fun testGpsSampleUpdatesCanonicalDashboardSpeed() {
        coordinator.startTripManually()
        harness.replay(driveSamples())

        assertEquals(90.0, coordinator.sessionState.value.selectedSpeed.valueKmh)
    }

    @Test
    fun testStoppingSavesExactlyOneTripAndClearsActiveSession() {
        coordinator.startTripManually()
        harness.replay(driveSamples())
        assertEquals(ActiveTripState.ACTIVE, coordinator.sessionState.value.activeTripState)

        // Requests canonical trip completion, as DrivingTrackingCoordinator.stopTracking() does.
        coordinator.endTripManually()

        assertEquals(1, tripRepository.saveCount)
        assertEquals(1, activeSessionRepository.deleteCount)
        assertEquals(ActiveTripState.IDLE, coordinator.sessionState.value.activeTripState)
    }

    @Test
    fun testStoppingWithNoActiveTripSavesNothing() {
        // No trip was ever started — Stop Drive with nothing in progress must not fabricate a trip.
        coordinator.endTripManually()

        assertEquals(0, tripRepository.saveCount)
        assertEquals(0, activeSessionRepository.deleteCount)
    }

    @Test
    fun testBriefUnavailableSpeedSampleDuringCandidateDoesNotCancelIt() {
        // A GPS fix with no speed component (Location.hasSpeed() == false) must be treated
        // as "unknown", not "confirmed stationary" — the candidate's original qualifying
        // window (started at t=2000) must still complete at t=7000 (5000ms later).
        harness.replay(
            listOf(
                ReplayCoordinate(timestampMs = 2000L, latitude = 41.2502, longitude = -95.93, speedKmh = 90.0),
                ReplayCoordinate(timestampMs = 3000L, latitude = 41.2504, longitude = -95.93, speedKmh = null),
                ReplayCoordinate(timestampMs = 7000L, latitude = 41.2506, longitude = -95.93, speedKmh = 90.0)
            )
        )

        assertEquals(ActiveTripState.ACTIVE, coordinator.sessionState.value.activeTripState)
    }

    @Test
    fun testRepeatedUnavailableSpeedSamplesDoNotAccidentallyStartATrip() {
        harness.replay(
            listOf(
                ReplayCoordinate(timestampMs = 2000L, latitude = 41.2502, longitude = -95.93, speedKmh = null),
                ReplayCoordinate(timestampMs = 3000L, latitude = 41.2504, longitude = -95.93, speedKmh = null),
                ReplayCoordinate(timestampMs = 4000L, latitude = 41.2506, longitude = -95.93, speedKmh = null)
            )
        )

        assertEquals(ActiveTripState.IDLE, coordinator.sessionState.value.activeTripState)
        assertNull(coordinator.sessionState.value.tripId)
    }
}
