package com.tankpilot.core

enum class FuelType {
    REGULAR,
    MIDGRADE,
    PREMIUM,
    DIESEL,
    E85,
    UNKNOWN
}

enum class UnitSystem {
    IMPERIAL,
    METRIC
}

enum class ConfidenceLevel {
    VERY_HIGH,
    HIGH,
    MEDIUM,
    LOW
}

enum class StationProvider {
    GOOGLE_PLACES,
    HERE,
    TOMTOM,
    UNKNOWN
}

data class StationId(
    val provider: StationProvider,
    val providerId: String
)
