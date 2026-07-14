package com.tankpilot.fillup.domain

data class FillUp(
    val id: String,
    val vehicleId: String,
    val timestamp: Long,
    val gallonsAdded: Double,
    val price: Double,
    val odometer: Double?,
    val isFull: Boolean
)
