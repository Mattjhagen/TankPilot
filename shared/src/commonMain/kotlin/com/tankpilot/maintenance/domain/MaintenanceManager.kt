package com.tankpilot.maintenance.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.max

object MaintenanceManager {

    /**
     * Evaluates the current state of a maintenance task based on current vehicle odometer and date.
     */
    fun evaluateStatus(
        task: MaintenanceTask,
        currentOdometerMiles: Double,
        currentDate: Instant = Clock.System.now(),
        dueSoonMilesThreshold: Double = 500.0,
        dueSoonDaysThreshold: Int = 30
    ): MaintenanceStatus {

        var milesRemaining: Double? = null
        var daysRemaining: Int? = null

        var state = MaintenanceDueState.OK

        // Evaluate miles
        if (task.intervalMiles != null && task.lastCompletedMiles != null) {
            val nextDueMiles = task.lastCompletedMiles + task.intervalMiles
            val diff = nextDueMiles - currentOdometerMiles
            milesRemaining = diff

            if (diff <= 0) {
                state = MaintenanceDueState.OVERDUE
            } else if (diff <= dueSoonMilesThreshold && state != MaintenanceDueState.OVERDUE) {
                state = MaintenanceDueState.DUE_SOON
            }
        }

        // Evaluate time
        if (task.intervalMonths != null && task.lastCompletedDate != null) {
            // Rough approximation of months for simplicity: 1 month = 30.44 days
            val intervalMs = task.intervalMonths * 30.44 * 24 * 60 * 60 * 1000
            val nextDueDateMs = task.lastCompletedDate.toEpochMilliseconds() + intervalMs.toLong()
            val currentMs = currentDate.toEpochMilliseconds()
            
            val diffMs = nextDueDateMs - currentMs
            val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            daysRemaining = diffDays

            if (diffDays <= 0) {
                state = MaintenanceDueState.OVERDUE
            } else if (diffDays <= dueSoonDaysThreshold && state != MaintenanceDueState.OVERDUE) {
                state = MaintenanceDueState.DUE_SOON
            }
        }

        return MaintenanceStatus(
            task = task,
            state = state,
            milesRemaining = milesRemaining,
            daysRemaining = daysRemaining
        )
    }

    /**
     * Gets the most critical relevant reminder to show on the dashboard.
     * Overdue outranks Due Soon. Critical outranks Low.
     */
    fun getHighestPriorityReminder(statuses: List<MaintenanceStatus>): MaintenanceStatus? {
        if (statuses.isEmpty()) return null

        val dueOrOverdue = statuses.filter { it.state != MaintenanceDueState.OK }
        if (dueOrOverdue.isEmpty()) return null

        return dueOrOverdue.sortedWith(
            compareBy<MaintenanceStatus> { 
                when (it.state) {
                    MaintenanceDueState.OVERDUE -> 0
                    MaintenanceDueState.DUE_SOON -> 1
                    MaintenanceDueState.OK -> 2
                }
            }.thenBy { 
                when (it.task.priority) {
                    MaintenancePriority.CRITICAL -> 0
                    MaintenancePriority.HIGH -> 1
                    MaintenancePriority.MEDIUM -> 2
                    MaintenancePriority.LOW -> 3
                }
            }
        ).firstOrNull()
    }
}
