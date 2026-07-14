package com.tankpilot.android

import androidx.compose.runtime.Composable

/**
 * Debug variant: delegates to the real TestLabScreen.
 */
@Composable
fun DebugScreenHost(onNavigateBack: () -> Unit) {
    com.tankpilot.android.ui.screens.testlab.TestLabScreen(
        onNavigateBack = onNavigateBack
    )
}
