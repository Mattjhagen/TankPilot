package com.tankpilot.android.auto.mapper

import com.tankpilot.android.auto.model.CarFuelSnapshot
import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.FuelStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies requirements 1, 2, and 8 from phases/phase-03a-android-auto-foundation.md
 * (estimates labeled as estimates, missing data never renders as zero, root screen
 * stays within a supported row count) against the same pure content builder
 * TankPilotCarHomeScreen's onGetTemplate() actually calls — see
 * CarFuelSnapshotPaneMapper.kt for the thin Row/Pane translation this feeds. The real
 * androidx.car.app types are not constructed here; they throw outside Robolectric/
 * instrumentation in a plain JVM test (confirmed empirically), so the pure content
 * layer is the testable seam.
 */
class CarFuelStatusRowMapperTest {

    private fun fullSnapshot() = CarFuelSnapshot(
        vehicleLabel = "2020 Honda Civic",
        fuelPercent = 34,
        gallonsRemaining = 4.2,
        safeRangeMiles = 112.0,
        confidencePercent = 88,
        confidenceLevel = ConfidenceLevel.HIGH,
        fuelStatus = FuelStatus.NORMAL,
        reachableStationCount = 3,
        isPreviewFixture = false
    )

    @Test
    fun estimatedFuelRowIsLabeledAsAnEstimate() {
        val rows = buildFuelStatusRowContents(fullSnapshot())
        val fuelRow = rows.first { it.title == "Estimated Fuel" }
        assertTrue(fuelRow.title.contains("Estimated"))
    }

    @Test
    fun missingFuelDoesNotRenderAsZero() {
        val rows = buildFuelStatusRowContents(CarFuelSnapshot.unavailable())
        val fuelRow = rows.first { it.title == "Estimated Fuel" }
        assertEquals("Unavailable", fuelRow.text)
        assertFalse(fuelRow.text.contains("0%"))
        assertFalse(fuelRow.text.contains("0 gal"))
    }

    @Test
    fun missingSafeRangeDoesNotRenderAsZero() {
        val rows = buildFuelStatusRowContents(CarFuelSnapshot.unavailable())
        val rangeRow = rows.first { it.title == "Safe Range" }
        assertEquals("Unavailable", rangeRow.text)
        assertFalse(rangeRow.text.contains("0 mi"))
    }

    @Test
    fun missingReachableStationCountDoesNotRenderAsZeroStations() {
        val rows = buildFuelStatusRowContents(fullSnapshot().copy(reachableStationCount = null))
        val rescueRow = rows.first { it.title == "Fuel Rescue" }
        assertEquals("Unavailable", rescueRow.text)
    }

    @Test
    fun zeroReachableStationsIsDistinctFromUnavailable() {
        val rows = buildFuelStatusRowContents(fullSnapshot().copy(reachableStationCount = 0))
        val rescueRow = rows.first { it.title == "Fuel Rescue" }
        assertEquals("No safe stations nearby", rescueRow.text)
    }

    @Test
    fun rootScreenStaysWithinSupportedRowCount() {
        val rows = buildFuelStatusRowContents(fullSnapshot())
        // PaneTemplate's real row ceiling isn't documented with an exact number by
        // Google; 5 is what this screen currently uses (fuel, range, confidence,
        // status, rescue) plus 1 action — a future row addition should fail this
        // test and force a deliberate decision, not silently grow past what's been
        // exercised on a real host/DHU.
        assertTrue("Root screen has ${rows.size} rows", rows.size <= 5)
    }
}
