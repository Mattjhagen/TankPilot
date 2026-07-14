package com.tankpilot.android.auto

/**
 * Android Auto driver-distraction guidance discourages deep screen stacks. TankPilot's
 * deepest path today is Home → Fuel Rescue Recommendations → Critical Fuel → Station
 * Detail (depth 4). This constant is the documented ceiling; FuelRescueScreenGraphTest
 * asserts every defined navigation path stays within it, so a future screen addition
 * that quietly exceeds it fails a test rather than shipping unnoticed.
 */
const val MAX_CAR_SCREEN_STACK_DEPTH = 5
