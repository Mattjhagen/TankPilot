package com.tankpilot.telemetry.domain.bluetooth

import kotlinx.coroutines.flow.StateFlow

expect class BluetoothScanner {
    fun getBondedDevices(): List<ObdDevice>
    val isBluetoothEnabled: Boolean

    val bleDevices: StateFlow<List<ObdDevice>>
    fun startBleScan()
    fun stopBleScan()
}
