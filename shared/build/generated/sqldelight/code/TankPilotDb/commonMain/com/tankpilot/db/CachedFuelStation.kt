package com.tankpilot.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class CachedFuelStation(
  public val provider: String,
  public val providerId: String,
  public val name: String,
  public val brand: String?,
  public val latitude: Double,
  public val longitude: Double,
  public val address: String?,
  public val fetchedAt: Long,
)
