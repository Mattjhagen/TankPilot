package com.tankpilot.fuel.domain

import com.tankpilot.core.*
import com.tankpilot.vehicle.domain.VehicleRepository
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.fillup.domain.FillUpRepository
import com.tankpilot.fuel.FuelEngine
import com.tankpilot.confidence.ConfidenceEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FuelStateUseCase(
    private val vehicleRepository: VehicleRepository,
    private val tripRepository: TripRepository,
    private val fillUpRepository: FillUpRepository,
    private val scope: CoroutineScope
) {
    val currentVehicle = vehicleRepository.getVehicles().map { it.firstOrNull() }
        .stateIn(scope, SharingStarted.Lazily, null)

    private val trips = currentVehicle.flatMapLatest { v ->
        if (v == null) flowOf(emptyList()) else tripRepository.getTrips(v.id)
    }.stateIn(scope, SharingStarted.Lazily, emptyList())

    private val fillUps = currentVehicle.flatMapLatest { v ->
        if (v == null) flowOf(emptyList()) else fillUpRepository.getFillUps(v.id)
    }.stateIn(scope, SharingStarted.Lazily, emptyList())

    val estimatedFuelRemaining = combine(currentVehicle, trips, fillUps) { vehicle, tripsList, fillUpsList ->
        if (vehicle == null) return@combine Gallons(0.0)
        
        val lastFullFillIndex = fillUpsList.indexOfFirst { it.isFull }
        val (startTimeMs, startFuel) = if (lastFullFillIndex != -1) {
            val fill = fillUpsList[lastFullFillIndex]
            fill.timestamp to vehicle.tankCapacity
        } else {
            0L to vehicle.tankCapacity
        }

        val partialFillups = if (lastFullFillIndex > 0) fillUpsList.subList(0, lastFullFillIndex)
        else if (lastFullFillIndex == -1) fillUpsList else emptyList()
        
        val gallonsAdded = partialFillups.sumOf { it.gallonsAdded }
        val tripsSinceFull = tripsList.filter { it.timestamp > startTimeMs }
        val gallonsBurned = tripsSinceFull.sumOf { it.fuelBurned }

        val remaining = startFuel + gallonsAdded - gallonsBurned
        Gallons(maxOf(0.0, minOf(vehicle.tankCapacity, remaining)))
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), Gallons(0.0))

    val confidence = combine(trips, fillUps) { tripsList, fillUpsList ->
        ConfidenceEngine.calculateConfidence(fillUpsList, tripsList)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), ConfidenceLevel.LOW)

    val confidencePercent = combine(confidence, fillUps) { conf, fillUpsList ->
        val base = when (conf) {
            ConfidenceLevel.VERY_HIGH -> 96
            ConfidenceLevel.HIGH -> 88
            ConfidenceLevel.MEDIUM -> 72
            ConfidenceLevel.LOW -> 45
        }
        base + minOf(3, fillUpsList.size)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), 45)

    val safeRange = combine(estimatedFuelRemaining, currentVehicle, confidence) { remaining, vehicle, conf ->
        if (vehicle == null) return@combine Miles(0.0)
        FuelEngine.calculateSafeRange(remaining, MilesPerGallon(vehicle.learnedMpg), conf)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), Miles(0.0))

    val fuelStatus = combine(estimatedFuelRemaining, currentVehicle) { remaining, vehicle ->
        if (vehicle == null) return@combine FuelStatus.UNKNOWN
        when {
            remaining.value <= vehicle.reserveFuelGallons -> FuelStatus.CRITICAL
            vehicle.tankCapacity > 0.0 &&
                (remaining.value / vehicle.tankCapacity) <= vehicle.lowFuelThresholdPercent -> FuelStatus.LOW
            else -> FuelStatus.NORMAL
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), FuelStatus.UNKNOWN)
}
