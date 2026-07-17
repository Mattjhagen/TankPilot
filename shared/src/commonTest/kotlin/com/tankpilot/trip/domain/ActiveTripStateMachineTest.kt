package com.tankpilot.trip.domain

import kotlin.test.*

class ActiveTripStateMachineTest {

    private lateinit var stateMachine: ActiveTripStateMachine

    @BeforeTest
    fun setUp() {
        stateMachine = ActiveTripStateMachine(
            startSpeedKmh = 12.87,
            startDurationMs = 5000L,
            startGraceMs = 3000L,
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
    fun testBriefDipBelowStartThresholdDoesNotCancelCandidate() {
        stateMachine.onSpeedUpdate(13.0, 1000L)
        assertEquals(ActiveTripState.START_CANDIDATE, stateMachine.state.value)

        // One brief low reading, 1 second later — well within the 3s grace window.
        stateMachine.onSpeedUpdate(2.0, 2000L)
        assertEquals(ActiveTripState.START_CANDIDATE, stateMachine.state.value)

        // Motion resumes and the original 5-second window (from t=1000) still completes.
        stateMachine.onSpeedUpdate(13.0, 6000L)
        assertEquals(ActiveTripState.ACTIVE, stateMachine.state.value)
    }

    @Test
    fun testSustainedLowSpeedDoesCancelCandidate() {
        stateMachine.onSpeedUpdate(13.0, 1000L)
        assertEquals(ActiveTripState.START_CANDIDATE, stateMachine.state.value)

        // Low speed persists past the 3s grace window without ever recovering.
        stateMachine.onSpeedUpdate(2.0, 2000L)
        assertEquals(ActiveTripState.START_CANDIDATE, stateMachine.state.value)
        stateMachine.onSpeedUpdate(2.0, 5100L) // 3.1s after the dip began at 2000L
        assertEquals(ActiveTripState.IDLE, stateMachine.state.value)
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
