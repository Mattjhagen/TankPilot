package com.tankpilot.trip.domain

data class Trip(
    val id: String,
    val vehicleId: String,
    val timestamp: Long,
    val distance: Double,
    val duration: Long,
    val idleTime: Long,
    val averageSpeed: Double,
    val drivingType: DrivingType,
    val fuelBurned: Double,
    val maxSpeedKmh: Double? = null,
    val highwayPercentage: Double? = null
)

enum class DrivingType {
    CITY,
    HIGHWAY,
    MIXED
}
