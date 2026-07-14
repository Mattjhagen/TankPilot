package com.tankpilot.maintenance.usecase

import com.tankpilot.core.Miles
import com.tankpilot.core.Money
import com.tankpilot.maintenance.domain.*
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.days

class MaintenanceUseCaseTest {

    private val fakeClock = com.tankpilot.core.FakeAppClock(Instant.fromEpochMilliseconds(1000000000000))
    private val useCase = MaintenanceUseCase(fakeClock)

    @Test
    fun testDisabledScheduleReturnsOk() {
        val schedule = MaintenanceSchedule(
            id = "s1", vehicleId = "v1", type = MaintenanceType.OIL_CHANGE, displayName = "Oil Change",
            mileageInterval = Miles(5000.0), timeInterval = 180.days,
            dueSoonMileageThreshold = null, dueSoonTimeThreshold = null,
            isEnabled = false
        )
        val result = useCase.evaluateDueState(schedule, null, Miles(1000.0))
        assertEquals(MaintenanceDueState.OK, result.dueState)
    }

    @Test
    fun testNoHistoryReturnsUnknown() {
        val schedule = MaintenanceSchedule(
            id = "s1", vehicleId = "v1", type = MaintenanceType.OIL_CHANGE, displayName = "Oil Change",
            mileageInterval = Miles(5000.0), timeInterval = 180.days,
            dueSoonMileageThreshold = null, dueSoonTimeThreshold = null,
            isEnabled = true
        )
        val result = useCase.evaluateDueState(schedule, null, Miles(1000.0))
        assertEquals(MaintenanceDueState.UNKNOWN, result.dueState)
    }

    @Test
    fun testMileageOverdue() {
        val schedule = MaintenanceSchedule(
            id = "s1", vehicleId = "v1", type = MaintenanceType.OIL_CHANGE, displayName = "Oil Change",
            mileageInterval = Miles(5000.0), timeInterval = 180.days,
            dueSoonMileageThreshold = null, dueSoonTimeThreshold = null,
            isEnabled = true
        )
        val event = MaintenanceEvent(
            id = "e1", vehicleId = "v1", type = MaintenanceType.OIL_CHANGE,
            completedAt = fakeClock.now() - 10.days,
            odometer = Miles(10000.0), cost = Money.usd(50.0), notes = null
        )
        // 5000 interval. Due at 15000. Current is 16000 -> Overdue
        val result = useCase.evaluateDueState(schedule, event, Miles(16000.0))
        assertEquals(MaintenanceDueState.OVERDUE, result.dueState)
    }

    @Test
    fun testTimeDueSoonOutranksMileageOk() {
        val schedule = MaintenanceSchedule(
            id = "s1", vehicleId = "v1", type = MaintenanceType.OIL_CHANGE, displayName = "Oil Change",
            mileageInterval = Miles(5000.0), timeInterval = 180.days,
            dueSoonMileageThreshold = null, dueSoonTimeThreshold = 14.days,
            isEnabled = true
        )
        val event = MaintenanceEvent(
            id = "e1", vehicleId = "v1", type = MaintenanceType.OIL_CHANGE,
            completedAt = fakeClock.now() - 170.days, // 10 days remaining
            odometer = Miles(10000.0), cost = Money.usd(50.0), notes = null
        )
        // Mileage is fine, but time is due soon
        val result = useCase.evaluateDueState(schedule, event, Miles(11000.0))
        assertEquals(MaintenanceDueState.DUE_SOON, result.dueState)
    }
}
