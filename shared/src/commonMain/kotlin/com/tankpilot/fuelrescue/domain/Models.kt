package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.FuelType
import com.tankpilot.core.Gallons
import com.tankpilot.core.Money
import com.tankpilot.core.FuelPricePerUnit
import com.tankpilot.core.StationId
import com.tankpilot.core.StationProvider

enum class PriceFreshness {
    RECENT,   // Provider update timestamp <= 6 hours
    AGING,    // Provider update timestamp > 6 hours and <= 24 hours
    STALE,    // Provider update timestamp > 24 hours
    UNKNOWN   // Provider update timestamp unavailable or future timestamp
}

enum class ReachabilityStatus {
    SAFELY_REACHABLE,
    MARGINALLY_REACHABLE,
    OUTSIDE_SAFE_RANGE,
    UNKNOWN
}

data class StationFuelPrice(
    val fuelType: FuelType,
    val fuelGradeKey: String,
    val displayFuelGrade: String?,
    val price: FuelPricePerUnit,
    val updatedAt: Long?,
    val freshness: PriceFreshness,
    val source: String
)

data class FuelStation(
    val id: StationId,
    val name: String,
    val brand: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val distanceMiles: Double,
    val routeDistanceMiles: Double?,
    val estimatedDriveMinutes: Double?,
    val isOpen: Boolean?,
    val navigationDestination: String?,
    val fuelPrices: List<StationFuelPrice>,
    val lastFetchedAt: Long,
    // Set only by the release-variant Phase A demo provider (ReleaseDemoFuelStationProvider).
    // Real/mock production data always leaves this false. Android Auto uses this to bypass
    // fuel-safety filtering for POI-visibility validation and to disable navigation hand-off
    // against a synthetic location.
    val isDemoData: Boolean = false
)

data class FuelStationRecommendation(
    val station: FuelStation,
    val reachabilityStatus: ReachabilityStatus,
    val estimatedFuelUsedToReach: Gallons,
    val estimatedFuelRemainingOnArrival: Gallons,
    val advertisedPrice: FuelPricePerUnit?,
    val effectiveTripCost: Money,
    val estimatedFillCost: Money,
    val estimatedSavings: Money,
    val detourMiles: Double,
    val priceFreshness: PriceFreshness,
    val recommendationScore: Double,
    val recommendationReasons: List<String>,
    val warningMessages: List<String>
)
