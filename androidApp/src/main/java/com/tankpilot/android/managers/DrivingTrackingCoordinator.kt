package com.tankpilot.android.managers

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
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

private const val TAG = "TankPilotDrive"

/** Pure, Context-free provider selection so the decision itself is unit-testable. */
fun selectLocationProvider(
    gmsAvailable: Boolean,
    fusedProvider: () -> LocationProvider,
    frameworkProvider: () -> LocationProvider
): LocationProvider = if (gmsAvailable) fusedProvider() else frameworkProvider()

/**
 * ForegroundServiceStartNotAllowedException only exists on API 31+ — referencing it in
 * an `is` check on a minSdk-26 app needs an explicit version guard or lint's NewApi check
 * correctly flags it as potentially unverifiable on older devices. Pulled into its own
 * @ChecksSdkIntAtLeast-annotated function (the androidx-recommended pattern) so lint
 * recognizes the guard AND the gating decision is unit-testable with an explicit sdkInt,
 * without needing Robolectric or a real device at a specific API level.
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun supportsForegroundServiceStartNotAllowedException(sdkInt: Int): Boolean =
    sdkInt >= Build.VERSION_CODES.S

class DrivingTrackingCoordinator(
    private val context: Context,
    private val drivingSessionCoordinator: DrivingSessionCoordinator,
    private val scope: CoroutineScope,
    locationProviderFactory: () -> LocationProvider = {
        val gmsAvailable = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        selectLocationProvider(
            gmsAvailable = gmsAvailable,
            fusedProvider = {
                Log.d(TAG, "Selected location provider: Fused (GMS)")
                FusedLocationProvider(context)
            },
            frameworkProvider = {
                Log.d(TAG, "Selected location provider: Framework (LocationManager)")
                FrameworkLocationProvider(context)
            }
        )
    }
) {
    private val locationProvider: LocationProvider = locationProviderFactory()

    /** For the debug diagnostics panel — which concrete provider Start Drive will use. */
    val selectedProviderLabel: String = when (locationProvider) {
        is FusedLocationProvider -> "Fused"
        is FrameworkLocationProvider -> "Framework"
        else -> locationProvider::class.simpleName ?: "Unknown"
    }

    private val _trackingStatus = MutableStateFlow<TrackingUnavailableReason?>(null)
    val trackingStatus: StateFlow<TrackingUnavailableReason?> = _trackingStatus.asStateFlow()

    /** True once startTracking() has succeeded and stopTracking() hasn't run since. Guards
     * against duplicate location-provider registrations / foreground service (re)starts
     * from a double tap on Start Drive, and lets Stop Drive be called repeatedly safely. */
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    init {
        // Forward location updates to the shared coordinator
        locationProvider.locationFlow
            .onEach { sample ->
                if (sample != null) {
                    drivingSessionCoordinator.onRawLocationUpdate(sample)
                }
            }
            .launchIn(scope)

        // Surface provider-detected issues (e.g. location services disabled) on the canonical status
        locationProvider.statusFlow
            .onEach { reason -> _trackingStatus.value = reason }
            .launchIn(scope)
    }

    fun startTracking() {
        Log.d(TAG, "Start Drive requested")
        if (_isTracking.value) {
            Log.d(TAG, "Start Drive ignored — already tracking")
            return
        }
        try {
            locationProvider.startTracking()
            // Start foreground service to keep background tracking alive
            val intent = Intent(context, DrivingNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            Log.d(TAG, "Foreground service start requested")
            _isTracking.value = true
            _trackingStatus.value = null
        } catch (e: SecurityException) {
            Log.w(TAG, "Start Drive failed: location permission denied")
            _trackingStatus.value = TrackingUnavailableReason.LOCATION_PERMISSION_DENIED
        } catch (e: Exception) {
            if (supportsForegroundServiceStartNotAllowedException(Build.VERSION.SDK_INT) &&
                e is android.app.ForegroundServiceStartNotAllowedException
            ) {
                Log.w(TAG, "Start Drive failed: foreground service start not allowed from background")
                _trackingStatus.value = TrackingUnavailableReason.FOREGROUND_START_NOT_ALLOWED
            } else {
                Log.w(TAG, "Start Drive failed: ${e::class.simpleName}")
                _trackingStatus.value = TrackingUnavailableReason.UNKNOWN
            }
        }
    }

    /** Called when the user declines the location permission request, before startTracking() is ever attempted. */
    fun onPermissionDenied() {
        Log.d(TAG, "Location permission denied by user")
        _trackingStatus.value = TrackingUnavailableReason.LOCATION_PERMISSION_DENIED
    }

    fun stopTracking() {
        if (!_isTracking.value) {
            Log.d(TAG, "Stop Drive requested — already stopped, no-op")
            return
        }
        Log.d(TAG, "Stop Drive requested")
        locationProvider.stopTracking()
        // Request canonical trip completion: if a trip is active, this transitions the
        // shared state machine to COMPLETING, which DrivingSessionCoordinator's own
        // listener persists exactly once and clears the active session for.
        drivingSessionCoordinator.endTripManually()
        val intent = Intent(context, DrivingNotificationService::class.java)
        context.stopService(intent)
        Log.d(TAG, "Foreground service stop requested")
        _isTracking.value = false
        _trackingStatus.value = null
    }
}
