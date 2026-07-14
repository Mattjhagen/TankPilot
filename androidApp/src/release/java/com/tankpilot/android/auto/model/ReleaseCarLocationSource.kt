package com.tankpilot.android.auto.model

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.Manifest
import com.tankpilot.core.GeoCoordinate

/**
 * Release builds never fabricate a location. Uses the platform LocationManager's last
 * known fix (no Play Services dependency) and returns null — never a stale/fake
 * coordinate — when permission isn't granted or no fix is available yet.
 */
class ReleaseCarLocationSource(private val context: Context) : CarLocationSource {

    override fun currentLocationOrNull(): GeoCoordinate? {
        val hasFinePermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        if (!hasFinePermission && !hasCoarsePermission) return null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        for (provider in providers) {
            val location = runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
            if (location != null && GeoCoordinate.isValid(location.latitude, location.longitude)) {
                return GeoCoordinate(location.latitude, location.longitude)
            }
        }
        return null
    }
}
