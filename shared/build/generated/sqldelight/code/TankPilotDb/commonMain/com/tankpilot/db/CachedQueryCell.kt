package com.tankpilot.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class CachedQueryCell(
  public val id: String,
  public val provider: String,
  public val latitudeCell: Long,
  public val longitudeCell: Long,
  public val radiusMiles: Double,
  public val fuelType: String,
  public val fetchedAt: Long,
  public val expiresAt: Long,
)
