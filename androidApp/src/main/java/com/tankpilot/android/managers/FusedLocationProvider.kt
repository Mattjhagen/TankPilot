package com.tankpilot.android.managers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.tankpilot.location.domain.LocationProvider
import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.LocationSampleSource
import com.tankpilot.location.domain.TrackingUnavailableReason
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant

class FusedLocationProvider(
    private val context: Context
) : LocationProvider {

    private val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    
    private val _locationFlow = MutableStateFlow<LocationSample?>(null)
    override val locationFlow: StateFlow<LocationSample?> = _locationFlow.asStateFlow()

    private val _statusFlow = MutableStateFlow<TrackingUnavailableReason?>(null)
    override val statusFlow: StateFlow<TrackingUnavailableReason?> = _statusFlow.asStateFlow()

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val lastLoc = result.lastLocation ?: return
            val sample = mapToSample(lastLoc)
            _locationFlow.value = sample
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            if (!availability.isLocationAvailable) {
                _statusFlow.value = TrackingUnavailableReason.LOCATION_SERVICES_DISABLED
            } else {
                _statusFlow.value = null
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startTracking() {
        Log.d("FusedLocationProvider", "Starting Fused location updates")
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L)
            .build()
            
        try {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
            _statusFlow.value = null
        } catch (e: SecurityException) {
            _statusFlow.value = TrackingUnavailableReason.LOCATION_PERMISSION_DENIED
        } catch (e: Exception) {
            _statusFlow.value = TrackingUnavailableReason.UNKNOWN
        }
    }

    override fun stopTracking() {
        Log.d("FusedLocationProvider", "Stopping Fused location updates")
        client.removeLocationUpdates(callback)
        _locationFlow.value = null
    }

    private fun mapToSample(location: Location): LocationSample {
        return LocationSample(
            timestamp = Instant.fromEpochMilliseconds(location.time),
            latitude = location.latitude,
            longitude = location.longitude,
            speedKmh = if (location.hasSpeed()) location.speed * 3.6 else null,
            speedAccuracyMps = if (location.hasSpeedAccuracy()) location.speedAccuracyMetersPerSecond.toDouble() else null,
            horizontalAccuracyMeters = if (location.hasAccuracy()) location.accuracy.toDouble() else null,
            bearingDegrees = if (location.hasBearing()) location.bearing.toDouble() else null,
            source = LocationSampleSource.FUSED
        )
    }
}
