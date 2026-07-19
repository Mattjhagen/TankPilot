package com.tankpilot.trip.domain

import com.tankpilot.location.domain.LocationAuthorizationStatus
import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.LocationSampleSource
import com.tankpilot.location.domain.RoadContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestClock(var currentTimeMs: Long) : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(currentTimeMs)
}

@OptIn(ExperimentalCoroutinesApi::class)
class DriveAutoStartStateMachineTest {
    private val clock = TestClock(1000L)
    private var isSessionActiveFlag = false
    private var startSessionSuccess = true
    private var startSessionCallCount = 0

    private val isSessionActive: () -> Boolean = { isSessionActiveFlag }
    private val startSession: () -> Boolean = {
        startSessionCallCount++
        startSessionSuccess
    }

    private val machine = DriveAutoStartStateMachine(
        config = DriveAutoStartConfig(),
        clock = clock,
        isSessionActive = isSessionActive,
        startSession = startSession
    )

    private fun sample(speedMph: Double?, hAcc: Double? = 10.0, sAcc: Double? = 1.0, ageMs: Long = 0L): LocationSample {
        val speedKmh = speedMph?.let { it * 1.609344 }
        return LocationSample(
            timestamp = Instant.fromEpochMilliseconds(clock.currentTimeMs - ageMs),
            latitude = 0.0,
            longitude = 0.0,
            speedKmh = speedKmh,
            speedAccuracyMps = sAcc,
            horizontalAccuracyMeters = hAcc,
            bearingDegrees = null,
            roadContext = RoadContext.UNKNOWN,
            source = LocationSampleSource.GPS
        )
    }

    @Test
    fun `connection enters ARMED`() = runBlocking {
        assertEquals(AutoStartState.DISCONNECTED, machine.state.value)
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ARMED, machine.state.value)
    }

    @Test
    fun `exactly 5 mph does not begin confirmation`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(5.0))
        assertEquals(AutoStartState.ARMED, machine.state.value)
    }

    @Test
    fun `greater than 5 mph begins confirmation`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(5.1))
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
    }

    @Test
    fun `5 continuous seconds above threshold starts once`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
        
        clock.currentTimeMs += 5000L
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
        assertEquals(1, startSessionCallCount)
    }

    @Test
    fun `sample between 4 and 5 mph invalidates continuous confirmation window but does not disarm`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
        
        clock.currentTimeMs += 2000L
        machine.onLocationSample(sample(4.5))
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
        
        clock.currentTimeMs += 3000L
        machine.onLocationSample(sample(6.0))
        // Window was invalidated, so this 6.0 restarts it. Should not be active.
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
        
        clock.currentTimeMs += 5000L
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
        assertEquals(1, startSessionCallCount)
    }

    @Test
    fun `below 4 mph resets and returns to ARMED`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
        
        clock.currentTimeMs += 1000L
        machine.onLocationSample(sample(3.9))
        assertEquals(AutoStartState.ARMED, machine.state.value)
    }

    @Test
    fun `stale sample ignored`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0, ageMs = 15000L))
        assertEquals(AutoStartState.ARMED, machine.state.value)
    }

    @Test
    fun `out-of-order sample ignored`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
        
        clock.currentTimeMs += 1000L
        // Send a sample that is older than the last one
        machine.onLocationSample(sample(6.0, ageMs = 2000L))
        
        clock.currentTimeMs += 4000L
        machine.onLocationSample(sample(6.0))
        // The out of order sample was ignored, so the continuous window is broken? 
        // No, the out-of-order sample is entirely ignored, so the window from the first sample continues.
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
    }

    @Test
    fun `negative speed ignored`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        
        clock.currentTimeMs += 1000L
        machine.onLocationSample(sample(-1.0)) // Ignored
        
        clock.currentTimeMs += 4000L
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
    }

    @Test
    fun `poor horizontal accuracy ignored`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0, hAcc = 100.0))
        assertEquals(AutoStartState.ARMED, machine.state.value)
    }

    @Test
    fun `poor speed accuracy ignored`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0, sAcc = 10.0)) // config max is 3.0
        assertEquals(AutoStartState.ARMED, machine.state.value)
    }

    @Test
    fun `unavailable speed accuracy follows documented fallback`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0, sAcc = null))
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
    }

    @Test
    fun `insufficient authorization prevents starting`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.DENIED)
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.ARMED, machine.state.value) // does not transition to WAITING
    }

    @Test
    fun `active session prevents duplicate start`() = runBlocking {
        isSessionActiveFlag = true
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
    }

    @Test
    fun `concurrent qualifying callbacks start exactly once`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        
        clock.currentTimeMs += 5000L
        machine.onLocationSample(sample(6.0)) // Triggers start
        machine.onLocationSample(sample(6.0)) // Should not trigger again because state is ACTIVE
        assertEquals(1, startSessionCallCount)
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
    }

    @Test
    fun `failed start returns to ARMED`() = runBlocking {
        startSessionSuccess = false
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        
        clock.currentTimeMs += 5000L
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.ARMED, machine.state.value)
        assertEquals(1, startSessionCallCount)
    }

    @Test
    fun `failed start requires a fresh confirmation period`() = runBlocking {
        startSessionSuccess = false
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        
        clock.currentTimeMs += 5000L
        machine.onLocationSample(sample(6.0)) // Fails
        assertEquals(AutoStartState.ARMED, machine.state.value)
        
        startSessionSuccess = true
        machine.onLocationSample(sample(6.0)) // Retries
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
        
        clock.currentTimeMs += 5000L
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
        assertEquals(2, startSessionCallCount)
    }

    @Test
    fun `disconnect before start returns to DISCONNECTED`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.WAITING_FOR_RELIABLE_SPEED, machine.state.value)
        
        machine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.DISCONNECTED, machine.state.value)
    }

    @Test
    fun `disconnect during active session leaves trip active`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        
        clock.currentTimeMs += 5000L
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
        
        machine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
    }

    @Test
    fun `reconnect during ACTIVE does not start another trip`() = runBlocking {
        isSessionActiveFlag = true
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
        
        machine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
        assertEquals(0, startSessionCallCount)
    }

    @Test
    fun `overlapping VehicleContexts`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.Bluetooth)
        assertEquals(AutoStartState.ARMED, machine.state.value)
        
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ARMED, machine.state.value)
        
        machine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.Bluetooth)
        assertEquals(AutoStartState.ARMED, machine.state.value)
        
        machine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.DISCONNECTED, machine.state.value)
    }

    @Test
    fun `duplicate context insertion is idempotent`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ARMED, machine.state.value)
        
        machine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.DISCONNECTED, machine.state.value)
    }

    @Test
    fun `removing inactive context does nothing`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ARMED, machine.state.value)
        
        machine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.Bluetooth)
        assertEquals(AutoStartState.ARMED, machine.state.value)
    }

    @Test
    fun `final context removed while trip active leaves trip active`() = runBlocking {
        machine.enterDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        machine.onLocationAuthorizationChanged(LocationAuthorizationStatus.AUTHORIZED)
        machine.onLocationSample(sample(6.0))
        clock.currentTimeMs += 5000L
        machine.onLocationSample(sample(6.0))
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
        
        machine.exitDriveDetectionMode(com.tankpilot.trip.domain.VehicleContext.CarPlay)
        assertEquals(AutoStartState.ACTIVE, machine.state.value)
    }
}
