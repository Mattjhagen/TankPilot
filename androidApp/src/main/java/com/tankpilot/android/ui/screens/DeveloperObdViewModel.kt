package com.tankpilot.android.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tankpilot.telemetry.data.ObdTelemetryProvider
import com.tankpilot.telemetry.domain.bluetooth.AndroidBleObdTransport
import com.tankpilot.telemetry.domain.bluetooth.AndroidClassicObdTransport
import com.tankpilot.telemetry.domain.bluetooth.BluetoothScanner
import com.tankpilot.telemetry.domain.bluetooth.ObdDevice
import com.tankpilot.telemetry.domain.bluetooth.ObdTransport
import com.tankpilot.telemetry.domain.obd.Elm327Driver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class DeveloperObdViewModel(private val context: Context) : ViewModel() {

    private val scanner = BluetoothScanner(context)

    val bleDevices: StateFlow<List<ObdDevice>> = scanner.bleDevices
    val bondedDevices: StateFlow<List<ObdDevice>> = MutableStateFlow(emptyList()) // Keep for classic fallback if needed

    private var activeTransport: ObdTransport? = null
    private var elmDriver: Elm327Driver? = null
    private var activeProvider: ObdTelemetryProvider? = null

    val isBluetoothEnabled = MutableStateFlow(scanner.isBluetoothEnabled)

    private val _selectedDevice = MutableStateFlow<ObdDevice?>(null)
    val selectedDevice: StateFlow<ObdDevice?> = _selectedDevice.asStateFlow()

    private val _isBleMode = MutableStateFlow(true)
    val isBleMode: StateFlow<Boolean> = _isBleMode.asStateFlow()

    fun setBleMode(isBle: Boolean) {
        _isBleMode.value = isBle
    }

    fun startScan() {
        isBluetoothEnabled.value = scanner.isBluetoothEnabled
        if (scanner.isBluetoothEnabled) {
            scanner.startBleScan()
        }
    }

    fun stopScan() {
        scanner.stopBleScan()
    }

    fun loadBondedDevices() {
        isBluetoothEnabled.value = scanner.isBluetoothEnabled
        if (scanner.isBluetoothEnabled) {
            (bondedDevices as MutableStateFlow).value = scanner.getBondedDevices()
        }
    }

    fun selectDevice(device: ObdDevice) {
        _selectedDevice.value = device
    }

    fun connect() {
        val device = _selectedDevice.value ?: return
        
        // Cleanup old
        viewModelScope.launch {
            activeProvider?.disconnect()
            activeTransport?.disconnect()
            elmDriver?.stop()
            
            val transport = if (_isBleMode.value) {
                AndroidBleObdTransport(context, device, viewModelScope)
            } else {
                AndroidClassicObdTransport(device, viewModelScope)
            }
            
            activeTransport = transport
            elmDriver = Elm327Driver(transport, viewModelScope)
            
            transport.connect()
        }
    }

    fun initElm() {
        viewModelScope.launch {
            try {
                elmDriver?.initialize()
                val banner = elmDriver?.getBanner()
                val protocol = elmDriver?.getProtocol()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun queryRpm() {
        viewModelScope.launch {
            elmDriver?.requestPid("01", "0C")
        }
    }

    fun querySpeed() {
        viewModelScope.launch {
            elmDriver?.requestPid("01", "0D")
        }
    }

    fun startPolling() {
        val transport = activeTransport ?: return
        viewModelScope.launch {
            activeProvider?.disconnect()
            activeProvider = ObdTelemetryProvider(transport, viewModelScope)
            activeProvider?.connect()
        }
    }

    fun stopPolling() {
        viewModelScope.launch {
            activeProvider?.disconnect()
            activeProvider = null
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            activeProvider?.disconnect()
            elmDriver?.stop()
            activeTransport?.disconnect()
        }
    }

    // Expose flows from the active provider for the UI
    val connectionStatus = flow {
        while (true) {
            emit(activeTransport?.connectionState?.value?.name ?: "DISCONNECTED")
            kotlinx.coroutines.delay(500)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, "DISCONNECTED")

    val transportEvents = flow {
        while (true) {
            // Need a better way to collect events, but for now we rely on the state flow
            emit("Listening to transport...")
            kotlinx.coroutines.delay(5000)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    val rawLogs = flow {
        while (true) {
            emit(elmDriver?.rawLogs?.value ?: emptyList())
            kotlinx.coroutines.delay(500)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val telemetryData = flow {
        while (true) {
            emit(activeProvider?.telemetryFlow?.value)
            kotlinx.coroutines.delay(500)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val capabilities = flow {
        while (true) {
            emit(activeProvider?.capabilitiesFlow?.value)
            kotlinx.coroutines.delay(500)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
