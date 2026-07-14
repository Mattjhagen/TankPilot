package com.tankpilot.db.shared

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.tankpilot.db.TankPilotDb
import com.tankpilot.db.TankPilotDbQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<TankPilotDb>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = TankPilotDbImpl.Schema

internal fun KClass<TankPilotDb>.newInstance(driver: SqlDriver): TankPilotDb =
    TankPilotDbImpl(driver)

private class TankPilotDbImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), TankPilotDb {
  override val tankPilotDbQueries: TankPilotDbQueries = TankPilotDbQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 3

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE Vehicle (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    year INTEGER NOT NULL,
          |    make TEXT NOT NULL,
          |    model TEXT NOT NULL,
          |    trim TEXT,
          |    color TEXT,
          |    engine TEXT NOT NULL,
          |    engineDisplacementLiters REAL,
          |    cylinderCount INTEGER,
          |    tankCapacity REAL NOT NULL,
          |    factoryCityMpg REAL NOT NULL,
          |    factoryHwyMpg REAL NOT NULL,
          |    learnedMpg REAL NOT NULL,
          |    preferredFuelType TEXT NOT NULL, -- REGULAR, MIDGRADE, PREMIUM, DIESEL, E85, UNKNOWN
          |    preferredFuelGrade TEXT,
          |    unitSystem TEXT NOT NULL, -- IMPERIAL, METRIC
          |    reserveFuelGallons REAL NOT NULL,
          |    lowFuelThresholdPercent REAL NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE Trip (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    vehicleId TEXT NOT NULL,
          |    timestamp INTEGER NOT NULL,
          |    distance REAL NOT NULL,
          |    duration INTEGER NOT NULL,
          |    idleTime INTEGER NOT NULL,
          |    averageSpeed REAL NOT NULL,
          |    drivingType TEXT NOT NULL,
          |    fuelBurned REAL NOT NULL,
          |    FOREIGN KEY(vehicleId) REFERENCES Vehicle(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE FillUp (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    vehicleId TEXT NOT NULL,
          |    timestamp INTEGER NOT NULL,
          |    gallonsAdded REAL NOT NULL,
          |    price REAL NOT NULL,
          |    odometer REAL,
          |    isFull INTEGER NOT NULL,
          |    FOREIGN KEY(vehicleId) REFERENCES Vehicle(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CachedQueryCell (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    provider TEXT NOT NULL,
          |    latitudeCell INTEGER NOT NULL,
          |    longitudeCell INTEGER NOT NULL,
          |    radiusMiles REAL NOT NULL,
          |    fuelType TEXT NOT NULL,
          |    fetchedAt INTEGER NOT NULL,
          |    expiresAt INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CachedFuelStation (
          |    provider TEXT NOT NULL,
          |    providerId TEXT NOT NULL,
          |    name TEXT NOT NULL,
          |    brand TEXT,
          |    latitude REAL NOT NULL,
          |    longitude REAL NOT NULL,
          |    address TEXT,
          |    fetchedAt INTEGER NOT NULL,
          |    PRIMARY KEY(provider, providerId)
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CachedFuelPrice (
          |    stationProvider TEXT NOT NULL,
          |    stationProviderId TEXT NOT NULL,
          |    fuelType TEXT NOT NULL,
          |    fuelGradeKey TEXT NOT NULL DEFAULT '',
          |    displayFuelGrade TEXT,
          |    priceAmountMicros INTEGER NOT NULL,
          |    currencyCode TEXT NOT NULL,
          |    priceUnit TEXT NOT NULL,
          |    providerUpdatedAt INTEGER,
          |    fetchedAt INTEGER NOT NULL,
          |    PRIMARY KEY (stationProvider, stationProviderId, fuelType, fuelGradeKey),
          |    FOREIGN KEY(stationProvider, stationProviderId) REFERENCES CachedFuelStation(provider, providerId) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CachedQueryStation (
          |    queryCellId TEXT NOT NULL,
          |    stationProvider TEXT NOT NULL,
          |    stationProviderId TEXT NOT NULL,
          |    PRIMARY KEY (queryCellId, stationProvider, stationProviderId),
          |    FOREIGN KEY(queryCellId) REFERENCES CachedQueryCell(id) ON DELETE CASCADE,
          |    FOREIGN KEY(stationProvider, stationProviderId) REFERENCES CachedFuelStation(provider, providerId) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CachedStationRoute (
          |    originCellId TEXT NOT NULL,
          |    stationProvider TEXT NOT NULL,
          |    stationProviderId TEXT NOT NULL,
          |    routeMode TEXT NOT NULL,
          |    distanceMiles REAL NOT NULL,
          |    durationSeconds INTEGER NOT NULL,
          |    fetchedAt INTEGER NOT NULL,
          |    expiresAt INTEGER NOT NULL,
          |    PRIMARY KEY (originCellId, stationProvider, stationProviderId, routeMode),
          |    FOREIGN KEY(stationProvider, stationProviderId) REFERENCES CachedFuelStation(provider, providerId) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    private fun migrateInternal(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
    ): QueryResult.Value<Unit> {
      if (oldVersion <= 1 && newVersion > 1) {
        driver.execute(null, """
            |CREATE TABLE Vehicle (
            |    id TEXT NOT NULL PRIMARY KEY,
            |    year INTEGER NOT NULL,
            |    make TEXT NOT NULL,
            |    model TEXT NOT NULL,
            |    trim TEXT, -- Nullable trim configuration
            |    color TEXT, -- Nullable vehicle color hex or name
            |    engine TEXT NOT NULL,
            |    tankCapacity REAL NOT NULL,
            |    factoryCityMpg REAL NOT NULL,
            |    factoryHwyMpg REAL NOT NULL,
            |    learnedMpg REAL NOT NULL
            |)
            """.trimMargin(), 0)
        driver.execute(null, """
            |CREATE TABLE Trip (
            |    id TEXT NOT NULL PRIMARY KEY,
            |    vehicleId TEXT NOT NULL,
            |    timestamp INTEGER NOT NULL,
            |    distance REAL NOT NULL,
            |    duration INTEGER NOT NULL,
            |    idleTime INTEGER NOT NULL,
            |    averageSpeed REAL NOT NULL,
            |    drivingType TEXT NOT NULL,
            |    fuelBurned REAL NOT NULL,
            |    FOREIGN KEY(vehicleId) REFERENCES Vehicle(id) ON DELETE CASCADE
            |)
            """.trimMargin(), 0)
        driver.execute(null, """
            |CREATE TABLE FillUp (
            |    id TEXT NOT NULL PRIMARY KEY,
            |    vehicleId TEXT NOT NULL,
            |    timestamp INTEGER NOT NULL,
            |    gallonsAdded REAL NOT NULL,
            |    price REAL NOT NULL,
            |    odometer REAL,
            |    isFull INTEGER NOT NULL,
            |    FOREIGN KEY(vehicleId) REFERENCES Vehicle(id) ON DELETE CASCADE
            |)
            """.trimMargin(), 0)
      }
      if (oldVersion <= 2 && newVersion > 2) {
        driver.execute(null, "ALTER TABLE Vehicle ADD COLUMN engineDisplacementLiters REAL", 0)
        driver.execute(null, "ALTER TABLE Vehicle ADD COLUMN cylinderCount INTEGER", 0)
        driver.execute(null,
            "ALTER TABLE Vehicle ADD COLUMN preferredFuelType TEXT NOT NULL DEFAULT 'REGULAR'", 0)
        driver.execute(null, "ALTER TABLE Vehicle ADD COLUMN preferredFuelGrade TEXT", 0)
        driver.execute(null,
            "ALTER TABLE Vehicle ADD COLUMN unitSystem TEXT NOT NULL DEFAULT 'IMPERIAL'", 0)
        driver.execute(null,
            "ALTER TABLE Vehicle ADD COLUMN reserveFuelGallons REAL NOT NULL DEFAULT 2.0", 0)
        driver.execute(null,
            "ALTER TABLE Vehicle ADD COLUMN lowFuelThresholdPercent REAL NOT NULL DEFAULT 0.20", 0)
        driver.execute(null, """
            |CREATE TABLE CachedQueryCell (
            |    id TEXT NOT NULL PRIMARY KEY,
            |    latitudeCell INTEGER NOT NULL,
            |    longitudeCell INTEGER NOT NULL,
            |    radiusMiles REAL NOT NULL,
            |    fuelType TEXT NOT NULL,
            |    fetchedAt INTEGER NOT NULL,
            |    expiresAt INTEGER NOT NULL
            |)
            """.trimMargin(), 0)
        driver.execute(null, """
            |CREATE TABLE CachedFuelStation (
            |    providerId TEXT NOT NULL PRIMARY KEY,
            |    name TEXT NOT NULL,
            |    brand TEXT NOT NULL,
            |    latitude REAL NOT NULL,
            |    longitude REAL NOT NULL,
            |    address TEXT NOT NULL,
            |    fetchedAt INTEGER NOT NULL
            |)
            """.trimMargin(), 0)
        driver.execute(null, """
            |CREATE TABLE CachedFuelPrice (
            |    stationProviderId TEXT NOT NULL,
            |    fuelType TEXT NOT NULL,
            |    fuelGradeKey TEXT NOT NULL DEFAULT '',
            |    displayFuelGrade TEXT,
            |    priceAmountMicros INTEGER NOT NULL,
            |    currencyCode TEXT NOT NULL,
            |    priceUnit TEXT NOT NULL,
            |    providerUpdatedAt INTEGER,
            |    fetchedAt INTEGER NOT NULL,
            |    PRIMARY KEY (stationProviderId, fuelType, fuelGradeKey),
            |    FOREIGN KEY(stationProviderId) REFERENCES CachedFuelStation(providerId) ON DELETE CASCADE
            |)
            """.trimMargin(), 0)
      }
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> {
      var lastVersion = oldVersion

      callbacks.filter { it.afterVersion in oldVersion until newVersion }
      .sortedBy { it.afterVersion }
      .forEach { callback ->
        migrateInternal(driver, oldVersion = lastVersion, newVersion = callback.afterVersion + 1)
        callback.block(driver)
        lastVersion = callback.afterVersion + 1
      }

      if (lastVersion < newVersion) {
        migrateInternal(driver, lastVersion, newVersion)
      }
      return QueryResult.Unit
    }
  }
}
