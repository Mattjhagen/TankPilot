package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.CurrencyMicros
import com.tankpilot.core.FuelType
import com.tankpilot.core.Gallons
import com.tankpilot.core.Money
import com.tankpilot.core.FuelPriceUnit
import com.tankpilot.core.FuelPricePerUnit
import com.tankpilot.core.Miles
import com.tankpilot.core.MilesPerGallon
import com.tankpilot.core.StationId
import kotlinx.datetime.Clock
import kotlin.math.max

object FuelRescueEngine {

    /**
     * Evaluates and ranks a list of nearby fuel stations for a vehicle.
     * Returns a sorted list of FuelStationRecommendation, highest score first.
     */
    fun evaluateStations(
        estimatedRemaining: Gallons,
        learnedMpg: MilesPerGallon,
        confidenceLevel: ConfidenceLevel,
        vehicleFuelType: FuelType,
        vehicleFuelGradeKey: String,
        reserveFuel: Gallons,
        tankCapacity: Gallons,
        candidates: List<FuelStation>,
        routeDistances: Map<StationId, Pair<Double, Double>>, // StationId -> Pair(DistanceMiles, DurationMinutes)
        fallbackPrice: FuelPricePerUnit? = null
    ): List<FuelStationRecommendation> {
        if (candidates.isEmpty()) return emptyList()

        // 1. Expected physical MPG
        val expectedMpg = learnedMpg.value

        // 2. Confidence Safety Multiplier (applied exactly once to usable fuel for safe range)
        val confidenceMultiplier = when (confidenceLevel) {
            ConfidenceLevel.VERY_HIGH -> 0.95
            ConfidenceLevel.HIGH -> 0.90
            ConfidenceLevel.MEDIUM -> 0.85
            ConfidenceLevel.LOW -> 0.80
        }
        val usableFuelVal = max(0.0, estimatedRemaining.value - reserveFuel.value)
        val safeRangeMiles = usableFuelVal * expectedMpg * confidenceMultiplier

        // 3. Compute the Median Compatible, Non-Stale Price (Local Reference Price)
        val currencyCode = candidates.firstOrNull()?.fuelPrices?.firstOrNull()?.price?.money?.currencyCode ?: "USD"
        
        val activePrices = candidates.flatMap { it.fuelPrices }
            .filter { it.fuelType == vehicleFuelType && it.freshness != PriceFreshness.STALE }
            .map { it.price.toPerGallon().money.amountMicros.value }
            .sorted()

        val referencePriceVal = when {
            activePrices.isNotEmpty() -> {
                val mid = activePrices.size / 2
                if (activePrices.size % 2 == 0) {
                    (activePrices[mid - 1] + activePrices[mid]) / 2
                } else {
                    activePrices[mid]
                }
            }
            fallbackPrice != null -> fallbackPrice.toPerGallon().money.amountMicros.value
            else -> 4_000_000L // $4.00 fallback in micros
        }
        val referencePrice = FuelPricePerUnit(
            Money(CurrencyMicros(referencePriceVal), currencyCode),
            FuelPriceUnit.PER_GALLON
        )

        // 4. Pre-process candidates
        val recommendations = candidates.map { station ->
            val routeInfo = routeDistances[station.id]
            val distance = routeInfo?.first ?: station.routeDistanceMiles
            val duration = routeInfo?.second ?: station.estimatedDriveMinutes ?: (distance?.let { it / 30.0 * 60.0 }) // fallback 30mph

            // Travel consumption uses physical MPG (no safety reduction)
            val fuelUsedVal = if (distance != null) distance / expectedMpg else 0.0
            val fuelRemainingOnArrivalVal = estimatedRemaining.value - fuelUsedVal

            val reachability = when {
                distance == null -> ReachabilityStatus.UNKNOWN
                distance <= safeRangeMiles -> ReachabilityStatus.SAFELY_REACHABLE
                fuelRemainingOnArrivalVal > 0.0 -> ReachabilityStatus.MARGINALLY_REACHABLE
                else -> ReachabilityStatus.OUTSIDE_SAFE_RANGE
            }

            val priceInfo = station.fuelPrices.firstOrNull { 
                it.fuelType == vehicleFuelType && (vehicleFuelGradeKey.isEmpty() || it.fuelGradeKey == vehicleFuelGradeKey)
            }
            val pricePerGallon = priceInfo?.price?.toPerGallon()

            // Cost calculations
            val calcPricePerGallon = pricePerGallon ?: referencePrice
            val priceMicros = calcPricePerGallon.money.amountMicros.value

            // Fuel purchased on arrival to fill the tank to capacity
            val fuelOnArrival = max(0.0, fuelRemainingOnArrivalVal)
            val gallonsPurchased = max(0.0, tankCapacity.value - fuelOnArrival)
            val purchaseCostMicros = (gallonsPurchased * priceMicros).toLong()
            val purchaseCost = Money(CurrencyMicros(purchaseCostMicros), currencyCode)

            // Travel cost is fuel to station * reference price
            val travelCostMicros = (fuelUsedVal * referencePrice.money.amountMicros.value).toLong()
            val travelCost = Money(CurrencyMicros(travelCostMicros), currencyCode)

            val effectiveTripCost = purchaseCost + travelCost

            val detour = 0.0

            // Scoring (0 to 100)
            var score = 0.0
            val warnings = mutableListOf<String>()

            // A. Safety (50 pts)
            when (reachability) {
                ReachabilityStatus.SAFELY_REACHABLE -> score += 50.0
                ReachabilityStatus.MARGINALLY_REACHABLE -> {
                    score += 20.0
                    warnings.add("Marginally within range — estimated ${roundToOneDecimal(fuelRemainingOnArrivalVal)} gallons remaining on arrival.")
                }
                ReachabilityStatus.OUTSIDE_SAFE_RANGE -> {
                    score += 0.0
                    warnings.add("Outside safe range — expected to run out of fuel before arrival.")
                }
                ReachabilityStatus.UNKNOWN -> {
                    score += 0.0
                    warnings.add("No route distance available.")
                }
            }

            // B. Freshness (10 pts)
            val freshness = priceInfo?.freshness ?: PriceFreshness.UNKNOWN
            when (freshness) {
                PriceFreshness.RECENT -> score += 10.0
                PriceFreshness.AGING -> score += 7.0
                PriceFreshness.STALE -> {
                    score += 3.0
                    warnings.add("Price may be outdated — last reported ${formatHoursAgo(priceInfo?.updatedAt)}")
                }
                PriceFreshness.UNKNOWN -> {
                    score += 0.0
                    warnings.add("Price age unknown.")
                }
            }

            // C. Open Status (5 pts)
            val isOpen = station.isOpen
            when (isOpen) {
                true -> score += 5.0
                false -> {
                    score -= 100.0 // heavily penalize closed stations so they go to the bottom
                    warnings.add("Station is currently closed.")
                }
                null -> score += 3.0
            }

            if (pricePerGallon == null) {
                warnings.add("Fuel price is unavailable.")
            }

            // If not safely reachable, cap score at 0.0
            if (reachability != ReachabilityStatus.SAFELY_REACHABLE) {
                score = 0.0
            }

            FuelStationRecommendation(
                station = station.copy(
                    routeDistanceMiles = distance,
                    estimatedDriveMinutes = duration
                ),
                reachabilityStatus = reachability,
                estimatedFuelUsedToReach = Gallons(fuelUsedVal),
                estimatedFuelRemainingOnArrival = Gallons(max(0.0, fuelRemainingOnArrivalVal)),
                advertisedPrice = pricePerGallon,
                effectiveTripCost = effectiveTripCost,
                estimatedFillCost = purchaseCost,
                estimatedSavings = Money(CurrencyMicros(0L), currencyCode), // computed below
                detourMiles = detour,
                priceFreshness = freshness,
                recommendationScore = max(0.0, score),
                recommendationReasons = emptyList(),
                warningMessages = warnings
            )
        }

        // 5. Filter for eligible recommendations
        val eligible = recommendations.filter {
            it.reachabilityStatus == ReachabilityStatus.SAFELY_REACHABLE &&
            it.station.routeDistanceMiles != null &&
            it.advertisedPrice != null &&
            it.station.isOpen != false
        }

        // Closest safe station among eligible candidates
        val closestSafe = eligible.minByOrNull { it.station.routeDistanceMiles!! }
        val cheapestSafe = eligible.minByOrNull { it.effectiveTripCost.amountMicros.value }

        // Find min and max cost for eligible stations to normalize the cost score (20 pts)
        val eligibleCosts = eligible.map { it.effectiveTripCost.amountMicros.value }
        val minCost = eligibleCosts.minOrNull() ?: 0L
        val maxCost = eligibleCosts.maxOrNull() ?: 0L

        val finalRecommendations = recommendations.map { rec ->
            var finalScore = rec.recommendationScore
            val reasons = mutableListOf<String>()
            var detour = 0.0

            if (rec.reachabilityStatus == ReachabilityStatus.SAFELY_REACHABLE && rec.station.routeDistanceMiles != null) {
                // Calculate actual detour
                if (closestSafe != null) {
                    detour = max(0.0, rec.station.routeDistanceMiles - closestSafe.station.routeDistanceMiles!!)
                }
                
                // Adjust detour score (up to 15 pts)
                val detourPoints = max(0.0, 15.0 - (detour * 2.0))
                finalScore += detourPoints

                // Adjust cost score (up to 20 pts)
                if (rec.advertisedPrice != null) {
                    val currentCost = rec.effectiveTripCost.amountMicros.value
                    val costPoints = if (maxCost > minCost) {
                        20.0 * (1.0 - (currentCost - minCost).toDouble() / (maxCost - minCost).toDouble())
                    } else {
                        20.0
                    }
                    finalScore += costPoints
                }
            }

            // Calculate savings relative to the closest safe station
            val savings = if (closestSafe != null && rec.reachabilityStatus == ReachabilityStatus.SAFELY_REACHABLE) {
                val diff = closestSafe.effectiveTripCost.amountMicros.value - rec.effectiveTripCost.amountMicros.value
                Money(CurrencyMicros(max(0L, diff)), currencyCode)
            } else {
                Money(CurrencyMicros(0L), currencyCode)
            }

            // Assign badges / reasons if eligible
            val isEligible = eligible.any { it.station.id == rec.station.id }
            if (isEligible) {
                if (rec.station.id == closestSafe?.station?.id) {
                    reasons.add("Closest safe option — estimated ${roundToOneDecimal(rec.estimatedFuelRemainingOnArrival.value)} gallons remaining on arrival.")
                }
                if (rec.station.id == cheapestSafe?.station?.id && cheapestSafe.station.id != closestSafe?.station?.id) {
                    reasons.add("Cheapest reachable option.")
                }
                if (savings.amountMicros.value > 500_000L) { // saves > $0.50
                    val dollars = savings.amountMicros.value.toDouble() / 1_000_000.0
                    reasons.add("Saves $${roundToTwoDecimals(dollars)} compared to closest station.")
                }
            }

            rec.copy(
                detourMiles = detour,
                estimatedSavings = savings,
                recommendationScore = max(0.0, finalScore),
                recommendationReasons = reasons
            )
        }

        return finalRecommendations.sortedByDescending { it.recommendationScore }
    }

    private fun roundToOneDecimal(value: Double): Double {
        return (value * 10).toInt() / 10.0
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100).toInt() / 100.0
    }

    private fun formatHoursAgo(timestampMs: Long?): String {
        if (timestampMs == null) return "unknown hours ago"
        val diffMs = Clock.System.now().toEpochMilliseconds() - timestampMs
        val hours = max(1L, diffMs / (1000 * 60 * 60))
        return if (hours == 1L) "1 hour ago" else "$hours hours ago"
    }
}
