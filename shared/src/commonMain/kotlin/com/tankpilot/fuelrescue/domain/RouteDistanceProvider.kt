package com.tankpilot.fuelrescue.domain

interface RouteDistanceProvider {
    /**
     * Calculates routing distance and duration from the current location to multiple destinations.
     * Returns a map of Station ID to Pair(Distance in Miles, Duration in Minutes).
     */
    suspend fun calculateRouteDistances(
        originLat: Double,
        originLng: Double,
        destinations: List<Pair<String, Pair<Double, Double>>>
    ): Map<String, Pair<Double, Double>>
}
