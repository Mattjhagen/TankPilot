package com.tankpilot.fillup.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.tankpilot.db.TankPilotDb
import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.fillup.domain.FillUpRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightFillUpRepository(
    private val db: TankPilotDb,
    private val dispatcher: CoroutineDispatcher
) : FillUpRepository {
    
    private val queries = db.tankPilotDbQueries

    override fun getFillUps(vehicleId: String): Flow<List<FillUp>> {
        return queries.getFillUpsForVehicle(vehicleId)
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override fun getRecentFillUps(vehicleId: String, limit: Long): Flow<List<FillUp>> {
        return queries.getRecentFillUpsForVehicle(vehicleId, limit)
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun saveFillUp(fillUp: FillUp) = withContext(dispatcher) {
        queries.insertFillUp(
            id = fillUp.id,
            vehicleId = fillUp.vehicleId,
            timestamp = fillUp.timestamp,
            gallonsAdded = fillUp.gallonsAdded,
            price = fillUp.price,
            odometer = fillUp.odometer,
            isFull = if (fillUp.isFull) 1L else 0L
        )
    }

    override suspend fun deleteFillUp(id: String) = withContext(dispatcher) {
        queries.deleteFillUp(id)
    }
}

private fun com.tankpilot.db.FillUp.toDomain(): FillUp {
    return FillUp(
        id = id,
        vehicleId = vehicleId,
        timestamp = timestamp,
        gallonsAdded = gallonsAdded,
        price = price,
        odometer = odometer,
        isFull = isFull == 1L
    )
}
