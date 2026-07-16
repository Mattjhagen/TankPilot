package com.tankpilot.core

import platform.Foundation.NSUUID

actual fun randomUuid(): String = NSUUID().UUIDString()
