package com.tankpilot.trip.domain

data class DriveAutoStartConfig(
    val maxHorizontalAccuracyMeters: Double = 30.0,
    val maxSpeedAccuracyMps: Double = 3.0,
    val maxSampleAgeSeconds: Double = 10.0,
    val startThresholdMph: Double = 5.0,
    val resetThresholdMph: Double = 4.0,
    val confirmationDurationSeconds: Double = 5.0
)
