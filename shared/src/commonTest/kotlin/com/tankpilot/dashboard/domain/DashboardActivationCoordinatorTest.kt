package com.tankpilot.dashboard.domain

import com.tankpilot.telemetry.domain.TelemetryData
import com.tankpilot.trip.domain.TripSessionState
import com.tankpilot.core.FakeAppClock
import kotlinx.datetime.Instant
import kotlin.test.*

/**
 * Boundary policy (inclusive/exclusive):
 *   elapsed <= 30 minutes          → ACTIVE  (auto-restore, inclusive)
 *   30m < elapsed <= 4h            → CONFIRMATION_REQUIRED
 *   elapsed > 4h                   → INACTIVE (discard)
 *   lastActivityTimestamp = 0      → INACTIVE (treated as missing)
 *   lastActivityTimestamp > now    → INACTIVE (future = invalid)
 *   isVisible = false              → INACTIVE (no session to restore)
 */
class DashboardActivationCoordinatorTest {

    // ── Helpers ──────────────────────────────────────────────────────────────

    private val thirtyMinsMs = 30 * 60 * 1000L
    private val fourHoursMs  = 4 * 60 * 60 * 1000L

    /** Creates a coordinator whose "now" is [nowMs] ms past epoch. */
    private fun coordinatorAt(nowMs: Long) = DashboardActivationCoordinator(
        isAutoModeEnabled = true,
        clock = FakeAppClock(Instant.fromEpochMilliseconds(nowMs))
    )

    /** Visible session whose last activity was [activityMs] ms past epoch. */
    private fun visibleSession(activityMs: Long) = DashboardSessionState(
        isVisible = true,
        lastActivityTimestamp = activityMs
    )

    // ── Pre-existing tests ────────────────────────────────────────────────────

    @Test
    fun `initial state is INACTIVE`() {
        val coordinator = DashboardActivationCoordinator(isAutoModeEnabled = true)
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
    }

    @Test
    fun `manual enter and exit`() {
        val coordinator = DashboardActivationCoordinator(isAutoModeEnabled = true)
        coordinator.manualEnter()
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
        coordinator.manualExit()
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
        coordinator.manualEnter()
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
    }

    // ── In-range tests (existing) ─────────────────────────────────────────────

    @Test
    fun `restoreState inside 30 minutes → ACTIVE`() {
        val nowMs = thirtyMinsMs * 2
        val coordinator = coordinatorAt(nowMs)
        coordinator.restoreState(visibleSession(activityMs = nowMs - 25 * 60_000))
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
    }

    @Test
    fun `restoreState between 30m and 4h → CONFIRMATION_REQUIRED`() {
        val nowMs = 2 * 60 * 60_000L // 2 hours
        val coordinator = coordinatorAt(nowMs)
        coordinator.restoreState(visibleSession(activityMs = nowMs - 60 * 60_000L))
        assertEquals(DashboardMode.CONFIRMATION_REQUIRED, coordinator.dashboardMode.value)
    }

    @Test
    fun `restoreState over 4 hours → INACTIVE`() {
        val nowMs = 5 * 60 * 60_000L
        val coordinator = coordinatorAt(nowMs)
        coordinator.restoreState(visibleSession(activityMs = nowMs - 4 * 60 * 60_000L - 1))
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
    }

    // ── Exact boundary tests ──────────────────────────────────────────────────

    @Test
    fun `restoreState exactly 30 minutes elapsed → ACTIVE (inclusive boundary)`() {
        val nowMs = 60 * 60_000L // 1h
        val coordinator = coordinatorAt(nowMs)
        // elapsed = exactly thirtyMinsMs → should auto-restore
        coordinator.restoreState(visibleSession(activityMs = nowMs - thirtyMinsMs))
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
    }

    @Test
    fun `restoreState 30 minutes plus 1ms elapsed → CONFIRMATION_REQUIRED`() {
        val nowMs = 60 * 60_000L
        val coordinator = coordinatorAt(nowMs)
        coordinator.restoreState(visibleSession(activityMs = nowMs - thirtyMinsMs - 1))
        assertEquals(DashboardMode.CONFIRMATION_REQUIRED, coordinator.dashboardMode.value)
    }

    @Test
    fun `restoreState exactly 4 hours elapsed → CONFIRMATION_REQUIRED (inclusive boundary)`() {
        val nowMs = 5 * 60 * 60_000L
        val coordinator = coordinatorAt(nowMs)
        // elapsed = exactly fourHoursMs → still requires confirmation, not discarded
        coordinator.restoreState(visibleSession(activityMs = nowMs - fourHoursMs))
        assertEquals(DashboardMode.CONFIRMATION_REQUIRED, coordinator.dashboardMode.value)
    }

    @Test
    fun `restoreState 4 hours plus 1ms elapsed → INACTIVE`() {
        val nowMs = 5 * 60 * 60_000L
        val coordinator = coordinatorAt(nowMs)
        coordinator.restoreState(visibleSession(activityMs = nowMs - fourHoursMs - 1))
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
    }

    // ── Invalid timestamp tests ───────────────────────────────────────────────

    @Test
    fun `restoreState with zero lastActivityTimestamp → INACTIVE`() {
        val coordinator = coordinatorAt(60 * 60_000L)
        coordinator.restoreState(DashboardSessionState(isVisible = true, lastActivityTimestamp = 0L))
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
    }

    @Test
    fun `restoreState with future lastActivityTimestamp → INACTIVE`() {
        val nowMs = 60 * 60_000L
        val coordinator = coordinatorAt(nowMs)
        // Activity timestamp is in the future
        coordinator.restoreState(visibleSession(activityMs = nowMs + 5 * 60_000L))
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
    }

    @Test
    fun `restoreState when isVisible is false → INACTIVE`() {
        val coordinator = coordinatorAt(60 * 60_000L)
        coordinator.restoreState(DashboardSessionState(isVisible = false, lastActivityTimestamp = 1000L))
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
    }

    // ── confirmRestore / dismissRestore tests ────────────────────────────────

    @Test
    fun `confirmRestore transitions CONFIRMATION_REQUIRED to ACTIVE`() {
        val nowMs = 2 * 60 * 60_000L
        val coordinator = coordinatorAt(nowMs)
        coordinator.restoreState(visibleSession(activityMs = nowMs - 60 * 60_000L))
        assertEquals(DashboardMode.CONFIRMATION_REQUIRED, coordinator.dashboardMode.value)

        coordinator.confirmRestore()
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
        assertNull(coordinator.pendingSessionState.value)
    }

    @Test
    fun `dismissRestore transitions CONFIRMATION_REQUIRED to INACTIVE`() {
        val nowMs = 2 * 60 * 60_000L
        val coordinator = coordinatorAt(nowMs)
        coordinator.restoreState(visibleSession(activityMs = nowMs - 60 * 60_000L))
        assertEquals(DashboardMode.CONFIRMATION_REQUIRED, coordinator.dashboardMode.value)

        coordinator.dismissRestore()
        assertEquals(DashboardMode.INACTIVE, coordinator.dashboardMode.value)
        assertNull(coordinator.pendingSessionState.value)
    }

    @Test
    fun `pendingSessionState is set during CONFIRMATION_REQUIRED`() {
        val nowMs = 2 * 60 * 60_000L
        val coordinator = coordinatorAt(nowMs)
        val session = visibleSession(activityMs = nowMs - 60 * 60_000L)
        coordinator.restoreState(session)

        assertNotNull(coordinator.pendingSessionState.value)
        assertEquals(session.lastActivityTimestamp, coordinator.pendingSessionState.value?.lastActivityTimestamp)
    }

    @Test
    fun `confirmRestore is no-op when not in CONFIRMATION_REQUIRED`() {
        val coordinator = DashboardActivationCoordinator(isAutoModeEnabled = true)
        coordinator.manualEnter()
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
        coordinator.confirmRestore() // should not crash or change state
        assertEquals(DashboardMode.ACTIVE, coordinator.dashboardMode.value)
    }
}

