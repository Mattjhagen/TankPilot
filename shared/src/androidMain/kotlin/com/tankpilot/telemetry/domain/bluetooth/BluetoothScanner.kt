package com.tankpilot.telemetry.domain.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

actual class BluetoothScanner(private val context: Context) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val adapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val leScanner = adapter?.bluetoothLeScanner

    private val _bleDevices = MutableStateFlow<List<ObdDevice>>(emptyList())
    actual val bleDevices: StateFlow<List<ObdDevice>> = _bleDevices.asStateFlow()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                // Basic filtering for OBD candidates by name
                val name = device.name ?: device.address
                if (name.contains("vLinker", ignoreCase = true) ||
                    name.contains("OBD", ignoreCase = true) ||
                    name.contains("Vgate", ignoreCase = true) ||
                    name.contains("ELM", ignoreCase = true)
                ) {
                    val obdDevice = ObdDevice(device).apply {
                        this.rssi = result.rssi
                    }
                    _bleDevices.update { current ->
                        val existing = current.find { it.address == device.address }
                        if (existing != null) {
                            current.map { if (it.address == device.address) obdDevice else it }
                        } else {
                            current + obdDevice
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    actual fun getBondedDevices(): List<ObdDevice> {
        if (adapter == null || !adapter.isEnabled) return emptyList()
        return try {
            adapter.bondedDevices.map { ObdDevice(it) }
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    actual val isBluetoothEnabled: Boolean
        get() = adapter?.isEnabled == true

    @SuppressLint("MissingPermission")
    actual fun startBleScan() {
        if (!isBluetoothEnabled || leScanner == null) return
        _bleDevices.value = emptyList()
        try {
            leScanner.startScan(scanCallback)
        } catch (e: Exception) {}
    }

    @SuppressLint("MissingPermission")
    actual fun stopBleScan() {
        if (!isBluetoothEnabled || leScanner == null) return
        try {
            leScanner.stopScan(scanCallback)
        } catch (e: Exception) {}
    }
}
