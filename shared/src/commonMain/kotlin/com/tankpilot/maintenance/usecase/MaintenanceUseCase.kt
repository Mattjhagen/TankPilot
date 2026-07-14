package com.tankpilot.maintenance.usecase

import com.tankpilot.core.AppClock
import com.tankpilot.core.Miles
import com.tankpilot.maintenance.domain.*
import kotlinx.datetime.Instant
import kotlin.time.Duration

class MaintenanceUseCase(
    private val clock: AppClock
) {
    fun evaluateDueState(
        schedule: MaintenanceSchedule,
        latestEvent: MaintenanceEvent?,
        currentOdometer: Miles?
    ): MaintenanceTaskStatus {
        if (!schedule.isEnabled) {
            return MaintenanceTaskStatus(schedule, latestEvent, MaintenanceDueState.OK, null, null)
        }

        // Without a starting point, we can't calculate due status
        if (latestEvent == null) {
            return MaintenanceTaskStatus(schedule, null, MaintenanceDueState.UNKNOWN, null, null)
        }

        val now = clock.now()
        
        var timeDueState = MaintenanceDueState.OK
        var remainingDuration: Duration? = null
        
        if (schedule.timeInterval != null) {
            val elapsed = now - latestEvent.completedAt
            val remainingRaw = schedule.timeInterval - elapsed
            remainingDuration = if (remainingRaw.isPositive()) remainingRaw else Duration.ZERO
            
            val threshold = schedule.dueSoonTimeThreshold ?: (schedule.timeInterval * 0.1)
            timeDueState = when {
                !remainingRaw.isPositive() -> MaintenanceDueState.OVERDUE
                remainingRaw <= threshold -> MaintenanceDueState.DUE_SOON
                else -> MaintenanceDueState.OK
            }
        }

        var mileageDueState = MaintenanceDueState.OK
        var remainingMiles: Miles? = null

        if (schedule.mileageInterval != null) {
            if (currentOdometer != null && latestEvent.odometer != null) {
                val milesDriven = currentOdometer.value - latestEvent.odometer.value
                val remainingRaw = schedule.mileageInterval.value - milesDriven
                remainingMiles = if (remainingRaw >= 0) Miles(remainingRaw) else Miles(0.0)
                
                val threshold = schedule.dueSoonMileageThreshold?.value ?: (schedule.mileageInterval.value * 0.1)
                mileageDueState = when {
                    remainingRaw < 0 -> MaintenanceDueState.OVERDUE
                    remainingRaw <= threshold -> MaintenanceDueState.DUE_SOON
                    else -> MaintenanceDueState.OK
                }
            } else {
                mileageDueState = MaintenanceDueState.UNKNOWN
            }
        }

        val finalState = pickMostUrgentState(timeDueState, mileageDueState)
        
        return MaintenanceTaskStatus(
            schedule = schedule,
            lastEvent = latestEvent,
            dueState = finalState,
            remainingMiles = remainingMiles,
            remainingDuration = remainingDuration
        )
    }

    private fun pickMostUrgentState(state1: MaintenanceDueState, state2: MaintenanceDueState): MaintenanceDueState {
        val rank1 = rankState(state1)
        val rank2 = rankState(state2)
        return if (rank1 > rank2) state1 else state2
    }

    private fun rankState(state: MaintenanceDueState): Int {
        return when (state) {
            MaintenanceDueState.OVERDUE -> 4
            MaintenanceDueState.DUE -> 3
            MaintenanceDueState.DUE_SOON -> 2
            MaintenanceDueState.OK -> 1
            MaintenanceDueState.UNKNOWN -> 0
        }
    }
}
