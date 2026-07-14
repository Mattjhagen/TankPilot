package com.tankpilot.fuel

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.Gallons
import com.tankpilot.core.Miles
import com.tankpilot.core.MilesPerGallon
import com.tankpilot.trip.domain.DrivingType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FuelEngineTest {

    @Test
    fun testIdleFuelRatePrioritization() {
        val rate1 = FuelEngine.estimateIdleFuelRate(2.0, 8L)
        assertEquals(0.16, rate1.value, 0.001)

        val rate2 = FuelEngine.estimateIdleFuelRate(null, 6L)
        assertEquals(0.30, rate2.value, 0.001)

        val rate3 = FuelEngine.estimateIdleFuelRate(null, 4L)
        assertEquals(0.15, rate3.value, 0.001)

        val rate4 = FuelEngine.estimateIdleFuelRate(null, null)
        assertEquals(0.20, rate4.value, 0.001)
    }

    @Test
    fun testEstimateFuelBurnedNormalTrip() {
        val fuelBurned = FuelEngine.estimateFuelBurned(
            distance = Miles(20.0),
            durationSeconds = 1800L,
            idleTimeSeconds = 300L,
            drivingType = DrivingType.HIGHWAY,
            displacementLiters = 2.0,
            cylinderCount = null,
            learnedMpg = MilesPerGallon(20.0),
            factoryCityMpg = 15.0,
            factoryHwyMpg = 25.0
        )

        // Drive fuel: 20 miles / (25 * scale) where scale = 20 / 19.5 = 1.025641
        // Effective Highway MPG = 25.6410. Drive fuel = 20 / 25.6410 = 0.78 Gallons.
        // Idle fuel: 300s / 3600s = 0.0833 hrs * 0.16 GPH = 0.0133 Gallons.
        // Total = 0.79333 Gallons
        assertEquals(0.7933, fuelBurned.value, 0.001)
    }

    @Test
    fun testCalculateSafeRange() {
        val rangeLow = FuelEngine.calculateSafeRange(
            remainingFuel = Gallons(10.0),
            learnedMpg = MilesPerGallon(20.0),
            confidenceLevel = ConfidenceLevel.LOW
        )
        assertEquals(160.0, rangeLow.value, 0.001)

        val rangeHigh = FuelEngine.calculateSafeRange(
            remainingFuel = Gallons(10.0),
            learnedMpg = MilesPerGallon(20.0),
            confidenceLevel = ConfidenceLevel.VERY_HIGH
        )
        assertEquals(190.0, rangeHigh.value, 0.001)
    }

    @Test
    fun testRecalibrateMpg() {
        val newMpg = FuelEngine.recalibrateMpg(
            currentLearnedMpg = MilesPerGallon(20.0),
            distanceTraveled = Miles(300.0),
            totalGallonsAdded = Gallons(12.0),
            learningRate = 0.3
        )
        assertEquals(21.5, newMpg.value, 0.001)
    }
}
