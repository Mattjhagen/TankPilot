package com.tankpilot.android.ui.screens

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * DashboardScreen's root Box previously had no system-bar inset handling — on a
 * targetSdk 35 device (edge-to-edge is enforced, not optional, from Android 15+),
 * this let the status bar draw over the Start Drive button and top labels (confirmed
 * on a physical device). Compose UI tests aren't available in this project (no
 * androidx.compose.ui:ui-test-junit4 or Robolectric dependency), so this is a
 * source-level invariant instead: it fails if a future edit removes the inset
 * modifier from the root container.
 */
class DashboardScreenInsetsTest {

    private fun readSource(): String {
        val file = File("src/main/java/com/tankpilot/android/ui/screens/DashboardScreen.kt")
        assertTrue("Expected to find ${file.path} relative to the androidApp module directory", file.exists())
        return file.readText()
    }

    @Test
    fun rootContainerAppliesSafeDrawingInsetsBeforeVisualPadding() {
        val source = readSource()
        val rootBox = source.substringAfter("fun DashboardScreen(").substringAfter("Box(")

        assertTrue(
            "DashboardScreen's root Box must apply WindowInsets.safeDrawing so content " +
                "isn't drawn under the status/navigation bars on edge-to-edge (targetSdk 35+) devices",
            rootBox.contains("windowInsetsPadding(WindowInsets.safeDrawing)")
        )
    }

    @Test
    fun insetPaddingIsAppliedBeforeTheVisualPadding() {
        // Order matters: insets must constrain the content area first, with the
        // existing 20dp visual padding applied on top of that — not the other way
        // around, or the visual padding alone would still leave content under the bars.
        val source = readSource()
        val rootBox = source.substringAfter("fun DashboardScreen(").substringAfter("Box(")
        val insetsIndex = rootBox.indexOf("windowInsetsPadding(WindowInsets.safeDrawing)")
        val paddingIndex = rootBox.indexOf(".padding(20.dp)")

        assertTrue("windowInsetsPadding must be present", insetsIndex >= 0)
        assertTrue(".padding(20.dp) must be present", paddingIndex >= 0)
        assertTrue("windowInsetsPadding must come before the visual .padding(20.dp)", insetsIndex < paddingIndex)
    }
}
