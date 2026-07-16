package com.tankpilot.trip.domain

import kotlinx.serialization.Serializable

@Serializable
data class ActiveSession(
    val tripId: String,
    val vehicleId: String,
    val startTimestamp: Long,
    val accumulatedDistance: Double,
    val elapsedTimeSeconds: Long,
    val idleTimeSeconds: Long,
    val activeFuelBurn: Double,
    val lastActivityTimestamp: Long
)

interface ActiveSessionRepository {
    suspend fun saveSession(session: ActiveSession)
    suspend fun getSession(vehicleId: String): ActiveSession?
    suspend fun deleteSession(vehicleId: String)
}
