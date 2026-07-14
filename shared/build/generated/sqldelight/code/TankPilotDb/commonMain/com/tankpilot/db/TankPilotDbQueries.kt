package com.tankpilot.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Double
import kotlin.Long
import kotlin.String

public class TankPilotDbQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> getVehicles(mapper: (
    id: String,
    year: Long,
    make: String,
    model: String,
    trim: String?,
    color: String?,
    engine: String,
    engineDisplacementLiters: Double?,
    cylinderCount: Long?,
    tankCapacity: Double,
    factoryCityMpg: Double,
    factoryHwyMpg: Double,
    learnedMpg: Double,
    preferredFuelType: String,
    preferredFuelGrade: String?,
    unitSystem: String,
    reserveFuelGallons: Double,
    lowFuelThresholdPercent: Double,
  ) -> T): Query<T> = Query(79_413_650, arrayOf("Vehicle"), driver, "TankPilotDb.sq", "getVehicles",
      "SELECT Vehicle.id, Vehicle.year, Vehicle.make, Vehicle.model, Vehicle.trim, Vehicle.color, Vehicle.engine, Vehicle.engineDisplacementLiters, Vehicle.cylinderCount, Vehicle.tankCapacity, Vehicle.factoryCityMpg, Vehicle.factoryHwyMpg, Vehicle.learnedMpg, Vehicle.preferredFuelType, Vehicle.preferredFuelGrade, Vehicle.unitSystem, Vehicle.reserveFuelGallons, Vehicle.lowFuelThresholdPercent FROM Vehicle") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6)!!,
      cursor.getDouble(7),
      cursor.getLong(8),
      cursor.getDouble(9)!!,
      cursor.getDouble(10)!!,
      cursor.getDouble(11)!!,
      cursor.getDouble(12)!!,
      cursor.getString(13)!!,
      cursor.getString(14),
      cursor.getString(15)!!,
      cursor.getDouble(16)!!,
      cursor.getDouble(17)!!
    )
  }

  public fun getVehicles(): Query<Vehicle> = getVehicles { id, year, make, model, trim, color,
      engine, engineDisplacementLiters, cylinderCount, tankCapacity, factoryCityMpg, factoryHwyMpg,
      learnedMpg, preferredFuelType, preferredFuelGrade, unitSystem, reserveFuelGallons,
      lowFuelThresholdPercent ->
    Vehicle(
      id,
      year,
      make,
      model,
      trim,
      color,
      engine,
      engineDisplacementLiters,
      cylinderCount,
      tankCapacity,
      factoryCityMpg,
      factoryHwyMpg,
      learnedMpg,
      preferredFuelType,
      preferredFuelGrade,
      unitSystem,
      reserveFuelGallons,
      lowFuelThresholdPercent
    )
  }

  public fun <T : Any> getVehicleById(id: String, mapper: (
    id: String,
    year: Long,
    make: String,
    model: String,
    trim: String?,
    color: String?,
    engine: String,
    engineDisplacementLiters: Double?,
    cylinderCount: Long?,
    tankCapacity: Double,
    factoryCityMpg: Double,
    factoryHwyMpg: Double,
    learnedMpg: Double,
    preferredFuelType: String,
    preferredFuelGrade: String?,
    unitSystem: String,
    reserveFuelGallons: Double,
    lowFuelThresholdPercent: Double,
  ) -> T): Query<T> = GetVehicleByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6)!!,
      cursor.getDouble(7),
      cursor.getLong(8),
      cursor.getDouble(9)!!,
      cursor.getDouble(10)!!,
      cursor.getDouble(11)!!,
      cursor.getDouble(12)!!,
      cursor.getString(13)!!,
      cursor.getString(14),
      cursor.getString(15)!!,
      cursor.getDouble(16)!!,
      cursor.getDouble(17)!!
    )
  }

  public fun getVehicleById(id: String): Query<Vehicle> = getVehicleById(id) { id_, year, make,
      model, trim, color, engine, engineDisplacementLiters, cylinderCount, tankCapacity,
      factoryCityMpg, factoryHwyMpg, learnedMpg, preferredFuelType, preferredFuelGrade, unitSystem,
      reserveFuelGallons, lowFuelThresholdPercent ->
    Vehicle(
      id_,
      year,
      make,
      model,
      trim,
      color,
      engine,
      engineDisplacementLiters,
      cylinderCount,
      tankCapacity,
      factoryCityMpg,
      factoryHwyMpg,
      learnedMpg,
      preferredFuelType,
      preferredFuelGrade,
      unitSystem,
      reserveFuelGallons,
      lowFuelThresholdPercent
    )
  }

  public fun <T : Any> getTripsForVehicle(vehicleId: String, mapper: (
    id: String,
    vehicleId: String,
    timestamp: Long,
    distance: Double,
    duration: Long,
    idleTime: Long,
    averageSpeed: Double,
    drivingType: String,
    fuelBurned: Double,
  ) -> T): Query<T> = GetTripsForVehicleQuery(vehicleId) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!,
      cursor.getDouble(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!,
      cursor.getDouble(6)!!,
      cursor.getString(7)!!,
      cursor.getDouble(8)!!
    )
  }

  public fun getTripsForVehicle(vehicleId: String): Query<Trip> = getTripsForVehicle(vehicleId) {
      id, vehicleId_, timestamp, distance, duration, idleTime, averageSpeed, drivingType,
      fuelBurned ->
    Trip(
      id,
      vehicleId_,
      timestamp,
      distance,
      duration,
      idleTime,
      averageSpeed,
      drivingType,
      fuelBurned
    )
  }

  public fun <T : Any> getRecentTripsForVehicle(
    vehicleId: String,
    `value`: Long,
    mapper: (
      id: String,
      vehicleId: String,
      timestamp: Long,
      distance: Double,
      duration: Long,
      idleTime: Long,
      averageSpeed: Double,
      drivingType: String,
      fuelBurned: Double,
    ) -> T,
  ): Query<T> = GetRecentTripsForVehicleQuery(vehicleId, value) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!,
      cursor.getDouble(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!,
      cursor.getDouble(6)!!,
      cursor.getString(7)!!,
      cursor.getDouble(8)!!
    )
  }

  public fun getRecentTripsForVehicle(vehicleId: String, value_: Long): Query<Trip> =
      getRecentTripsForVehicle(vehicleId, value_) { id, vehicleId_, timestamp, distance, duration,
      idleTime, averageSpeed, drivingType, fuelBurned ->
    Trip(
      id,
      vehicleId_,
      timestamp,
      distance,
      duration,
      idleTime,
      averageSpeed,
      drivingType,
      fuelBurned
    )
  }

  public fun <T : Any> getFillUpsForVehicle(vehicleId: String, mapper: (
    id: String,
    vehicleId: String,
    timestamp: Long,
    gallonsAdded: Double,
    price: Double,
    odometer: Double?,
    isFull: Long,
  ) -> T): Query<T> = GetFillUpsForVehicleQuery(vehicleId) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!,
      cursor.getDouble(3)!!,
      cursor.getDouble(4)!!,
      cursor.getDouble(5),
      cursor.getLong(6)!!
    )
  }

  public fun getFillUpsForVehicle(vehicleId: String): Query<FillUp> =
      getFillUpsForVehicle(vehicleId) { id, vehicleId_, timestamp, gallonsAdded, price, odometer,
      isFull ->
    FillUp(
      id,
      vehicleId_,
      timestamp,
      gallonsAdded,
      price,
      odometer,
      isFull
    )
  }

  public fun <T : Any> getRecentFillUpsForVehicle(
    vehicleId: String,
    `value`: Long,
    mapper: (
      id: String,
      vehicleId: String,
      timestamp: Long,
      gallonsAdded: Double,
      price: Double,
      odometer: Double?,
      isFull: Long,
    ) -> T,
  ): Query<T> = GetRecentFillUpsForVehicleQuery(vehicleId, value) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!,
      cursor.getDouble(3)!!,
      cursor.getDouble(4)!!,
      cursor.getDouble(5),
      cursor.getLong(6)!!
    )
  }

  public fun getRecentFillUpsForVehicle(vehicleId: String, value_: Long): Query<FillUp> =
      getRecentFillUpsForVehicle(vehicleId, value_) { id, vehicleId_, timestamp, gallonsAdded,
      price, odometer, isFull ->
    FillUp(
      id,
      vehicleId_,
      timestamp,
      gallonsAdded,
      price,
      odometer,
      isFull
    )
  }

  public fun <T : Any> getCachedStations(mapper: (
    provider: String,
    providerId: String,
    name: String,
    brand: String?,
    latitude: Double,
    longitude: Double,
    address: String?,
    fetchedAt: Long,
  ) -> T): Query<T> = Query(-1_422_889_076, arrayOf("CachedFuelStation"), driver, "TankPilotDb.sq",
      "getCachedStations",
      "SELECT CachedFuelStation.provider, CachedFuelStation.providerId, CachedFuelStation.name, CachedFuelStation.brand, CachedFuelStation.latitude, CachedFuelStation.longitude, CachedFuelStation.address, CachedFuelStation.fetchedAt FROM CachedFuelStation") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3),
      cursor.getDouble(4)!!,
      cursor.getDouble(5)!!,
      cursor.getString(6),
      cursor.getLong(7)!!
    )
  }

  public fun getCachedStations(): Query<CachedFuelStation> = getCachedStations { provider,
      providerId, name, brand, latitude, longitude, address, fetchedAt ->
    CachedFuelStation(
      provider,
      providerId,
      name,
      brand,
      latitude,
      longitude,
      address,
      fetchedAt
    )
  }

  public fun <T : Any> getCachedPricesForStation(
    stationProvider: String,
    stationProviderId: String,
    mapper: (
      stationProvider: String,
      stationProviderId: String,
      fuelType: String,
      fuelGradeKey: String,
      displayFuelGrade: String?,
      priceAmountMicros: Long,
      currencyCode: String,
      priceUnit: String,
      providerUpdatedAt: Long?,
      fetchedAt: Long,
    ) -> T,
  ): Query<T> = GetCachedPricesForStationQuery(stationProvider, stationProviderId) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getLong(5)!!,
      cursor.getString(6)!!,
      cursor.getString(7)!!,
      cursor.getLong(8),
      cursor.getLong(9)!!
    )
  }

  public fun getCachedPricesForStation(stationProvider: String, stationProviderId: String):
      Query<CachedFuelPrice> = getCachedPricesForStation(stationProvider, stationProviderId) {
      stationProvider_, stationProviderId_, fuelType, fuelGradeKey, displayFuelGrade,
      priceAmountMicros, currencyCode, priceUnit, providerUpdatedAt, fetchedAt ->
    CachedFuelPrice(
      stationProvider_,
      stationProviderId_,
      fuelType,
      fuelGradeKey,
      displayFuelGrade,
      priceAmountMicros,
      currencyCode,
      priceUnit,
      providerUpdatedAt,
      fetchedAt
    )
  }

  public fun <T : Any> getCachedQueryCell(id: String, mapper: (
    id: String,
    provider: String,
    latitudeCell: Long,
    longitudeCell: Long,
    radiusMiles: Double,
    fuelType: String,
    fetchedAt: Long,
    expiresAt: Long,
  ) -> T): Query<T> = GetCachedQueryCellQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!,
      cursor.getLong(3)!!,
      cursor.getDouble(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!!
    )
  }

  public fun getCachedQueryCell(id: String): Query<CachedQueryCell> = getCachedQueryCell(id) { id_,
      provider, latitudeCell, longitudeCell, radiusMiles, fuelType, fetchedAt, expiresAt ->
    CachedQueryCell(
      id_,
      provider,
      latitudeCell,
      longitudeCell,
      radiusMiles,
      fuelType,
      fetchedAt,
      expiresAt
    )
  }

  public fun <T : Any> getCachedStationsForQuery(queryCellId: String, mapper: (
    provider: String,
    providerId: String,
    name: String,
    brand: String?,
    latitude: Double,
    longitude: Double,
    address: String?,
    fetchedAt: Long,
  ) -> T): Query<T> = GetCachedStationsForQueryQuery(queryCellId) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3),
      cursor.getDouble(4)!!,
      cursor.getDouble(5)!!,
      cursor.getString(6),
      cursor.getLong(7)!!
    )
  }

  public fun getCachedStationsForQuery(queryCellId: String): Query<CachedFuelStation> =
      getCachedStationsForQuery(queryCellId) { provider, providerId, name, brand, latitude,
      longitude, address, fetchedAt ->
    CachedFuelStation(
      provider,
      providerId,
      name,
      brand,
      latitude,
      longitude,
      address,
      fetchedAt
    )
  }

  public fun <T : Any> getCachedRoute(
    originCellId: String,
    stationProvider: String,
    stationProviderId: String,
    routeMode: String,
    mapper: (
      originCellId: String,
      stationProvider: String,
      stationProviderId: String,
      routeMode: String,
      distanceMiles: Double,
      durationSeconds: Long,
      fetchedAt: Long,
      expiresAt: Long,
    ) -> T,
  ): Query<T> = GetCachedRouteQuery(originCellId, stationProvider, stationProviderId, routeMode) {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getDouble(4)!!,
      cursor.getLong(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!!
    )
  }

  public fun getCachedRoute(
    originCellId: String,
    stationProvider: String,
    stationProviderId: String,
    routeMode: String,
  ): Query<CachedStationRoute> = getCachedRoute(originCellId, stationProvider, stationProviderId,
      routeMode) { originCellId_, stationProvider_, stationProviderId_, routeMode_, distanceMiles,
      durationSeconds, fetchedAt, expiresAt ->
    CachedStationRoute(
      originCellId_,
      stationProvider_,
      stationProviderId_,
      routeMode_,
      distanceMiles,
      durationSeconds,
      fetchedAt,
      expiresAt
    )
  }

  public fun insertVehicle(
    id: String,
    year: Long,
    make: String,
    model: String,
    trim: String?,
    color: String?,
    engine: String,
    engineDisplacementLiters: Double?,
    cylinderCount: Long?,
    tankCapacity: Double,
    factoryCityMpg: Double,
    factoryHwyMpg: Double,
    learnedMpg: Double,
    preferredFuelType: String,
    preferredFuelGrade: String?,
    unitSystem: String,
    reserveFuelGallons: Double,
    lowFuelThresholdPercent: Double,
  ) {
    driver.execute(514_047_560, """
        |INSERT OR REPLACE INTO Vehicle(
        |    id, year, make, model, trim, color, engine, engineDisplacementLiters, cylinderCount,
        |    tankCapacity, factoryCityMpg, factoryHwyMpg, learnedMpg,
        |    preferredFuelType, preferredFuelGrade, unitSystem, reserveFuelGallons, lowFuelThresholdPercent
        |)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 18) {
          bindString(0, id)
          bindLong(1, year)
          bindString(2, make)
          bindString(3, model)
          bindString(4, trim)
          bindString(5, color)
          bindString(6, engine)
          bindDouble(7, engineDisplacementLiters)
          bindLong(8, cylinderCount)
          bindDouble(9, tankCapacity)
          bindDouble(10, factoryCityMpg)
          bindDouble(11, factoryHwyMpg)
          bindDouble(12, learnedMpg)
          bindString(13, preferredFuelType)
          bindString(14, preferredFuelGrade)
          bindString(15, unitSystem)
          bindDouble(16, reserveFuelGallons)
          bindDouble(17, lowFuelThresholdPercent)
        }
    notifyQueries(514_047_560) { emit ->
      emit("Vehicle")
    }
  }

  public fun updateLearnedMpg(learnedMpg: Double, id: String) {
    driver.execute(-1_007_668_299, """UPDATE Vehicle SET learnedMpg = ? WHERE id = ?""", 2) {
          bindDouble(0, learnedMpg)
          bindString(1, id)
        }
    notifyQueries(-1_007_668_299) { emit ->
      emit("Vehicle")
    }
  }

  public fun deleteVehicle(id: String) {
    driver.execute(820_782_870, """DELETE FROM Vehicle WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(820_782_870) { emit ->
      emit("FillUp")
      emit("Trip")
      emit("Vehicle")
    }
  }

  public fun insertTrip(
    id: String,
    vehicleId: String,
    timestamp: Long,
    distance: Double,
    duration: Long,
    idleTime: Long,
    averageSpeed: Double,
    drivingType: String,
    fuelBurned: Double,
  ) {
    driver.execute(1_806_563_977, """
        |INSERT INTO Trip(id, vehicleId, timestamp, distance, duration, idleTime, averageSpeed, drivingType, fuelBurned)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 9) {
          bindString(0, id)
          bindString(1, vehicleId)
          bindLong(2, timestamp)
          bindDouble(3, distance)
          bindLong(4, duration)
          bindLong(5, idleTime)
          bindDouble(6, averageSpeed)
          bindString(7, drivingType)
          bindDouble(8, fuelBurned)
        }
    notifyQueries(1_806_563_977) { emit ->
      emit("Trip")
    }
  }

  public fun deleteTrip(id: String) {
    driver.execute(-1_689_979_781, """DELETE FROM Trip WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(-1_689_979_781) { emit ->
      emit("Trip")
    }
  }

  public fun insertFillUp(
    id: String,
    vehicleId: String,
    timestamp: Long,
    gallonsAdded: Double,
    price: Double,
    odometer: Double?,
    isFull: Long,
  ) {
    driver.execute(532_162_786, """
        |INSERT INTO FillUp(id, vehicleId, timestamp, gallonsAdded, price, odometer, isFull)
        |VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 7) {
          bindString(0, id)
          bindString(1, vehicleId)
          bindLong(2, timestamp)
          bindDouble(3, gallonsAdded)
          bindDouble(4, price)
          bindDouble(5, odometer)
          bindLong(6, isFull)
        }
    notifyQueries(532_162_786) { emit ->
      emit("FillUp")
    }
  }

  public fun deleteFillUp(id: String) {
    driver.execute(-981_963_180, """DELETE FROM FillUp WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(-981_963_180) { emit ->
      emit("FillUp")
    }
  }

  public fun insertCachedStation(
    provider: String,
    providerId: String,
    name: String,
    brand: String?,
    latitude: Double,
    longitude: Double,
    address: String?,
    fetchedAt: Long,
  ) {
    driver.execute(1_722_521_550, """
        |INSERT OR REPLACE INTO CachedFuelStation(provider, providerId, name, brand, latitude, longitude, address, fetchedAt)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 8) {
          bindString(0, provider)
          bindString(1, providerId)
          bindString(2, name)
          bindString(3, brand)
          bindDouble(4, latitude)
          bindDouble(5, longitude)
          bindString(6, address)
          bindLong(7, fetchedAt)
        }
    notifyQueries(1_722_521_550) { emit ->
      emit("CachedFuelStation")
    }
  }

  public fun insertCachedPrice(
    stationProvider: String,
    stationProviderId: String,
    fuelType: String,
    fuelGradeKey: String,
    displayFuelGrade: String?,
    priceAmountMicros: Long,
    currencyCode: String,
    priceUnit: String,
    providerUpdatedAt: Long?,
    fetchedAt: Long,
  ) {
    driver.execute(-1_100_470_685, """
        |INSERT OR REPLACE INTO CachedFuelPrice(stationProvider, stationProviderId, fuelType, fuelGradeKey, displayFuelGrade, priceAmountMicros, currencyCode, priceUnit, providerUpdatedAt, fetchedAt)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 10) {
          bindString(0, stationProvider)
          bindString(1, stationProviderId)
          bindString(2, fuelType)
          bindString(3, fuelGradeKey)
          bindString(4, displayFuelGrade)
          bindLong(5, priceAmountMicros)
          bindString(6, currencyCode)
          bindString(7, priceUnit)
          bindLong(8, providerUpdatedAt)
          bindLong(9, fetchedAt)
        }
    notifyQueries(-1_100_470_685) { emit ->
      emit("CachedFuelPrice")
    }
  }

  public fun clearCache() {
    driver.execute(81_151_296, """DELETE FROM CachedFuelStation""", 0)
    notifyQueries(81_151_296) { emit ->
      emit("CachedFuelPrice")
      emit("CachedFuelStation")
      emit("CachedQueryStation")
      emit("CachedStationRoute")
    }
  }

  public fun insertCachedQueryCell(
    id: String,
    provider: String,
    latitudeCell: Long,
    longitudeCell: Long,
    radiusMiles: Double,
    fuelType: String,
    fetchedAt: Long,
    expiresAt: Long,
  ) {
    driver.execute(2_054_802_020, """
        |INSERT OR REPLACE INTO CachedQueryCell(id, provider, latitudeCell, longitudeCell, radiusMiles, fuelType, fetchedAt, expiresAt)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 8) {
          bindString(0, id)
          bindString(1, provider)
          bindLong(2, latitudeCell)
          bindLong(3, longitudeCell)
          bindDouble(4, radiusMiles)
          bindString(5, fuelType)
          bindLong(6, fetchedAt)
          bindLong(7, expiresAt)
        }
    notifyQueries(2_054_802_020) { emit ->
      emit("CachedQueryCell")
    }
  }

  public fun deleteExpiredQueryCells(expiresAt: Long) {
    driver.execute(1_750_969_304, """DELETE FROM CachedQueryCell WHERE expiresAt < ?""", 1) {
          bindLong(0, expiresAt)
        }
    notifyQueries(1_750_969_304) { emit ->
      emit("CachedQueryCell")
      emit("CachedQueryStation")
    }
  }

  public fun clearQueryCells() {
    driver.execute(-1_264_506_773, """DELETE FROM CachedQueryCell""", 0)
    notifyQueries(-1_264_506_773) { emit ->
      emit("CachedQueryCell")
      emit("CachedQueryStation")
    }
  }

  public fun insertCachedQueryStation(
    queryCellId: String,
    stationProvider: String,
    stationProviderId: String,
  ) {
    driver.execute(172_886_258, """
        |INSERT OR REPLACE INTO CachedQueryStation(queryCellId, stationProvider, stationProviderId)
        |VALUES (?, ?, ?)
        """.trimMargin(), 3) {
          bindString(0, queryCellId)
          bindString(1, stationProvider)
          bindString(2, stationProviderId)
        }
    notifyQueries(172_886_258) { emit ->
      emit("CachedQueryStation")
    }
  }

  public fun insertCachedRoute(
    originCellId: String,
    stationProvider: String,
    stationProviderId: String,
    routeMode: String,
    distanceMiles: Double,
    durationSeconds: Long,
    fetchedAt: Long,
    expiresAt: Long,
  ) {
    driver.execute(-1_098_700_957, """
        |INSERT OR REPLACE INTO CachedStationRoute(originCellId, stationProvider, stationProviderId, routeMode, distanceMiles, durationSeconds, fetchedAt, expiresAt)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 8) {
          bindString(0, originCellId)
          bindString(1, stationProvider)
          bindString(2, stationProviderId)
          bindString(3, routeMode)
          bindDouble(4, distanceMiles)
          bindLong(5, durationSeconds)
          bindLong(6, fetchedAt)
          bindLong(7, expiresAt)
        }
    notifyQueries(-1_098_700_957) { emit ->
      emit("CachedStationRoute")
    }
  }

  public fun clearExpiredRoutes(expiresAt: Long) {
    driver.execute(591_360_653, """DELETE FROM CachedStationRoute WHERE expiresAt < ?""", 1) {
          bindLong(0, expiresAt)
        }
    notifyQueries(591_360_653) { emit ->
      emit("CachedStationRoute")
    }
  }

  private inner class GetVehicleByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Vehicle", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Vehicle", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-716_274_061,
        """SELECT Vehicle.id, Vehicle.year, Vehicle.make, Vehicle.model, Vehicle.trim, Vehicle.color, Vehicle.engine, Vehicle.engineDisplacementLiters, Vehicle.cylinderCount, Vehicle.tankCapacity, Vehicle.factoryCityMpg, Vehicle.factoryHwyMpg, Vehicle.learnedMpg, Vehicle.preferredFuelType, Vehicle.preferredFuelGrade, Vehicle.unitSystem, Vehicle.reserveFuelGallons, Vehicle.lowFuelThresholdPercent FROM Vehicle WHERE id = ?""",
        mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "TankPilotDb.sq:getVehicleById"
  }

  private inner class GetTripsForVehicleQuery<out T : Any>(
    public val vehicleId: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Trip", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Trip", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(316_365_958,
        """SELECT Trip.id, Trip.vehicleId, Trip.timestamp, Trip.distance, Trip.duration, Trip.idleTime, Trip.averageSpeed, Trip.drivingType, Trip.fuelBurned FROM Trip WHERE vehicleId = ? ORDER BY timestamp DESC""",
        mapper, 1) {
      bindString(0, vehicleId)
    }

    override fun toString(): String = "TankPilotDb.sq:getTripsForVehicle"
  }

  private inner class GetRecentTripsForVehicleQuery<out T : Any>(
    public val vehicleId: String,
    public val `value`: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Trip", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Trip", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(233_093_355,
        """SELECT Trip.id, Trip.vehicleId, Trip.timestamp, Trip.distance, Trip.duration, Trip.idleTime, Trip.averageSpeed, Trip.drivingType, Trip.fuelBurned FROM Trip WHERE vehicleId = ? ORDER BY timestamp DESC LIMIT ?""",
        mapper, 2) {
      bindString(0, vehicleId)
      bindLong(1, value)
    }

    override fun toString(): String = "TankPilotDb.sq:getRecentTripsForVehicle"
  }

  private inner class GetFillUpsForVehicleQuery<out T : Any>(
    public val vehicleId: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("FillUp", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("FillUp", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_589_386_675,
        """SELECT FillUp.id, FillUp.vehicleId, FillUp.timestamp, FillUp.gallonsAdded, FillUp.price, FillUp.odometer, FillUp.isFull FROM FillUp WHERE vehicleId = ? ORDER BY timestamp DESC""",
        mapper, 1) {
      bindString(0, vehicleId)
    }

    override fun toString(): String = "TankPilotDb.sq:getFillUpsForVehicle"
  }

  private inner class GetRecentFillUpsForVehicleQuery<out T : Any>(
    public val vehicleId: String,
    public val `value`: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("FillUp", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("FillUp", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-9_979_534,
        """SELECT FillUp.id, FillUp.vehicleId, FillUp.timestamp, FillUp.gallonsAdded, FillUp.price, FillUp.odometer, FillUp.isFull FROM FillUp WHERE vehicleId = ? ORDER BY timestamp DESC LIMIT ?""",
        mapper, 2) {
      bindString(0, vehicleId)
      bindLong(1, value)
    }

    override fun toString(): String = "TankPilotDb.sq:getRecentFillUpsForVehicle"
  }

  private inner class GetCachedPricesForStationQuery<out T : Any>(
    public val stationProvider: String,
    public val stationProviderId: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("CachedFuelPrice", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("CachedFuelPrice", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(154_662_114,
        """SELECT CachedFuelPrice.stationProvider, CachedFuelPrice.stationProviderId, CachedFuelPrice.fuelType, CachedFuelPrice.fuelGradeKey, CachedFuelPrice.displayFuelGrade, CachedFuelPrice.priceAmountMicros, CachedFuelPrice.currencyCode, CachedFuelPrice.priceUnit, CachedFuelPrice.providerUpdatedAt, CachedFuelPrice.fetchedAt FROM CachedFuelPrice WHERE stationProvider = ? AND stationProviderId = ?""",
        mapper, 2) {
      bindString(0, stationProvider)
      bindString(1, stationProviderId)
    }

    override fun toString(): String = "TankPilotDb.sq:getCachedPricesForStation"
  }

  private inner class GetCachedQueryCellQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("CachedQueryCell", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("CachedQueryCell", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-885_890_531,
        """SELECT CachedQueryCell.id, CachedQueryCell.provider, CachedQueryCell.latitudeCell, CachedQueryCell.longitudeCell, CachedQueryCell.radiusMiles, CachedQueryCell.fuelType, CachedQueryCell.fetchedAt, CachedQueryCell.expiresAt FROM CachedQueryCell WHERE id = ?""",
        mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "TankPilotDb.sq:getCachedQueryCell"
  }

  private inner class GetCachedStationsForQueryQuery<out T : Any>(
    public val queryCellId: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("CachedFuelStation", "CachedQueryStation", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("CachedFuelStation", "CachedQueryStation", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_532_541_227, """
    |SELECT s.provider, s.providerId, s.name, s.brand, s.latitude, s.longitude, s.address, s.fetchedAt FROM CachedFuelStation s
    |JOIN CachedQueryStation q ON s.provider = q.stationProvider AND s.providerId = q.stationProviderId
    |WHERE q.queryCellId = ?
    """.trimMargin(), mapper, 1) {
      bindString(0, queryCellId)
    }

    override fun toString(): String = "TankPilotDb.sq:getCachedStationsForQuery"
  }

  private inner class GetCachedRouteQuery<out T : Any>(
    public val originCellId: String,
    public val stationProvider: String,
    public val stationProviderId: String,
    public val routeMode: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("CachedStationRoute", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("CachedStationRoute", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(86_410_140, """
    |SELECT CachedStationRoute.originCellId, CachedStationRoute.stationProvider, CachedStationRoute.stationProviderId, CachedStationRoute.routeMode, CachedStationRoute.distanceMiles, CachedStationRoute.durationSeconds, CachedStationRoute.fetchedAt, CachedStationRoute.expiresAt FROM CachedStationRoute
    |WHERE originCellId = ? AND stationProvider = ? AND stationProviderId = ? AND routeMode = ?
    """.trimMargin(), mapper, 4) {
      bindString(0, originCellId)
      bindString(1, stationProvider)
      bindString(2, stationProviderId)
      bindString(3, routeMode)
    }

    override fun toString(): String = "TankPilotDb.sq:getCachedRoute"
  }
}
