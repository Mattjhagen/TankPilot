package com.tankpilot.location.domain

import kotlinx.datetime.Instant

enum class LocationSampleSource {
    GPS,
    NETWORK,
    FUSED,
    UNKNOWN
}

enum class RoadContext {
    HIGHWAY_LIKELY,
    URBAN_LIKELY,
    UNKNOWN
}

data class LocationSample(
    val timestamp: Instant,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Double?,
    val speedAccuracyMps: Double?,
    val horizontalAccuracyMeters: Double?,
    val bearingDegrees: Double?,
    val roadContext: RoadContext = RoadContext.UNKNOWN,
    val source: LocationSampleSource = LocationSampleSource.GPS
)

enum class TrackingUnavailableReason {
    LOCATION_PERMISSION_DENIED,
    LOCATION_SERVICES_DISABLED,
    FOREGROUND_START_NOT_ALLOWED,
    PLAY_SERVICES_UNAVAILABLE,
    UNKNOWN
}

enum class SpeedSource {
    OBD,
    GPS,
    MOCK,
    UNKNOWN
}

data class SelectedSpeed(
    val valueKmh: Double?,
    val source: SpeedSource,
    val timestamp: Instant?,
    val isFresh: Boolean
)
