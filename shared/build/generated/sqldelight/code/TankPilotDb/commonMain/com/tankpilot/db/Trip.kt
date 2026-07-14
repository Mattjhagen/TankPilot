package com.tankpilot.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class Trip(
  public val id: String,
  public val vehicleId: String,
  public val timestamp: Long,
  public val distance: Double,
  public val duration: Long,
  public val idleTime: Long,
  public val averageSpeed: Double,
  public val drivingType: String,
  public val fuelBurned: Double,
)
