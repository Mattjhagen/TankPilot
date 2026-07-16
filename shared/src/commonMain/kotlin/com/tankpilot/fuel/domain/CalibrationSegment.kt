package com.tankpilot.fuel.domain

data class CalibrationSegment(
    val startingFullFillTimestamp: Long,
    val endingFullFillTimestamp: Long,
    val startingOdometer: Double?,
    val endingOdometer: Double?,
    val accumulatedDistance: Double,
    val allGallonsAddedBetween: Double,
    val distanceConfidence: Double,
    val dataCompletenessFlag: Boolean
)
