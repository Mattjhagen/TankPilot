package com.tankpilot.trip.data

import com.tankpilot.db.TankPilotDb
import com.tankpilot.trip.domain.ActiveSession
import com.tankpilot.trip.domain.ActiveSessionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class SqlDelightActiveSessionRepository(
    private val db: TankPilotDb,
    private val dispatcher: CoroutineDispatcher
) : ActiveSessionRepository {

    private val queries = db.tankPilotDbQueries

    override suspend fun saveSession(session: ActiveSession) = withContext(dispatcher) {
        val json = Json.encodeToString(session)
        queries.insertSetting("active_session_${session.vehicleId}", json)
    }

    override suspend fun getSession(vehicleId: String): ActiveSession? = withContext(dispatcher) {
        val json = queries.getSetting("active_session_$vehicleId").executeAsOneOrNull()
        if (json != null) {
            try {
                Json.decodeFromString<ActiveSession>(json)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    override suspend fun deleteSession(vehicleId: String) = withContext(dispatcher) {
        queries.deleteSetting("active_session_$vehicleId")
    }
}
