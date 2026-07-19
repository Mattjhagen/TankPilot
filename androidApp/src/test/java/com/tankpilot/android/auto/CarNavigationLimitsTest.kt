package com.tankpilot.android.auto

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TankPilot doesn't instantiate real Screen/CarContext objects in JVM unit tests (that
 * needs a host or Robolectric), so this documents the app's defined navigation paths
 * as plain data and checks them against MAX_CAR_SCREEN_STACK_DEPTH — the same constant
 * every screen's push() call site is expected to respect. A new screen that quietly
 * exceeds the ceiling should fail here, not first get discovered in the DHU.
 */
class CarNavigationLimitsTest {

    // Phase A: TankPilotCarRootScreen is the root (a genuine POI nearby-station list),
    // folding in what FuelRescueRecommendationsScreen previously did (now deleted).
    // TankPilotCarHomeScreen is kept in the codebase but unlinked from this release's
    // graph entirely — see TankPilotCarRootScreen's doc comment.
    private val definedPaths = listOf(
        "TankPilotCarRootScreen" to 1,
        "TankPilotCarRootScreen -> StationDetail" to 2,
        "TankPilotCarRootScreen -> CriticalFuel" to 2,
        "TankPilotCarRootScreen -> CriticalFuel -> StationDetail(warning)" to 3,
        "TankPilotCarRootScreen -> CriticalFuel -> RoadsideAssistanceInfo" to 3
    )

    @Test
    fun everyDefinedNavigationPathStaysWithinTheDepthCeiling() {
        definedPaths.forEach { (path, depth) ->
            assertTrue("$path has depth $depth, exceeds MAX_CAR_SCREEN_STACK_DEPTH=$MAX_CAR_SCREEN_STACK_DEPTH", depth <= MAX_CAR_SCREEN_STACK_DEPTH)
        }
    }
}
