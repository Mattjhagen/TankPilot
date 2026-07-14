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

    private val definedPaths = listOf(
        "Home" to 1,
        "Home -> FuelRescueRecommendations" to 2,
        "Home -> FuelRescueRecommendations -> StationDetail" to 3,
        "Home -> FuelRescueRecommendations -> CriticalFuel" to 3,
        "Home -> FuelRescueRecommendations -> CriticalFuel -> StationDetail(warning)" to 4,
        "Home -> FuelRescueRecommendations -> CriticalFuel -> RoadsideAssistanceInfo" to 4
    )

    @Test
    fun everyDefinedNavigationPathStaysWithinTheDepthCeiling() {
        definedPaths.forEach { (path, depth) ->
            assertTrue("$path has depth $depth, exceeds MAX_CAR_SCREEN_STACK_DEPTH=$MAX_CAR_SCREEN_STACK_DEPTH", depth <= MAX_CAR_SCREEN_STACK_DEPTH)
        }
    }
}
