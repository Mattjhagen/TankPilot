package com.tankpilot.testsupport.location

import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.LocationSampleSource
import com.tankpilot.location.domain.RoadContext
import com.tankpilot.trip.domain.DrivingSessionCoordinator
import kotlinx.datetime.Instant

data class ReplayCoordinate(
    val timestampMs: Long,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Double?,
    val roadContext: RoadContext = RoadContext.UNKNOWN
)

class LocationReplayHarness(
    private val coordinator: DrivingSessionCoordinator
) {
    fun replay(samples: List<ReplayCoordinate>) {
        samples.forEach { sample ->
            val timestamp = Instant.fromEpochMilliseconds(sample.timestampMs)
            val locationSample = LocationSample(
                timestamp = timestamp,
                latitude = sample.latitude,
                longitude = sample.longitude,
                speedKmh = sample.speedKmh,
                speedAccuracyMps = 0.5,
                horizontalAccuracyMeters = 5.0,
                bearingDegrees = 0.0,
                roadContext = sample.roadContext,
                source = LocationSampleSource.GPS
            )
            coordinator.onRawLocationUpdate(locationSample, timestamp)
        }
    }
}
