package com.tankpilot.dashboard.domain

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for the one-shot critical-fuel detection gate.
 *
 * DashboardViewModel uses a `wasCritical` boolean to emit `DashboardEffect.CriticalFuelEntered`
 * only on a NORMAL → CRITICAL transition, not on continued-critical or restored-critical state.
 *
 * Because ViewModel is Android-only, we extract the gate logic here as a pure function
 * and test it independently.
 *
 * Architectural guarantees proven by these tests:
 *   1. NORMAL → CRITICAL emits once
 *   2. CRITICAL → CRITICAL does not re-emit (continued state)
 *   3. CRITICAL → NORMAL → CRITICAL emits again (a real second transition)
 *   4. State restored as CRITICAL after process death WILL emit once on first evaluation
 *      (wasCritical resets to false on ViewModel creation — this is intentional and documented)
 */
class DashboardCriticalFuelGateTest {

    /**
     * Simulates the gate: returns true when a CriticalFuelEntered effect should be emitted.
     * Mirrors the logic in DashboardViewModel.uiState combine block.
     */
    private fun shouldEmitCritical(isCritical: Boolean, wasCritical: Boolean): Boolean =
        isCritical && !wasCritical

    @Test
    fun `NORMAL to CRITICAL emits effect`() {
        val wasCritical = false
        val isCritical = true
        assertEquals(true, shouldEmitCritical(isCritical, wasCritical))
    }

    @Test
    fun `CRITICAL remains CRITICAL does not re-emit`() {
        val wasCritical = true
        val isCritical = true
        assertEquals(false, shouldEmitCritical(isCritical, wasCritical))
    }

    @Test
    fun `NORMAL remains NORMAL does not emit`() {
        val wasCritical = false
        val isCritical = false
        assertEquals(false, shouldEmitCritical(isCritical, wasCritical))
    }

    @Test
    fun `CRITICAL to NORMAL to CRITICAL emits second time`() {
        // Sequence: false->true (emit), true->true (no emit), true->false (no emit), false->true (emit again)
        var wasCritical = false

        // Step 1: NORMAL → CRITICAL
        var isCritical = true
        assertEquals(true, shouldEmitCritical(isCritical, wasCritical))
        wasCritical = isCritical

        // Step 2: CRITICAL → CRITICAL (sustained)
        isCritical = true
        assertEquals(false, shouldEmitCritical(isCritical, wasCritical))
        wasCritical = isCritical

        // Step 3: CRITICAL → NORMAL (resolved)
        isCritical = false
        assertEquals(false, shouldEmitCritical(isCritical, wasCritical))
        wasCritical = isCritical

        // Step 4: NORMAL → CRITICAL again (real new transition — must emit)
        isCritical = true
        assertEquals(true, shouldEmitCritical(isCritical, wasCritical))
    }

    @Test
    fun `process death restores critical state emits once on first evaluation`() {
        // After process death, ViewModel is recreated with wasCritical = false.
        // If persistent state says isCritical = true, the first evaluation will emit.
        // This is intentional: the user should be re-alerted after returning to the app.
        val wasCriticalAfterRecreation = false // reset by ViewModel init
        val isCriticalFromRestoredState = true
        assertEquals(true, shouldEmitCritical(isCriticalFromRestoredState, wasCriticalAfterRecreation))
    }
}
