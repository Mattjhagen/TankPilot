package com.tankpilot.telemetry.domain.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

@SuppressLint("MissingPermission")
class AndroidBleObdTransport(
    private val context: Context,
    private val device: ObdDevice,
    private val scope: CoroutineScope
) : ObdTransport {

    private val _connectionState = MutableStateFlow(ObdTransportState.DISCONNECTED)
    override val connectionState = _connectionState.asStateFlow()

    private val _events = MutableSharedFlow<ObdTransportEvent>(extraBufferCapacity = 50)
    override val events = _events.asSharedFlow()

    private val _incomingData = MutableSharedFlow<ByteArray>(extraBufferCapacity = 100)
    override fun incomingData(): Flow<ByteArray> = _incomingData.asSharedFlow()

    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    
    private var writeContinuation: CancellableContinuation<Unit>? = null

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    _connectionState.value = ObdTransportState.DISCOVERING_SERVICES
                    _events.tryEmit(ObdTransportEvent.Log("GATT Connected. Discovering services..."))
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    _connectionState.value = ObdTransportState.DISCONNECTED
                    _events.tryEmit(ObdTransportEvent.Log("GATT Disconnected."))
                    closeGatt()
                }
            } else {
                _connectionState.value = ObdTransportState.ERROR
                _events.tryEmit(ObdTransportEvent.Error("GATT Error status: $status"))
                closeGatt()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                _events.tryEmit(ObdTransportEvent.Log("Services discovered. Configuring characteristics..."))
                configureCharacteristics(gatt)
            } else {
                _connectionState.value = ObdTransportState.ERROR
                _events.tryEmit(ObdTransportEvent.Error("Service discovery failed with status $status"))
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            // Android 13+ passes value directly
            _incomingData.tryEmit(value)
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            // Older Android versions
            _incomingData.tryEmit(characteristic.value)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeContinuation?.resumeWith(Result.success(Unit))
            } else {
                writeContinuation?.resumeWith(Result.failure(Exception("GATT write failed: $status")))
            }
            writeContinuation = null
        }
    }

    override suspend fun connect() {
        withContext(Dispatchers.Main) {
            _connectionState.value = ObdTransportState.CONNECTING
            _events.tryEmit(ObdTransportEvent.Log("Initiating GATT connection to ${device.address}"))
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = device.androidDevice.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
            } else {
                bluetoothGatt = device.androidDevice.connectGatt(context, false, gattCallback)
            }
        }
    }

    override suspend fun write(data: ByteArray) {
        val gatt = bluetoothGatt ?: throw IllegalStateException("Not connected")
        val char = writeCharacteristic ?: throw IllegalStateException("Write characteristic not configured")

        // Chunking logic (Standard BLE MTU payload limit is typically 20 bytes if not negotiated)
        val chunkSize = 20
        var offset = 0

        while (offset < data.size) {
            val end = minOf(offset + chunkSize, data.size)
            val chunk = data.copyOfRange(offset, end)
            
            suspendCancellableCoroutine<Unit> { cont ->
                writeContinuation = cont
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val writeType = if (char.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) {
                        BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                    } else {
                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    }
                    gatt.writeCharacteristic(char, chunk, writeType)
                } else {
                    char.value = chunk
                    char.writeType = if (char.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) {
                        BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                    } else {
                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    }
                    gatt.writeCharacteristic(char)
                }
            }
            offset = end
            delay(10) // Small delay between chunks
        }
    }

    override suspend fun disconnect() {
        withContext(Dispatchers.Main) {
            _connectionState.value = ObdTransportState.DISCONNECTED
            closeGatt()
        }
    }

    private fun configureCharacteristics(gatt: BluetoothGatt) {
        var notifyChar: BluetoothGattCharacteristic? = null
        
        for (profile in KnownBleProfiles) {
            for (service in gatt.services) {
                val serviceUuidStr = service.uuid.toString()
                
                // Extremely simple matching for now; production should normalize UUIDs to 128-bit
                if (profile.serviceUuids.any { serviceUuidStr.contains(it, ignoreCase = true) }) {
                    for (char in service.characteristics) {
                        val charUuidStr = char.uuid.toString()
                        if (profile.writeCharacteristicUuids.any { charUuidStr.contains(it, ignoreCase = true) }) {
                            writeCharacteristic = char
                        }
                        if (profile.notifyCharacteristicUuids.any { charUuidStr.contains(it, ignoreCase = true) }) {
                            notifyChar = char
                        }
                    }
                }
            }
            if (writeCharacteristic != null && notifyChar != null) {
                _events.tryEmit(ObdTransportEvent.Log("Matched BLE Profile: ${profile.id}"))
                break
            }
        }

        // Fallback: If no profile matched, blindly find any TX/RX pair.
        if (writeCharacteristic == null || notifyChar == null) {
            _events.tryEmit(ObdTransportEvent.Log("No known profile matched. Attempting heuristic fallback."))
            for (service in gatt.services) {
                for (char in service.characteristics) {
                    val props = char.properties
                    if (writeCharacteristic == null && ((props and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 || (props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0)) {
                        writeCharacteristic = char
                    }
                    if (notifyChar == null && ((props and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0 || (props and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0)) {
                        notifyChar = char
                    }
                }
            }
        }

        if (writeCharacteristic != null && notifyChar != null) {
            gatt.setCharacteristicNotification(notifyChar, true)
            
            // Enable CCCD descriptor
            val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            val descriptor = notifyChar.getDescriptor(cccdUuid)
            if (descriptor != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
            }
            
            _connectionState.value = ObdTransportState.CONNECTED
            _events.tryEmit(ObdTransportEvent.ConnectionStateChanged(ObdTransportState.CONNECTED))
        } else {
            _connectionState.value = ObdTransportState.ERROR
            _events.tryEmit(ObdTransportEvent.Error("Could not find suitable write/notify characteristics"))
            closeGatt()
        }
    }

    private fun closeGatt() {
        try {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
        } catch (e: Exception) {}
        bluetoothGatt = null
        writeCharacteristic = null
    }
}
