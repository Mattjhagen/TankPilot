package com.tankpilot.android.auto.model

import com.tankpilot.core.GeoCoordinate

/**
 * Debug-only fixed preview coordinate (matches MainActivity's existing phone-side mock
 * location) so Fuel Rescue can be exercised in the Desktop Head Unit without real GPS.
 * Never compiled into release — see ReleaseCarLocationSource.
 */
class DebugCarLocationSource : CarLocationSource {
    override fun currentLocationOrNull(): GeoCoordinate = GeoCoordinate(37.7749, -122.4194)
}
