package com.tankpilot.db

import kotlin.String

public data class CachedQueryStation(
  public val queryCellId: String,
  public val stationProvider: String,
  public val stationProviderId: String,
)
