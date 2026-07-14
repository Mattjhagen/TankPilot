package com.tankpilot.maintenance.domain

import kotlinx.datetime.Instant

enum class MaintenanceDueState {
    OK,
    DUE_SOON,
    OVERDUE
}

enum class MaintenancePriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class MaintenanceTask(
    val id: String,
    val name: String,
    val description: String,
    val priority: MaintenancePriority,
    val intervalMiles: Double?,
    val intervalMonths: Int?,
    val lastCompletedMiles: Double?,
    val lastCompletedDate: Instant?
)

data class MaintenanceStatus(
    val task: MaintenanceTask,
    val state: MaintenanceDueState,
    val milesRemaining: Double?,
    val daysRemaining: Int?
)
