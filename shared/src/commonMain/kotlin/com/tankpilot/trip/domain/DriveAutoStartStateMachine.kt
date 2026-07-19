package com.tankpilot.trip.domain

import com.tankpilot.core.AppLogger
import com.tankpilot.location.domain.LocationAuthorizationStatus
import com.tankpilot.location.domain.LocationSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

private const val TAG = "DriveAutoStart"

enum class AutoStartState {
    DISCONNECTED,
    ARMED,
    WAITING_FOR_RELIABLE_SPEED,
    STARTING,
    ACTIVE,
    STOPPING
}

class DriveAutoStartStateMachine(
    private val config: DriveAutoStartConfig = DriveAutoStartConfig(),
    private val clock: Clock = Clock.System,
    private val isSessionActive: () -> Boolean,
    private val startSession: () -> Boolean
) {
    private val _state = MutableStateFlow(AutoStartState.DISCONNECTED)
    val state: StateFlow<AutoStartState> = _state.asStateFlow()
    
    private val _authorization = MutableStateFlow(LocationAuthorizationStatus.UNKNOWN)
    val authorization: StateFlow<LocationAuthorizationStatus> = _authorization.asStateFlow()

    private val mutex = Mutex()
    private var aboveThresholdSinceMs: Long? = null
    private var lastValidSampleMs: Long? = null
    
    private val _activeContexts = MutableStateFlow<Set<VehicleContext>>(emptySet())
    val activeContexts: StateFlow<Set<VehicleContext>> = _activeContexts.asStateFlow()

    suspend fun enterDriveDetectionMode(context: VehicleContext) = mutex.withLock {
        AppLogger.d(TAG, "enterDriveDetectionMode: $context")
        val current = _activeContexts.value.toMutableSet()
        current.add(context)
        _activeContexts.value = current
        
        if (isSessionActive()) {
            _state.value = AutoStartState.ACTIVE
            return@withLock
        }
        if (_state.value == AutoStartState.DISCONNECTED) {
            _state.value = AutoStartState.ARMED
            aboveThresholdSinceMs = null
        }
    }

    suspend fun exitDriveDetectionMode(context: VehicleContext) = mutex.withLock {
        AppLogger.d(TAG, "exitDriveDetectionMode: $context")
        val current = _activeContexts.value.toMutableSet()
        current.remove(context)
        _activeContexts.value = current
        
        if (_activeContexts.value.isEmpty() && _state.value != AutoStartState.ACTIVE) {
            _state.value = AutoStartState.DISCONNECTED
            aboveThresholdSinceMs = null
        }
    }

    suspend fun onLocationAuthorizationChanged(status: LocationAuthorizationStatus) = mutex.withLock {
        _authorization.value = status
    }

    suspend fun onLocationSample(sample: LocationSample) = mutex.withLock {
        if (_state.value != AutoStartState.ARMED && _state.value != AutoStartState.WAITING_FOR_RELIABLE_SPEED) {
            return@withLock
        }
        
        if (_authorization.value != LocationAuthorizationStatus.AUTHORIZED && _authorization.value != LocationAuthorizationStatus.UNKNOWN) {
            return@withLock
        }

        val now = clock.now().toEpochMilliseconds()
        
        // Validation
        if (sample.speedKmh == null || sample.speedKmh < 0) return@withLock
        val sampleAgeMs = now - sample.timestamp.toEpochMilliseconds()
        if (sampleAgeMs > config.maxSampleAgeSeconds * 1000) return@withLock
        
        if (lastValidSampleMs != null && sample.timestamp.toEpochMilliseconds() < lastValidSampleMs!!) {
            return@withLock // out-of-order
        }
        
        val hAcc = sample.horizontalAccuracyMeters
        if (hAcc != null && hAcc > config.maxHorizontalAccuracyMeters) return@withLock
        
        val sAcc = sample.speedAccuracyMps
        if (sAcc != null && sAcc > config.maxSpeedAccuracyMps) return@withLock
        // If speed accuracy is unavailable (null), we allow it as fallback.

        lastValidSampleMs = sample.timestamp.toEpochMilliseconds()
        val speedMph = sample.speedKmh / 1.609344

        // Hysteresis behavior
        if (speedMph > config.startThresholdMph) {
            if (_state.value == AutoStartState.ARMED) {
                _state.value = AutoStartState.WAITING_FOR_RELIABLE_SPEED
            }
            if (aboveThresholdSinceMs == null) {
                aboveThresholdSinceMs = sample.timestamp.toEpochMilliseconds()
            } else {
                val sustainedDurationMs = sample.timestamp.toEpochMilliseconds() - aboveThresholdSinceMs!!
                if (sustainedDurationMs >= config.confirmationDurationSeconds * 1000) {
                    startTripAtomically()
                }
            }
        } else {
            // Hysteresis: Any valid sample <= 5.0 mph IMMEDIATELY clears the confirmation timer. 
            // It completely invalidates the continuous timing window so accumulation cannot cross samples below the threshold.
            aboveThresholdSinceMs = null
            
            // If the speed drops below 4.0 mph, we additionally transition back to ARMED.
            // If the speed is between 4.0 and 5.0 mph, the state remains WAITING_FOR_RELIABLE_SPEED, 
            // but because aboveThresholdSinceMs is cleared, it must cross > 5.0 mph again to begin 
            // a fresh 5-second continuous confirmation.
            if (speedMph < config.resetThresholdMph) {
                _state.value = AutoStartState.ARMED
            }
        }
    }

    private fun startTripAtomically() {
        _state.value = AutoStartState.STARTING
        aboveThresholdSinceMs = null
        
        val success = startSession()
        if (success) {
            AppLogger.d(TAG, "Auto-start succeeded")
            _state.value = AutoStartState.ACTIVE
        } else {
            AppLogger.w(TAG, "Auto-start failed")
            _state.value = AutoStartState.ARMED
        }
    }
}
