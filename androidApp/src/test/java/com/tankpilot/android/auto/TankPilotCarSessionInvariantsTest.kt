package com.tankpilot.android.auto

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import java.io.File
import org.junit.Test

/**
 * androidx.car.app.Screen/Session cannot be constructed in a plain JVM unit test —
 * CarContext's constructor needs a real main Looper (confirmed empirically:
 * NullPointerException from Looper.getMainLooper() even via
 * androidx.car.app.testing.TestCarContext, without Robolectric, which this project
 * does not use). Koin's `by inject()` also erases the injected type at the bytecode
 * level, so there is no reflectable trace of which types TankPilotCarSession/
 * TankPilotCarHomeScreen depend on.
 *
 * Given those constraints, this test asserts the actual invariant — "Android Auto
 * never starts location tracking or a foreground service" — at the source level: a
 * future change that adds a DrivingTrackingCoordinator dependency (or a direct call to
 * startTracking()/stopTracking()) to either class fails here, rather than shipping
 * unnoticed.
 */
class TankPilotCarSessionInvariantsTest {

    private fun readSource(relativePath: String): String {
        val file = File(relativePath)
        assertTrue("Expected to find $relativePath relative to the androidApp module directory", file.exists())
        return file.readText()
    }

    // Matches an actual Kotlin dependency on the coordinator (an import, or a
    // `: DrivingTrackingCoordinator` type reference/injection) — not prose mentioning
    // its name in a comment, which several of these files deliberately do to document
    // this very invariant.
    private val coordinatorDependencyPattern = Regex(
        "import\\s+com\\.tankpilot\\.android\\.managers\\.DrivingTrackingCoordinator|" +
            ":\\s*DrivingTrackingCoordinator\\b"
    )

    @Test
    fun tankPilotCarSessionNeverReferencesDrivingTrackingCoordinator() {
        val source = readSource("src/main/java/com/tankpilot/android/auto/TankPilotCarSession.kt")
        assertFalse(
            "TankPilotCarSession must never depend on DrivingTrackingCoordinator — Android Auto only observes state a phone-initiated Start Drive already produced",
            coordinatorDependencyPattern.containsMatchIn(source)
        )
    }

    @Test
    fun tankPilotCarHomeScreenNeverReferencesDrivingTrackingCoordinator() {
        val source = readSource("src/main/java/com/tankpilot/android/auto/screen/TankPilotCarHomeScreen.kt")
        assertFalse(
            "TankPilotCarHomeScreen must never depend on DrivingTrackingCoordinator — Android Auto only observes state a phone-initiated Start Drive already produced",
            coordinatorDependencyPattern.containsMatchIn(source)
        )
    }

    @Test
    fun tankPilotCarRootScreenNeverReferencesDrivingTrackingCoordinator() {
        val source = readSource("src/main/java/com/tankpilot/android/auto/screen/TankPilotCarRootScreen.kt")
        assertFalse(
            "TankPilotCarRootScreen (the Phase A POI root) must never depend on DrivingTrackingCoordinator — Android Auto only observes state a phone-initiated vehicle setup/Start Drive already produced",
            coordinatorDependencyPattern.containsMatchIn(source)
        )
    }

    @Test
    fun neitherFileCallsStartTrackingOrStopTracking() {
        val sources = listOf(
            "src/main/java/com/tankpilot/android/auto/TankPilotCarSession.kt",
            "src/main/java/com/tankpilot/android/auto/screen/TankPilotCarHomeScreen.kt",
            "src/main/java/com/tankpilot/android/auto/screen/TankPilotCarRootScreen.kt"
        ).map { readSource(it) }

        sources.forEach { source ->
            assertFalse("Android Auto must never call startTracking()", source.contains("startTracking("))
            assertFalse("Android Auto must never call stopTracking()", source.contains("stopTracking("))
        }
    }

    @Test
    fun homeScreenInvalidatesWhenCanonicalFuelModelStateChanges() {
        val source = readSource("src/main/java/com/tankpilot/android/auto/screen/TankPilotCarHomeScreen.kt")
        // The car screen must re-render when FuelModelUseCase's displayed/range/status
        // flows change — verified here as "a combine() over those flows feeds invalidate()",
        // since TankPilotCarHomeScreen extends Screen and can't be instantiated in a plain
        // JVM test (see class doc).
        assertTrue(
            "TankPilotCarHomeScreen must combine FuelModelUseCase's canonical flows",
            source.contains("fuelModelUseCase.displayedFuelRemainingGallons") &&
                source.contains("fuelModelUseCase.fuelStatus") &&
                source.contains("fuelModelUseCase.warningText")
        )
        assertTrue(
            "The combined canonical state must drive invalidate()",
            Regex("combine\\([\\s\\S]*?\\)\\s*\\{[\\s\\S]*?}\\.collect\\s*\\{[\\s\\S]*?invalidate\\(\\)").containsMatchIn(source)
        )
    }

    @Test
    fun liveDiagnosticsPanelIsDebugOnlyAndAbsentFromRelease() {
        val debugFile = File("src/debug/java/com/tankpilot/android/ui/screens/testlab/LiveDiagnosticsPanel.kt")
        assertTrue("LiveDiagnosticsPanel.kt must live under src/debug, not src/main", debugFile.exists())

        val releaseDebugScreenHost = readSource("src/release/java/com/tankpilot/android/DebugScreenHost.kt")
        assertFalse(
            "Release's DebugScreenHost must never reference the debug-only diagnostics panel",
            releaseDebugScreenHost.contains("LiveDiagnosticsPanel") || releaseDebugScreenHost.contains("TestLabScreen")
        )
    }
}
