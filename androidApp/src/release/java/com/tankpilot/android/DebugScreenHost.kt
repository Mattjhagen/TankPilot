package com.tankpilot.android

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Release variant: TEST_LAB screen is not available in release builds.
 * This stub prevents the Screen.TEST_LAB enum case from causing a compile error.
 * Navigation to TEST_LAB is blocked by BuildConfig.DEBUG checks in HomeScreen.
 */
@Composable
fun DebugScreenHost(onNavigateBack: () -> Unit) {
    // No-op in release builds. HomeScreen never navigates here.
}
