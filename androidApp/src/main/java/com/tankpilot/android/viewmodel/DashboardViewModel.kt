package com.tankpilot.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import com.tankpilot.telemetry.domain.TelemetryData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val telemetryProvider: VehicleTelemetryProvider
) : ViewModel() {

    val telemetryData: StateFlow<TelemetryData> = telemetryProvider.telemetryFlow

    // Simulated Trip State
    private val _tripDistanceMiles = MutableStateFlow(0.0)
    val tripDistanceMiles = _tripDistanceMiles.asStateFlow()

    private val _tripDurationSeconds = MutableStateFlow(0L)
    val tripDurationSeconds = _tripDurationSeconds.asStateFlow()

    private val _compassHeading = MutableStateFlow(340) // NW
    val compassHeading = _compassHeading.asStateFlow()

    private var tripStartTimeMs = 0L
    private var isSimulating = false

    fun startTrip() {
        if (isSimulating) return
        isSimulating = true
        tripStartTimeMs = Clock.System.now().toEpochMilliseconds()

        viewModelScope.launch {
            telemetryProvider.connect()
            
            while(isSimulating) {
                delay(1000)
                _tripDurationSeconds.value = (Clock.System.now().toEpochMilliseconds() - tripStartTimeMs) / 1000
                
                // slow heading change
                _compassHeading.value = (_compassHeading.value + 1) % 360
                
                // mock distance increment
                val speedKmh = telemetryData.value.speedKmh ?: 0.0
                if (speedKmh > 0) {
                    val milesPerSec = (speedKmh * 0.621371) / 3600.0
                    _tripDistanceMiles.value += milesPerSec
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        isSimulating = false
        viewModelScope.launch {
            telemetryProvider.disconnect()
        }
    }
}
