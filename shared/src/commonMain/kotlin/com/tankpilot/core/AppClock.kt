package com.tankpilot.core

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface AppClock {
    fun now(): Instant
}

class SystemClock : AppClock {
    override fun now(): Instant = Clock.System.now()
}
