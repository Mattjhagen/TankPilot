package com.tankpilot.core

import kotlinx.datetime.Instant

class FakeAppClock(var currentTime: Instant) : AppClock {
    override fun now(): Instant = currentTime
}
