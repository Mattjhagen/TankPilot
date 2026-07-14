package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.StationId

/**
 * Car-side (and any future client) recommendation labeling. Deliberately separate from
 * FuelRescueEngine's own internal `eligible` set, which requires a price for every
 * candidate — including its closest-safe pick. TankPilot's Android Auto UX allows a
 * priceless station to still be labeled Closest Safe, just never Cheapest Reachable.
 *
 * This does not alter FuelRescueEngine's scores, savings, or its own
 * recommendationReasons — it only selects which already-ranked stations earn a
 * user-facing category badge. The ranking math itself lives entirely in
 * FuelRescueEngine.evaluateStations and is untouched.
 */
object FuelRescueEligibility {

    /**
     * A station may receive a normal recommendation label only when it is safely
     * reachable, has a valid route result, and is not known to be closed at arrival.
     * Compatible-fuel-availability is implied: SAFELY_REACHABLE is only assigned to
     * candidates the engine could resolve a route distance for. Price availability is
     * intentionally excluded here — see [categorize] for the separate, stricter
     * Cheapest Reachable requirement.
     */
    fun isEligibleForRecommendation(recommendation: FuelStationRecommendation): Boolean {
        return recommendation.reachabilityStatus == ReachabilityStatus.SAFELY_REACHABLE &&
            recommendation.station.routeDistanceMiles != null &&
            recommendation.station.isOpen != false
    }

    fun hasSafeRecommendation(recommendations: List<FuelStationRecommendation>): Boolean {
        return recommendations.any { isEligibleForRecommendation(it) }
    }

    /**
     * Selects up to one station per category from the already-ranked recommendation
     * list. A station may win more than one category (e.g. Best Overall and Closest
     * Safe can be the same station).
     */
    fun categorize(
        recommendations: List<FuelStationRecommendation>
    ): Map<StationId, Set<RecommendationCategory>> {
        val eligible = recommendations.filter { isEligibleForRecommendation(it) }
        if (eligible.isEmpty()) return emptyMap()

        val bestOverall = eligible.maxByOrNull { it.recommendationScore }
        val closestSafe = eligible.minByOrNull { it.station.routeDistanceMiles!! }
        val cheapestReachable = eligible
            .filter { it.advertisedPrice != null }
            .minByOrNull { it.effectiveTripCost.amountMicros.value }

        val result = mutableMapOf<StationId, MutableSet<RecommendationCategory>>()
        bestOverall?.let {
            result.getOrPut(it.station.id) { mutableSetOf() }.add(RecommendationCategory.BEST_OVERALL)
        }
        closestSafe?.let {
            result.getOrPut(it.station.id) { mutableSetOf() }.add(RecommendationCategory.CLOSEST_SAFE)
        }
        cheapestReachable?.let {
            result.getOrPut(it.station.id) { mutableSetOf() }.add(RecommendationCategory.CHEAPEST_REACHABLE)
        }
        return result
    }
}
