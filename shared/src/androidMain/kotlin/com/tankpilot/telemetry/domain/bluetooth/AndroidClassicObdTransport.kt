package com.tankpilot.telemetry.domain.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class AndroidClassicObdTransport(
    private val device: ObdDevice,
    private val scope: CoroutineScope
) : ObdTransport {

    private val sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    
    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private val _connectionState = MutableStateFlow(ObdTransportState.DISCONNECTED)
    override val connectionState = _connectionState.asStateFlow()

    private val _events = MutableSharedFlow<ObdTransportEvent>(extraBufferCapacity = 10)
    override val events = _events.asSharedFlow()

    private val _incomingData = MutableSharedFlow<ByteArray>(extraBufferCapacity = 100)
    
    private var readJob: Job? = null

    @SuppressLint("MissingPermission")
    override suspend fun connect() {
        withContext(Dispatchers.IO) {
            _connectionState.value = ObdTransportState.CONNECTING
            _events.tryEmit(ObdTransportEvent.ConnectionStateChanged(ObdTransportState.CONNECTING))
            
            try {
                socket = device.androidDevice.createRfcommSocketToServiceRecord(sppUuid)
                socket?.connect()
                inputStream = socket?.inputStream
                outputStream = socket?.outputStream
                
                _connectionState.value = ObdTransportState.CONNECTED
                _events.tryEmit(ObdTransportEvent.ConnectionStateChanged(ObdTransportState.CONNECTED))
                
                startReadLoop()
            } catch (e: Exception) {
                _connectionState.value = ObdTransportState.ERROR
                _events.tryEmit(ObdTransportEvent.Error("Classic connect failed", e))
                try { socket?.close() } catch (ignored: Exception) {}
                throw e
            }
        }
    }

    private fun startReadLoop() {
        readJob?.cancel()
        readJob = scope.launch(Dispatchers.IO) {
            val buffer = ByteArray(1024)
            while (isActive && _connectionState.value == ObdTransportState.CONNECTED) {
                try {
                    val bytesRead = inputStream?.read(buffer) ?: -1
                    if (bytesRead > 0) {
                        _incomingData.emit(buffer.copyOf(bytesRead))
                    } else if (bytesRead == -1) {
                        break // EOF
                    }
                } catch (e: Exception) {
                    break
                }
            }
            disconnect()
        }
    }

    override suspend fun write(data: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                outputStream?.write(data)
                outputStream?.flush()
            } catch (e: Exception) {
                _events.tryEmit(ObdTransportEvent.Error("Write failed", e))
                disconnect()
            }
        }
    }

    override fun incomingData(): Flow<ByteArray> = _incomingData.asSharedFlow()

    override suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            readJob?.cancel()
            _connectionState.value = ObdTransportState.DISCONNECTED
            _events.tryEmit(ObdTransportEvent.ConnectionStateChanged(ObdTransportState.DISCONNECTED))
            try {
                inputStream?.close()
                outputStream?.close()
                socket?.close()
            } catch (ignored: Exception) {}
        }
    }
}
