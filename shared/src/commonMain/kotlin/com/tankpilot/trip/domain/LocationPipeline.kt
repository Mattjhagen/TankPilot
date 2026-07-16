package com.tankpilot.trip.domain

import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.RoadContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlin.math.*

class LocationPipeline(
    private val validator: LocationSampleValidator = LocationSampleValidator(),
    private val scope: CoroutineScope
) {
    private val _validatedLocation = MutableStateFlow<LocationSample?>(null)
    val validatedLocation: StateFlow<LocationSample?> = _validatedLocation.asStateFlow()

    private val _roadContext = MutableStateFlow(RoadContext.UNKNOWN)
    val roadContext: StateFlow<RoadContext> = _roadContext.asStateFlow()

    private var cachedThoroughfare: String? = null
    private var cachedLatitude: Double? = null
    private var cachedLongitude: Double? = null
    private var lastLookupTimeMs: Long = 0L

    fun onRawLocationUpdate(
        sample: LocationSample,
        currentWallClockTime: kotlinx.datetime.Instant = Clock.System.now()
    ) {
        val validationResult = validator.validate(sample, currentWallClockTime)
        if (validationResult is LocationValidationResult.Accepted) {
            val acceptedSample = validationResult.sample
            
            val resolvedRoadContext = if (acceptedSample.roadContext != RoadContext.UNKNOWN) {
                acceptedSample.roadContext
            } else {
                _roadContext.value
            }

            val finalSample = acceptedSample.copy(roadContext = resolvedRoadContext)
            _validatedLocation.value = finalSample
            _roadContext.value = resolvedRoadContext
        }
    }

    fun updateRoadContextFromThoroughfare(
        latitude: Double,
        longitude: Double,
        thoroughfare: String?,
        timestampMs: Long = Clock.System.now().toEpochMilliseconds()
    ) {
        if (thoroughfare.isNullOrBlank()) return

        val timeSinceLast = timestampMs - lastLookupTimeMs
        val distanceMoved = calculateDistanceMeters(
            cachedLatitude ?: 0.0, cachedLongitude ?: 0.0,
            latitude, longitude
        )

        if (timeSinceLast < 10000L && distanceMoved < 500.0 && cachedThoroughfare == thoroughfare) {
            return
        }

        cachedThoroughfare = thoroughfare
        cachedLatitude = latitude
        cachedLongitude = longitude
        lastLookupTimeMs = timestampMs

        val highwayKeywords = listOf("I-", "Interstate", "Highway", "Hwy", "Route", "Freeway", "Fwy", "Freeway", "Parkway")
        val isHighway = highwayKeywords.any { thoroughfare.contains(it, ignoreCase = true) }

        _roadContext.value = if (isHighway) {
            RoadContext.HIGHWAY_LIKELY
        } else {
            RoadContext.URBAN_LIKELY
        }
    }

    fun reset() {
        validator.reset()
        _validatedLocation.value = null
        _roadContext.value = RoadContext.UNKNOWN
        cachedThoroughfare = null
        cachedLatitude = null
        cachedLongitude = null
        lastLookupTimeMs = 0L
    }

    private fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val lat1Rad = lat1 * PI / 180.0
        val lat2Rad = lat2 * PI / 180.0
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return 6371000.0 * c
    }
}
