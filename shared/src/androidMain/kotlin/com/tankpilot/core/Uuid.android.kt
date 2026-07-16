package com.tankpilot.core

import java.util.UUID

actual fun randomUuid(): String = UUID.randomUUID().toString()
