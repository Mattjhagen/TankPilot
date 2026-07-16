package com.tankpilot.android.managers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.tankpilot.location.domain.LocationProvider
import com.tankpilot.location.domain.LocationSample
import com.tankpilot.location.domain.LocationSampleSource
import com.tankpilot.location.domain.TrackingUnavailableReason
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant

class FrameworkLocationProvider(
    private val context: Context
) : LocationProvider {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    private val _locationFlow = MutableStateFlow<LocationSample?>(null)
    override val locationFlow: StateFlow<LocationSample?> = _locationFlow.asStateFlow()

    private val _statusFlow = MutableStateFlow<TrackingUnavailableReason?>(null)
    override val statusFlow: StateFlow<TrackingUnavailableReason?> = _statusFlow.asStateFlow()

    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val sample = mapToSample(location)
            _locationFlow.value = sample
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {
            if (provider == LocationManager.GPS_PROVIDER) {
                _statusFlow.value = null
            }
        }

        override fun onProviderDisabled(provider: String) {
            if (provider == LocationManager.GPS_PROVIDER) {
                _statusFlow.value = TrackingUnavailableReason.LOCATION_SERVICES_DISABLED
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startTracking() {
        Log.d("FrameworkLocationProvider", "Starting Framework location updates")
        
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            _statusFlow.value = TrackingUnavailableReason.LOCATION_SERVICES_DISABLED
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                0.0f,
                listener,
                Looper.getMainLooper()
            )
            _statusFlow.value = null
        } catch (e: SecurityException) {
            _statusFlow.value = TrackingUnavailableReason.LOCATION_PERMISSION_DENIED
        } catch (e: Exception) {
            _statusFlow.value = TrackingUnavailableReason.UNKNOWN
        }
    }

    override fun stopTracking() {
        Log.d("FrameworkLocationProvider", "Stopping Framework location updates")
        locationManager.removeUpdates(listener)
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
            source = LocationSampleSource.GPS
        )
    }
}
