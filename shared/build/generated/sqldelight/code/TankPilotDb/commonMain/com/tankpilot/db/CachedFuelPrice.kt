package com.tankpilot.db

import kotlin.Long
import kotlin.String

public data class CachedFuelPrice(
  public val stationProvider: String,
  public val stationProviderId: String,
  public val fuelType: String,
  public val fuelGradeKey: String,
  public val displayFuelGrade: String?,
  public val priceAmountMicros: Long,
  public val currencyCode: String,
  public val priceUnit: String,
  public val providerUpdatedAt: Long?,
  public val fetchedAt: Long,
)
