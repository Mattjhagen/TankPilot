package com.tankpilot.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class CachedStationRoute(
  public val originCellId: String,
  public val stationProvider: String,
  public val stationProviderId: String,
  public val routeMode: String,
  public val distanceMiles: Double,
  public val durationSeconds: Long,
  public val fetchedAt: Long,
  public val expiresAt: Long,
)
