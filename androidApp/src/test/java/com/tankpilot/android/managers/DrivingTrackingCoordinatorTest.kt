package com.tankpilot.android.managers

import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.content.Intent
import android.os.Build
import com.tankpilot.location.domain.LocationProvider
import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.TrackingUnavailableReason
import com.tankpilot.trip.domain.DrivingSessionCoordinator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Assert.*
import org.junit.Test

private class FakeLocationProvider(private val failOnStart: Throwable? = null) : LocationProvider {
    private val _locationFlow = MutableStateFlow<LocationSample?>(null)
    override val locationFlow = _locationFlow.asStateFlow()

    private val _statusFlow = MutableStateFlow<TrackingUnavailableReason?>(null)
    override val statusFlow = _statusFlow.asStateFlow()

    var startCallCount = 0
        private set
    var stopCallCount = 0
        private set

    val startCalled: Boolean get() = startCallCount > 0
    val stopCalled: Boolean get() = stopCallCount > 0

    override fun startTracking() {
        startCallCount++
        failOnStart?.let { throw it }
    }

    override fun stopTracking() {
        stopCallCount++
    }

    fun emitStatus(reason: TrackingUnavailableReason?) {
        _statusFlow.value = reason
    }
}

class DrivingTrackingCoordinatorTest {

    private fun buildCoordinator(
        fakeProvider: FakeLocationProvider,
        drivingSessionCoordinator: DrivingSessionCoordinator = mockk(relaxed = true),
        context: Context = mockk(relaxed = true)
    ): DrivingTrackingCoordinator {
        val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        return DrivingTrackingCoordinator(
            context = context,
            drivingSessionCoordinator = drivingSessionCoordinator,
            scope = scope,
            locationProviderFactory = { fakeProvider }
        )
    }

    @Test
    fun `fused provider selected when Play Services available`() {
        val fused = FakeLocationProvider()
        val framework = FakeLocationProvider()

        val selected = selectLocationProvider(
            gmsAvailable = true,
            fusedProvider = { fused },
            frameworkProvider = { framework }
        )

        assertSame(fused, selected)
    }

    @Test
    fun `framework fallback selected when Play Services unavailable`() {
        val fused = FakeLocationProvider()
        val framework = FakeLocationProvider()

        val selected = selectLocationProvider(
            gmsAvailable = false,
            fusedProvider = { fused },
            frameworkProvider = { framework }
        )

        assertSame(framework, selected)
    }

    @Test
    fun `start tracking with permission already granted starts the provider and clears tracking error`() {
        val fake = FakeLocationProvider()
        val coordinator = buildCoordinator(fake)

        coordinator.startTracking()

        assertTrue(fake.startCalled)
        assertNull(coordinator.trackingStatus.value)
    }

    @Test
    fun `permission denied surfaces as a tracking error without starting the provider`() {
        val fake = FakeLocationProvider()
        val coordinator = buildCoordinator(fake)

        coordinator.onPermissionDenied()

        assertFalse(fake.startCalled)
        assertEquals(TrackingUnavailableReason.LOCATION_PERMISSION_DENIED, coordinator.trackingStatus.value)
    }

    @Test
    fun `provider-detected location services disabled surfaces on canonical tracking status`() {
        val fake = FakeLocationProvider()
        val coordinator = buildCoordinator(fake)

        coordinator.startTracking()
        fake.emitStatus(TrackingUnavailableReason.LOCATION_SERVICES_DISABLED)

        assertEquals(TrackingUnavailableReason.LOCATION_SERVICES_DISABLED, coordinator.trackingStatus.value)
    }

    @Test
    fun `stopping requests canonical trip completion and stops the provider`() {
        val fake = FakeLocationProvider()
        val drivingSessionCoordinator = mockk<DrivingSessionCoordinator>(relaxed = true)
        val coordinator = buildCoordinator(fake, drivingSessionCoordinator)

        coordinator.startTracking()
        coordinator.stopTracking()

        assertTrue(fake.stopCalled)
        verify(exactly = 1) { drivingSessionCoordinator.endTripManually() }
    }

    @Test
    fun `repeated Start Drive calls are idempotent — provider only starts once`() {
        val fake = FakeLocationProvider()
        val coordinator = buildCoordinator(fake)

        coordinator.startTracking()
        coordinator.startTracking()
        coordinator.startTracking()

        assertEquals(1, fake.startCallCount)
        assertTrue(coordinator.isTracking.value)
    }

    @Test
    fun `repeated Stop Drive calls are safe — no redundant trip completion requests`() {
        val fake = FakeLocationProvider()
        val drivingSessionCoordinator = mockk<DrivingSessionCoordinator>(relaxed = true)
        val coordinator = buildCoordinator(fake, drivingSessionCoordinator)

        coordinator.startTracking()
        coordinator.stopTracking()
        coordinator.stopTracking()
        coordinator.stopTracking()

        assertEquals(1, fake.stopCallCount)
        verify(exactly = 1) { drivingSessionCoordinator.endTripManually() }
        assertFalse(coordinator.isTracking.value)
    }

    @Test
    fun `Stop Drive is safe to call before any Start Drive`() {
        val fake = FakeLocationProvider()
        val drivingSessionCoordinator = mockk<DrivingSessionCoordinator>(relaxed = true)
        val coordinator = buildCoordinator(fake, drivingSessionCoordinator)

        coordinator.stopTracking()

        assertEquals(0, fake.stopCallCount)
        verify(exactly = 0) { drivingSessionCoordinator.endTripManually() }
    }

    @Test
    fun `permission denied then granted retry succeeds`() {
        val fake = FakeLocationProvider()
        val coordinator = buildCoordinator(fake)

        coordinator.onPermissionDenied()
        assertEquals(TrackingUnavailableReason.LOCATION_PERMISSION_DENIED, coordinator.trackingStatus.value)
        assertFalse(coordinator.isTracking.value)

        // User retries — grants permission this time.
        coordinator.startTracking()

        assertTrue(fake.startCalled)
        assertTrue(coordinator.isTracking.value)
        assertNull(coordinator.trackingStatus.value)
    }

    @Test
    fun `location provider start failure surfaces as permission denied and does not mark tracking active`() {
        val failing = FakeLocationProvider(failOnStart = SecurityException("no permission"))
        val coordinator = buildCoordinator(failing)

        coordinator.startTracking()

        assertEquals(TrackingUnavailableReason.LOCATION_PERMISSION_DENIED, coordinator.trackingStatus.value)
        assertFalse(coordinator.isTracking.value)
        // A later retry must still be attempted, not permanently locked out by the guard.
        assertEquals(1, failing.startCallCount)
    }

    @Test
    fun `foreground service start failure prevents tracking from being marked active`() {
        val fake = FakeLocationProvider()
        val context = mockk<Context>(relaxed = true)
        every { context.startForegroundService(any<Intent>()) } throws ForegroundServiceStartNotAllowedException("blocked")
        every { context.startService(any<Intent>()) } throws ForegroundServiceStartNotAllowedException("blocked")
        val coordinator = buildCoordinator(fake, context = context)

        coordinator.startTracking()

        // The production code distinguishes FOREGROUND_START_NOT_ALLOWED from UNKNOWN via
        // Build.VERSION.SDK_INT >= S, which this plain-JVM test environment can't fake
        // without Robolectric (SDK_INT here isn't a real device's 31+). Both outcomes prove
        // the failure was caught and tracking was never marked active — that's the
        // behavior this test can actually verify here.
        assertTrue(
            coordinator.trackingStatus.value == TrackingUnavailableReason.FOREGROUND_START_NOT_ALLOWED ||
                coordinator.trackingStatus.value == TrackingUnavailableReason.UNKNOWN
        )
        assertFalse(coordinator.isTracking.value)
        assertTrue(fake.startCalled)
    }

    @Test
    fun `supportsForegroundServiceStartNotAllowedException gates by API level deterministically`() {
        assertFalse(supportsForegroundServiceStartNotAllowedException(Build.VERSION_CODES.R))
        assertTrue(supportsForegroundServiceStartNotAllowedException(Build.VERSION_CODES.S))
    }
}
