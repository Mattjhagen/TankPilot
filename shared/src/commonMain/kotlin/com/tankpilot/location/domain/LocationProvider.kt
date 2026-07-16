package com.tankpilot.location.domain

import kotlinx.coroutines.flow.StateFlow

interface LocationProvider {
    val locationFlow: StateFlow<LocationSample?>
    val statusFlow: StateFlow<TrackingUnavailableReason?>
    
    fun startTracking()
    fun stopTracking()
}
