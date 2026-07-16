package com.tankpilot.core

import android.util.Log

actual object AppLogger {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual fun w(tag: String, message: String) {
        Log.w(tag, message)
    }
}
