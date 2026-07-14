package com.tankpilot.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.tankpilot.db.shared.newInstance
import com.tankpilot.db.shared.schema
import kotlin.Unit

public interface TankPilotDb : Transacter {
  public val tankPilotDbQueries: TankPilotDbQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = TankPilotDb::class.schema

    public operator fun invoke(driver: SqlDriver): TankPilotDb =
        TankPilotDb::class.newInstance(driver)
  }
}
