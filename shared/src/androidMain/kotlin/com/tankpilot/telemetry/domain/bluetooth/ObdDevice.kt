package com.tankpilot.telemetry.domain.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

actual class ObdDevice(val androidDevice: BluetoothDevice) {
    
    actual val name: String?
        @SuppressLint("MissingPermission")
        get() = androidDevice.name
    actual val address: String
        get() = androidDevice.address
    actual var rssi: Int = 0
}
