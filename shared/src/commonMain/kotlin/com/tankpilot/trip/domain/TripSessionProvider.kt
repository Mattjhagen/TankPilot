package com.tankpilot.trip.domain

import com.tankpilot.core.Miles

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlin.time.Duration

enum class TripSessionState {
    INACTIVE,
    ACTIVE,
    PAUSED,
    ENDED
}

enum class TripStartSource {
    MANUAL,
    AUTO_TELEMETRY,
    AUTO_GPS
}

enum class TripEndReason {
    MANUAL,
    AUTO_TIMEOUT,
    DISCONNECT
}

interface TripSessionProvider {
    val sessionState: StateFlow<TripSessionState>
    val elapsedTime: StateFlow<Duration>
    val distanceDriven: StateFlow<Miles>
    val averageSpeed: StateFlow<Double?>
    val startedAt: StateFlow<Instant?>
    
    suspend fun startTrip(source: TripStartSource)
    suspend fun endTrip(reason: TripEndReason)
}
