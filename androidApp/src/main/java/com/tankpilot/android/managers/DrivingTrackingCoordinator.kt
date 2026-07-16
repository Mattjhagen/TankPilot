package com.tankpilot.android.managers

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.tankpilot.android.services.DrivingNotificationService
import com.tankpilot.location.domain.LocationProvider
import com.tankpilot.location.domain.TrackingUnavailableReason
import com.tankpilot.trip.domain.DrivingSessionCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DrivingTrackingCoordinator(
    private val context: Context,
    private val drivingSessionCoordinator: DrivingSessionCoordinator,
    private val scope: CoroutineScope
) {
    private val locationProvider: LocationProvider

    init {
        val gmsAvailable = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        
        locationProvider = if (gmsAvailable) {
            Log.d("DrivingTrackingCoordinator", "Using FusedLocationProvider (GMS)")
            FusedLocationProvider(context)
        } else {
            Log.d("DrivingTrackingCoordinator", "Using FrameworkLocationProvider (LocationManager)")
            FrameworkLocationProvider(context)
        }

        // Forward location updates to the shared coordinator
        locationProvider.locationFlow
            .onEach { sample ->
                if (sample != null) {
                    drivingSessionCoordinator.onRawLocationUpdate(sample)
                }
            }
            .launchIn(scope)
    }

    private val _trackingStatus = MutableStateFlow<TrackingUnavailableReason?>(null)
    val trackingStatus: StateFlow<TrackingUnavailableReason?> = _trackingStatus.asStateFlow()

    fun startTracking() {
        try {
            locationProvider.startTracking()
            // Start foreground service to keep background tracking alive
            val intent = Intent(context, DrivingNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            _trackingStatus.value = null
        } catch (e: SecurityException) {
            _trackingStatus.value = TrackingUnavailableReason.LOCATION_PERMISSION_DENIED
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e is android.app.ForegroundServiceStartNotAllowedException) {
                _trackingStatus.value = TrackingUnavailableReason.FOREGROUND_START_NOT_ALLOWED
            } else {
                _trackingStatus.value = TrackingUnavailableReason.UNKNOWN
            }
        }
    }

    fun stopTracking() {
        locationProvider.stopTracking()
        val intent = Intent(context, DrivingNotificationService::class.java)
        context.stopService(intent)
        _trackingStatus.value = null
    }
}
