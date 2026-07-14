package com.tankpilot.telemetry.domain.bluetooth

expect class ObdDevice {
    val name: String?
    val address: String
    var rssi: Int
}
