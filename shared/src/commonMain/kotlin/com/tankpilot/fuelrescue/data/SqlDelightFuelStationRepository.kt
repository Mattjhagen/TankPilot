package com.tankpilot.fuelrescue.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.tankpilot.db.TankPilotDb
import com.tankpilot.core.FuelType
import com.tankpilot.core.CurrencyMicros
import com.tankpilot.core.Money
import com.tankpilot.core.FuelPriceUnit
import com.tankpilot.core.FuelPricePerUnit
import com.tankpilot.core.StationId
import com.tankpilot.core.StationProvider
import com.tankpilot.fuelrescue.domain.FuelStation
import com.tankpilot.fuelrescue.domain.FuelStationProvider
import com.tankpilot.fuelrescue.domain.FuelStationRepository
import com.tankpilot.fuelrescue.domain.PriceFreshness
import com.tankpilot.fuelrescue.domain.StationFuelPrice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SqlDelightFuelStationRepository(
    private val db: TankPilotDb,
    private val provider: FuelStationProvider,
    private val dispatcher: CoroutineDispatcher,
    private val cacheDurationMs: Long = 15 * 60 * 1000L // 15 mins
) : FuelStationRepository {

    private val queries = db.tankPilotDbQueries

    override fun getCachedStations(): Flow<List<FuelStation>> {
        return queries.getCachedStations()
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { station ->
                    val prices = queries.getCachedPricesForStation(station.provider, station.providerId)
                        .executeAsList()
                        .map { it.toDomain() }
                    station.toDomain(prices)
                }
            }
    }

    override suspend fun refreshStations(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double,
        fuelType: FuelType,
        forceRefresh: Boolean
    ): List<FuelStation> = withContext(dispatcher) {
        val now = Clock.System.now().toEpochMilliseconds()
        val latCell = (latitude * 10).toInt()
        val lngCell = (longitude * 10).toInt()
        val cellId = "${latCell}_${lngCell}_${radiusMiles}_${fuelType.name}"

        if (!forceRefresh) {
            val cell = queries.getCachedQueryCell(cellId).executeAsOneOrNull()
            if (cell != null && cell.expiresAt > now) {
                val cached = queries.getCachedStationsForQuery(cellId).executeAsList()
                if (cached.isNotEmpty()) {
                    return@withContext cached.map { station ->
                        val prices = queries.getCachedPricesForStation(station.provider, station.providerId)
                            .executeAsList()
                            .map { it.toDomain() }
                        station.toDomain(prices)
                    }
                }
            }
        }

        // Fetch from provider
        try {
            val newStations = provider.getNearbyStations(latitude, longitude, radiusMiles, fuelType)
            
            db.transaction {
                queries.clearCache()
                queries.clearQueryCells()
                
                queries.insertCachedQueryCell(
                    id = cellId,
                    provider = StationProvider.GOOGLE_PLACES.name,
                    latitudeCell = latCell.toLong(),
                    longitudeCell = lngCell.toLong(),
                    radiusMiles = radiusMiles,
                    fuelType = fuelType.name,
                    fetchedAt = now,
                    expiresAt = now + cacheDurationMs
                )

                for (station in newStations) {
                    queries.insertCachedStation(
                        provider = station.id.provider.name,
                        providerId = station.id.providerId,
                        name = station.name,
                        brand = station.brand,
                        latitude = station.latitude,
                        longitude = station.longitude,
                        address = station.address,
                        fetchedAt = now
                    )

                    queries.insertCachedQueryStation(
                        queryCellId = cellId,
                        stationProvider = station.id.provider.name,
                        stationProviderId = station.id.providerId
                    )
                    
                    for (price in station.fuelPrices) {
                        queries.insertCachedPrice(
                            stationProvider = station.id.provider.name,
                            stationProviderId = station.id.providerId,
                            fuelType = price.fuelType.name,
                            fuelGradeKey = price.fuelGradeKey,
                            displayFuelGrade = price.displayFuelGrade,
                            priceAmountMicros = price.price.money.amountMicros.value,
                            currencyCode = price.price.money.currencyCode,
                            priceUnit = price.price.unit.name,
                            providerUpdatedAt = price.updatedAt,
                            fetchedAt = now
                        )
                    }
                }
            }
            newStations
        } catch (e: Exception) {
            // Fallback to cache on error
            val cached = queries.getCachedStations().executeAsList()
            if (cached.isNotEmpty()) {
                cached.map { station ->
                    val prices = queries.getCachedPricesForStation(station.provider, station.providerId)
                        .executeAsList()
                        .map { it.toDomain().copy(freshness = PriceFreshness.STALE) }
                    station.toDomain(prices)
                }
            } else {
                throw e
            }
        }
    }

    override suspend fun clearCache() = withContext(dispatcher) {
        queries.clearCache()
        queries.clearQueryCells()
    }
}

private fun com.tankpilot.db.CachedFuelStation.toDomain(
    prices: List<StationFuelPrice>
): FuelStation {
    val prov = try {
        StationProvider.valueOf(provider)
    } catch (e: Exception) {
        StationProvider.UNKNOWN
    }
    return FuelStation(
        id = StationId(prov, providerId),
        name = name,
        brand = brand,
        latitude = latitude,
        longitude = longitude,
        address = address,
        distanceMiles = 0.0,
        routeDistanceMiles = null,
        estimatedDriveMinutes = null,
        isOpen = null,
        navigationDestination = "maps://?q=$latitude,$longitude",
        fuelPrices = prices,
        lastFetchedAt = fetchedAt
    )
}

private fun com.tankpilot.db.CachedFuelPrice.toDomain(): StationFuelPrice {
    val rawFreshness = fetchedAt
    val ageMs = Clock.System.now().toEpochMilliseconds() - rawFreshness
    val freshness = when {
        ageMs <= 6 * 60 * 60 * 1000L -> PriceFreshness.RECENT
        ageMs <= 24 * 60 * 60 * 1000L -> PriceFreshness.AGING
        else -> PriceFreshness.STALE
    }

    return StationFuelPrice(
        fuelType = FuelType.valueOf(fuelType),
        fuelGradeKey = fuelGradeKey,
        displayFuelGrade = displayFuelGrade,
        price = FuelPricePerUnit(
            Money(CurrencyMicros(priceAmountMicros), currencyCode),
            FuelPriceUnit.valueOf(priceUnit)
        ),
        updatedAt = providerUpdatedAt,
        freshness = freshness,
        source = "Google Places"
    )
}
