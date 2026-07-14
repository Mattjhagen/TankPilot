package com.tankpilot.android.auto.mapper

import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.Row
import com.tankpilot.android.auto.model.CarFuelSnapshot

/**
 * Thin androidx.car.app translation of [buildFuelStatusRowContents] — no formatting
 * logic here, just Row/Pane construction.
 */
fun CarFuelSnapshot.toPane(onFindFuelClick: () -> Unit): Pane {
    val builder = Pane.Builder()
    buildFuelStatusRowContents(this).forEach { content ->
        builder.addRow(Row.Builder().setTitle(content.title).addText(content.text).build())
    }
    builder.addAction(
        Action.Builder()
            .setTitle("Find Fuel")
            .setOnClickListener(onFindFuelClick)
            .build()
    )
    return builder.build()
}
