package com.tankpilot.core

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tankpilot.db.TankPilotDb

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TankPilotDb.Schema, "tankpilot.db")
    }
}
