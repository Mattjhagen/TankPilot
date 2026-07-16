package com.tankpilot.core

/**
 * Minimal cross-platform diagnostic logging so shared/commonMain code (trip state
 * transitions, active-session persistence, location validation) can log without
 * pulling in android.util.Log, which would break the iOS targets. Never log precise
 * coordinates, credentials, or other personal data through this — tag/state names and
 * counts only.
 */
expect object AppLogger {
    fun d(tag: String, message: String)
    fun w(tag: String, message: String)
}
