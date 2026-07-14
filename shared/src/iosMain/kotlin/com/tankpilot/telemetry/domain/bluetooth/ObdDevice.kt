package com.tankpilot.telemetry.domain.bluetooth

actual class ObdDevice(
    actual val name: String?,
    actual val address: String,
    actual var rssi: Int = 0
)
