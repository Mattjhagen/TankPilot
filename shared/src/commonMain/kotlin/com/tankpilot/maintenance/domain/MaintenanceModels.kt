package com.tankpilot.maintenance.domain

import com.tankpilot.core.Miles
import com.tankpilot.core.Money
import kotlinx.datetime.Instant
import kotlin.time.Duration

enum class MaintenanceType {
    OIL_CHANGE,
    TIRE_ROTATION,
    ENGINE_AIR_FILTER,
    CABIN_AIR_FILTER,
    BATTERY,
    COOLANT,
    TRANSMISSION_SERVICE,
    BRAKE_INSPECTION,
    CUSTOM
}

data class MaintenanceSchedule(
    val id: String,
    val vehicleId: String,
    val type: MaintenanceType,
    val displayName: String,
    val mileageInterval: Miles?,
    val timeInterval: Duration?,
    val dueSoonMileageThreshold: Miles?,
    val dueSoonTimeThreshold: Duration?,
    val isEnabled: Boolean
)

data class MaintenanceEvent(
    val id: String,
    val vehicleId: String,
    val type: MaintenanceType,
    val completedAt: Instant,
    val odometer: Miles?,
    val cost: Money?,
    val notes: String?
)

enum class MaintenanceDueState {
    OK,
    DUE_SOON,
    DUE,
    OVERDUE,
    UNKNOWN
}

data class MaintenanceTaskStatus(
    val schedule: MaintenanceSchedule,
    val lastEvent: MaintenanceEvent?,
    val dueState: MaintenanceDueState,
    val remainingMiles: Miles?,
    val remainingDuration: Duration?
)
