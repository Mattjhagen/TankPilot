package com.tankpilot.vehicle.domain

import com.tankpilot.core.FuelType
import com.tankpilot.core.UnitSystem

data class Vehicle(
    val id: String,
    val year: Int,
    val make: String,
    val model: String,
    val trim: String?,
    val color: String?,
    val engine: String,
    val engineDisplacementLiters: Double?,
    val cylinderCount: Long?,
    val tankCapacity: Double, // Gallons
    val factoryCityMpg: Double,
    val factoryHwyMpg: Double,
    val learnedMpg: Double,
    val preferredFuelType: FuelType,
    val preferredFuelGrade: String?,
    val unitSystem: UnitSystem,
    val reserveFuelGallons: Double,
    val lowFuelThresholdPercent: Double
)
