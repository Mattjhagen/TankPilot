package com.tankpilot.fuel.domain

import com.tankpilot.vehicle.domain.Vehicle
import com.tankpilot.vehicle.domain.VehicleRepository
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.trip.domain.Trip
import com.tankpilot.fillup.domain.FillUp
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.abs

class CalibrationEngine(
    private val vehicleRepository: VehicleRepository,
    private val tripRepository: TripRepository
) {
    suspend fun calibrate(
        vehicle: Vehicle,
        currentFill: FillUp,
        allFillUps: List<FillUp>
    ): CalibrationSegment? {
        val allFills = (allFillUps + currentFill).sortedBy { it.timestamp }
        val fullFills = allFills.filter { it.isFull }
        
        if (fullFills.size < 2) return null
        
        val lastFill = fullFills[fullFills.lastIndex]
        val prevFill = fullFills[fullFills.lastIndex - 1]
        
        // 1. Calculate distance traveled in this segment
        val lastOdo = lastFill.odometer
        val prevOdo = prevFill.odometer
        val distance = if (lastOdo != null && prevOdo != null) {
            lastOdo - prevOdo
        } else {
            val tripsBetween = tripRepository.getTrips(vehicle.id).firstOrNull() ?: emptyList()
            tripsBetween.filter { it.timestamp in (prevFill.timestamp + 1)..lastFill.timestamp }
                .sumOf { it.distance }
        }

        if (distance <= 0.1) return null

        // 2. Sum gallons added during the segment
        val fillsInSegment = allFills.filter { it.timestamp in (prevFill.timestamp + 1)..lastFill.timestamp }
        val totalGallonsAdded = fillsInSegment.sumOf { it.gallonsAdded }
        
        if (totalGallonsAdded <= 0.0) return null

        // 3. Compute measured MPG
        val measuredMpg = distance / totalGallonsAdded
        if (measuredMpg < 5.0 || measuredMpg > 80.0) {
            return null
        }

        // 4. Calculate prediction error
        val tripsSincePrevFill = (tripRepository.getTrips(vehicle.id).firstOrNull() ?: emptyList())
            .filter { it.timestamp in (prevFill.timestamp + 1)..lastFill.timestamp }
        val predictedBurned = tripsSincePrevFill.sumOf { it.fuelBurned }
        
        val predictedRemaining = maxOf(0.0, vehicle.tankCapacity - predictedBurned)
        val actualRemaining = maxOf(0.0, vehicle.tankCapacity - lastFill.gallonsAdded)
        val predictionErrorGallons = predictedRemaining - actualRemaining
        val percentError = if (vehicle.tankCapacity > 0) abs(predictionErrorGallons) / vehicle.tankCapacity else 0.0

        // 5. Update vehicle parameters via EWMA (alpha = 0.3)
        val alpha = 0.3
        val currentLearnedMpg = vehicle.learnedMpg
        val newLearnedMpg = (alpha * measuredMpg) + ((1.0 - alpha) * currentLearnedMpg)

        vehicleRepository.updateLearnedMpg(vehicle.id, newLearnedMpg)

        return CalibrationSegment(
            startingFullFillTimestamp = prevFill.timestamp,
            endingFullFillTimestamp = lastFill.timestamp,
            startingOdometer = prevFill.odometer,
            endingOdometer = lastFill.odometer,
            accumulatedDistance = distance,
            allGallonsAddedBetween = totalGallonsAdded,
            distanceConfidence = if (lastOdo != null) 0.95 else 0.70,
            dataCompletenessFlag = true
        )
    }
}
