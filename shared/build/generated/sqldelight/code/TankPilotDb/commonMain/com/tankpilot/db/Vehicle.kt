package com.tankpilot.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class Vehicle(
  public val id: String,
  public val year: Long,
  public val make: String,
  public val model: String,
  public val trim: String?,
  public val color: String?,
  public val engine: String,
  public val engineDisplacementLiters: Double?,
  public val cylinderCount: Long?,
  public val tankCapacity: Double,
  public val factoryCityMpg: Double,
  public val factoryHwyMpg: Double,
  public val learnedMpg: Double,
  public val preferredFuelType: String,
  public val preferredFuelGrade: String?,
  public val unitSystem: String,
  public val reserveFuelGallons: Double,
  public val lowFuelThresholdPercent: Double,
)
