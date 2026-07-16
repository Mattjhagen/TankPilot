package com.tankpilot.core

actual object AppLogger {
    actual fun d(tag: String, message: String) {
        println("D/$tag: $message")
    }

    actual fun w(tag: String, message: String) {
        println("W/$tag: $message")
    }
}
