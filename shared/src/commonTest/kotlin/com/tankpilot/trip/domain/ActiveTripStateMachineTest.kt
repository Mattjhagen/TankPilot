package com.tankpilot.trip.domain

import kotlin.test.*

class ActiveTripStateMachineTest {

    private lateinit var stateMachine: ActiveTripStateMachine

    @BeforeTest
    fun setUp() {
        stateMachine = ActiveTripStateMachine(
            startSpeedKmh = 12.87,
            startDurationMs = 5000L,
            stopSpeedKmh = 3.21,
            stopDurationMs = 90000L
        )
    }

    @Test
    fun testDefaultStateIsIdle() {
        assertEquals(ActiveTripState.IDLE, stateMachine.state.value)
        assertNull(stateMachine.tripId.value)
    }

    @Test
    fun testStartCandidateOnSpeedThreshold() {
        stateMachine.onSpeedUpdate(13.0, 1000L)
        assertEquals(ActiveTripState.START_CANDIDATE, stateMachine.state.value)
    }

    @Test
    fun testSustainedSpeedStartsActiveTrip() {
        stateMachine.onSpeedUpdate(13.0, 1000L)
        // 5 seconds later
        stateMachine.onSpeedUpdate(14.0, 6000L)
        assertEquals(ActiveTripState.ACTIVE, stateMachine.state.value)
        assertNotNull(stateMachine.tripId.value)
    }

    @Test
    fun testTrafficLightStopDoesNotEndTripImmediately() {
        // Start trip
        stateMachine.onSpeedUpdate(13.0, 1000L)
        stateMachine.onSpeedUpdate(14.0, 6000L)
        assertEquals(ActiveTripState.ACTIVE, stateMachine.state.value)

        // Stationary
        stateMachine.onSpeedUpdate(0.0, 7000L)
        assertEquals(ActiveTripState.STOP_CANDIDATE, stateMachine.state.value)

        // Still stop candidate after 30 seconds
        stateMachine.onSpeedUpdate(0.0, 37000L)
        assertEquals(ActiveTripState.STOP_CANDIDATE, stateMachine.state.value)

        // Resumes motion
        stateMachine.onSpeedUpdate(15.0, 38000L)
        assertEquals(ActiveTripState.ACTIVE, stateMachine.state.value)
    }

    @Test
    fun testExtendedStopTransitionToCompleting() {
        // Start trip
        stateMachine.onSpeedUpdate(13.0, 1000L)
        stateMachine.onSpeedUpdate(14.0, 6000L)

        // Stationary
        stateMachine.onSpeedUpdate(0.0, 7000L)
        
        // After 90 seconds (91 seconds later)
        stateMachine.onSpeedUpdate(0.0, 98000L)
        assertEquals(ActiveTripState.COMPLETING, stateMachine.state.value)
    }
}
