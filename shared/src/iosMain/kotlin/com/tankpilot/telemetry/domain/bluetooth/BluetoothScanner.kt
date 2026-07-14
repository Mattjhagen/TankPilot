package com.tankpilot.telemetry.domain.bluetooth

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

actual class BluetoothScanner {
    actual fun getBondedDevices(): List<ObdDevice> = emptyList()
    actual val isBluetoothEnabled: Boolean = false
    
    private val _bleDevices = MutableStateFlow<List<ObdDevice>>(emptyList())
    actual val bleDevices: StateFlow<List<ObdDevice>> = _bleDevices
    
    actual fun startBleScan() {}
    actual fun stopBleScan() {}
}
