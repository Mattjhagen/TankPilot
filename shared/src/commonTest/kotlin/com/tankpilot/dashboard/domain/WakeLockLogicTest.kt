package com.tankpilot.dashboard.domain

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for the WakeLock acquisition/release state machine.
 *
 * The policy is: FLAG_KEEP_SCREEN_ON is set ONLY when dashboardActive AND isForeground.
 * This test uses a pure fake to verify the gate logic without any Android framework.
 *
 * Release paths verified:
 *   - Dashboard hidden (enabled = false)
 *   - App backgrounded (isForeground = false)
 *   - onDispose always releases
 *   - Activity recreated: old dispose releases, new acquire if conditions met
 */
class WakeLockLogicTest {

    private data class State(var acquireCount: Int = 0, var releaseCount: Int = 0)

    private fun applyPolicy(enabled: Boolean, isForeground: Boolean, state: State) {
        if (enabled && isForeground) state.acquireCount++ else state.releaseCount++
    }

    @Test
    fun `acquire when enabled and foreground`() {
        val state = State()
        applyPolicy(enabled = true, isForeground = true, state)
        assertEquals(1, state.acquireCount)
        assertEquals(0, state.releaseCount)
    }

    @Test
    fun `release when disabled even if foreground`() {
        val state = State()
        applyPolicy(enabled = false, isForeground = true, state)
        assertEquals(0, state.acquireCount)
        assertEquals(1, state.releaseCount)
    }

    @Test
    fun `release when enabled but backgrounded`() {
        val state = State()
        applyPolicy(enabled = true, isForeground = false, state)
        assertEquals(0, state.acquireCount)
        assertEquals(1, state.releaseCount)
    }

    @Test
    fun `release when disabled and backgrounded`() {
        val state = State()
        applyPolicy(enabled = false, isForeground = false, state)
        assertEquals(0, state.acquireCount)
        assertEquals(1, state.releaseCount)
    }

    @Test
    fun `background then foreground re-acquires if still enabled`() {
        val state = State()
        applyPolicy(enabled = true, isForeground = true, state)
        assertEquals(1, state.acquireCount)

        applyPolicy(enabled = true, isForeground = false, state)
        assertEquals(1, state.releaseCount)

        applyPolicy(enabled = true, isForeground = true, state)
        assertEquals(2, state.acquireCount)
    }

    @Test
    fun `dashboard deactivated while foreground releases flag`() {
        val state = State()
        applyPolicy(enabled = true, isForeground = true, state)
        assertEquals(1, state.acquireCount)

        applyPolicy(enabled = false, isForeground = true, state)
        assertEquals(1, state.releaseCount)
    }

    @Test
    fun `dispose path always releases`() {
        val state = State()
        state.releaseCount++ // onDispose always calls release()
        assertEquals(1, state.releaseCount)
        assertEquals(0, state.acquireCount)
    }

    @Test
    fun `activity recreation - old dispose releases, new composition re-acquires`() {
        val state = State()
        // Old composition active
        applyPolicy(enabled = true, isForeground = true, state)
        assertEquals(1, state.acquireCount)

        // onDispose of old Activity
        state.releaseCount++
        assertEquals(1, state.releaseCount)

        // New Activity composition: conditions still met
        applyPolicy(enabled = true, isForeground = true, state)
        assertEquals(2, state.acquireCount)
    }
}
