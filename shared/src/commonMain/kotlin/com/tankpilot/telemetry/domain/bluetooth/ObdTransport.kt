package com.tankpilot.telemetry.domain.bluetooth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

enum class ObdTransportState {
    DISCONNECTED,
    CONNECTING,
    DISCOVERING_SERVICES,
    CONNECTED,
    ERROR
}

sealed class ObdTransportEvent {
    data class Error(val message: String, val cause: Throwable? = null) : ObdTransportEvent()
    data class ConnectionStateChanged(val state: ObdTransportState) : ObdTransportEvent()
    data class Log(val message: String) : ObdTransportEvent()
}

interface ObdTransport {
    val connectionState: StateFlow<ObdTransportState>
    val events: Flow<ObdTransportEvent>
    
    suspend fun connect()
    suspend fun disconnect()
    suspend fun write(data: ByteArray)
    fun incomingData(): Flow<ByteArray>
}
