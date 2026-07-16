package com.tankpilot.android.auto.mapper

import com.tankpilot.android.auto.model.CarFuelSnapshot
import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.core.FuelStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the root Android Auto screen's 4-row content — built from the same pure
 * content builder TankPilotCarHomeScreen's onGetTemplate() actually calls (see
 * CarFuelSnapshotPaneMapper.kt for the thin Row/Pane translation this feeds). The real
 * androidx.car.app types are not constructed here; TestCarContext/Screen require a real
 * main Looper (confirmed empirically — NullPointerException on Looper.getMainLooper()
 * without Robolectric), so the pure content layer remains the testable seam.
 */
class CarFuelStatusRowMapperTest {

    private fun activeSnapshot() = CarFuelSnapshot(
        vehicleLabel = "2020 Honda Civic",
        fuelPercent = 34,
        gallonsRemaining = 4.2,
        conservativeRangeMiles = 112.0,
        expectedRangeMiles = 128.0,
        confidencePercent = 88,
        confidenceLevel = ConfidenceLevel.HIGH,
        fuelStatus = FuelStatus.NORMAL,
        reachableStationCount = 3,
        drivingPattern = "Highway",
        mpgValue = 30.5,
        mpgSource = "GPS",
        alertsText = "All Good",
        isTrackingActive = true,
        isLocationUnavailable = false,
        isPreviewFixture = false
    )

    // --- Row count ---

    @Test
    fun rootScreenHasExactlyFourRows() {
        val rows = buildFuelStatusRowContents(activeSnapshot())
        assertEquals(4, rows.size)
    }

    @Test
    fun rootScreenHasExactlyFourRowsWhenTrackingInactiveAndDataMissing() {
        val rows = buildFuelStatusRowContents(CarFuelSnapshot.unavailable())
        assertEquals(4, rows.size)
    }

    // --- Row 1: Can I Keep Driving? ---

    @Test
    fun canIKeepDrivingRowShowsFuelPercentAndCanonicalStatus() {
        val row = buildFuelStatusRowContents(activeSnapshot()).first { it.title == "Can I Keep Driving?" }
        assertTrue(row.text.contains("34%"))
        assertTrue(row.text.contains("Normal"))
    }

    @Test
    fun canIKeepDrivingRowCarriesTheCanonicalAlertText() {
        val snapshot = activeSnapshot().copy(
            fuelStatus = FuelStatus.CRITICAL,
            alertsText = "Critical range. Refuel soon."
        )
        val row = buildFuelStatusRowContents(snapshot).first { it.title == "Can I Keep Driving?" }
        assertTrue(row.text.contains("Critical range. Refuel soon."))
    }

    @Test
    fun canIKeepDrivingRowMissingFuelDoesNotRenderAsZero() {
        val row = buildFuelStatusRowContents(CarFuelSnapshot.unavailable()).first { it.title == "Can I Keep Driving?" }
        assertEquals("Unavailable", row.text)
        assertFalse(row.text.contains("0%"))
    }

    // --- Row 2: Conservative Range ---

    @Test
    fun conservativeRangeRowShowsConservativeMilesAndConfidence() {
        val row = buildFuelStatusRowContents(activeSnapshot()).first { it.title == "Conservative Range" }
        assertTrue(row.text.contains("112"))
        assertTrue(row.text.contains("High"))
        assertTrue(row.text.contains("88%"))
    }

    @Test
    fun conservativeRangeRowMissingDoesNotRenderAsZero() {
        val row = buildFuelStatusRowContents(CarFuelSnapshot.unavailable()).first { it.title == "Conservative Range" }
        assertEquals("Unavailable", row.text)
        assertFalse(row.text.contains("0 mi"))
    }

    // --- Row 3: Driving ---

    @Test
    fun drivingRowShowsPatternMpgValueAndProvenance() {
        val row = buildFuelStatusRowContents(activeSnapshot()).first { it.title == "Driving" }
        assertTrue(row.text.contains("Highway"))
        assertTrue(row.text.contains("30.5"))
        assertTrue(row.text.contains("GPS"))
    }

    @Test
    fun drivingRowMissingPatternAndMpgShowUnavailableNotZero() {
        val snapshot = activeSnapshot().copy(drivingPattern = null, mpgValue = null, mpgSource = null)
        val row = buildFuelStatusRowContents(snapshot).first { it.title == "Driving" }
        assertFalse(row.text.contains("0.0"))
        assertTrue(row.text.contains("Unavailable"))
    }

    // --- Row 4: Fuel Rescue / Tracking ---

    @Test
    fun fuelRescueRowShowsStartDriveInstructionWhenTrackingInactive() {
        val snapshot = activeSnapshot().copy(isTrackingActive = false)
        val row = buildFuelStatusRowContents(snapshot).first { it.title == "Fuel Rescue" }
        assertEquals("Open TankPilot on phone and tap Start Drive.", row.text)
    }

    @Test
    fun fuelRescueRowShowsLocationUnavailableWhenTrackingActiveButNoLocation() {
        val snapshot = activeSnapshot().copy(isTrackingActive = true, isLocationUnavailable = true)
        val row = buildFuelStatusRowContents(snapshot).first { it.title == "Fuel Rescue" }
        assertEquals("Location unavailable", row.text)
    }

    @Test
    fun fuelRescueRowShowsRealStationCountWhenTrackingActiveAndLocationAvailable() {
        val snapshot = activeSnapshot().copy(isTrackingActive = true, isLocationUnavailable = false, reachableStationCount = 3)
        val row = buildFuelStatusRowContents(snapshot).first { it.title == "Fuel Rescue" }
        assertEquals("3 safe stations nearby", row.text)
    }

    @Test
    fun fuelRescueRowMissingStationCountDoesNotRenderAsZeroStations() {
        val snapshot = activeSnapshot().copy(isTrackingActive = true, isLocationUnavailable = false, reachableStationCount = null)
        val row = buildFuelStatusRowContents(snapshot).first { it.title == "Fuel Rescue" }
        assertEquals("Unavailable", row.text)
    }

    @Test
    fun fuelRescueRowZeroReachableStationsIsDistinctFromUnavailable() {
        val snapshot = activeSnapshot().copy(isTrackingActive = true, isLocationUnavailable = false, reachableStationCount = 0)
        val row = buildFuelStatusRowContents(snapshot).first { it.title == "Fuel Rescue" }
        assertEquals("No safe stations nearby", row.text)
    }

    @Test
    fun fuelRescueRowPrioritizesTrackingInstructionOverLocationUnavailable() {
        // Both conditions true at once — inactive tracking must win, since starting a
        // drive is the actionable first step regardless of location state.
        val snapshot = activeSnapshot().copy(isTrackingActive = false, isLocationUnavailable = true)
        val row = buildFuelStatusRowContents(snapshot).first { it.title == "Fuel Rescue" }
        assertEquals("Open TankPilot on phone and tap Start Drive.", row.text)
    }

    // --- No emojis anywhere ---

    @Test
    fun noRowContainsEmoji() {
        val emojiRanges = listOf(0x1F300..0x1FAFF, 0x2600..0x27BF, 0x2190..0x21FF, 0x2B00..0x2BFF)
        val rows = buildFuelStatusRowContents(activeSnapshot()) + buildFuelStatusRowContents(CarFuelSnapshot.unavailable())
        rows.forEach { row ->
            row.text.codePoints().forEach { codePoint ->
                assertFalse(
                    "Row '${row.title}' text contains an emoji character: ${row.text}",
                    emojiRanges.any { codePoint in it }
                )
            }
        }
    }
}
