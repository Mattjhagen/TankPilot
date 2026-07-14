package com.tankpilot.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class FillUp(
  public val id: String,
  public val vehicleId: String,
  public val timestamp: Long,
  public val gallonsAdded: Double,
  public val price: Double,
  public val odometer: Double?,
  public val isFull: Long,
)
