package com.tankpilot.android.managers

import android.view.Window
import android.view.WindowManager

/**
 * Thin testable wrapper around Window FLAG_KEEP_SCREEN_ON.
 *
 * Extracted from DashboardWakeLockEffect so the acquire/release logic
 * can be verified by unit tests without a Compose runtime.
 *
 * Usage:
 *   val controller = WakeLockController(activity.window)
 *   controller.acquire()   // when driving
 *   controller.release()   // when backgrounded or dashboard hidden
 */
class WakeLockController(private val window: Window) {
    fun acquire() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun release() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
