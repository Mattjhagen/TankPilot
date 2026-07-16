package com.tankpilot.android.auto

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple in-process signal — not a new persistence layer — so the debug-only Test Lab
 * panel can show whether the Android Auto screen is currently visible. The car app
 * service runs in the same process as the phone app (no android:process override in
 * the manifest), so this plain in-memory StateFlow is all that's needed; it carries no
 * state across process death and isn't read by any production logic.
 */
object AndroidAutoVisibilityState {
    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

    internal fun setVisible(visible: Boolean) {
        _isVisible.value = visible
    }
}
