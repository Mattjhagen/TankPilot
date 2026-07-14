package com.tankpilot.vehicle.domain

data class Vehicle(
    val id: String,
    val year: Int,
    val make: String,
    val model: String,
    val engine: String,
    val tankCapacity: Double,
    val factoryCityMpg: Double,
    val factoryHwyMpg: Double,
    val learnedMpg: Double
)
