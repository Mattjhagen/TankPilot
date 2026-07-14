package com.tankpilot.telemetry.domain.obd

import com.tankpilot.telemetry.domain.bluetooth.ObdTransport
import com.tankpilot.telemetry.domain.bluetooth.ObdTransportState
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class Elm327Driver(
    private val transport: ObdTransport,
    private val scope: CoroutineScope
) {

    private val _rawLogs = MutableStateFlow<List<String>>(emptyList())
    val rawLogs: StateFlow<List<String>> = _rawLogs.asStateFlow()

    private fun logInteraction(command: String, response: String) {
        val sanitizedCommand = command.replace("\r", "")
        val sanitizedResponse = response.replace("\r", "\\r").replace("\n", "\\n").take(100) // Mask length
        // Do not log full VINs if a command was Mode 09
        if (sanitizedCommand.startsWith("09")) {
            _rawLogs.update { it + "[$sanitizedCommand] -> [REDACTED]" }
        } else {
            _rawLogs.update { it + "[$sanitizedCommand] -> [$sanitizedResponse]" }
        }
    }

    private val readChannel = Channel<ByteArray>(Channel.UNLIMITED)
    private var readJob: Job? = null

    init {
        startReadLoop()
    }

    private fun startReadLoop() {
        readJob?.cancel()
        readJob = scope.launch(Dispatchers.IO) {
            transport.incomingData().collect { data ->
                readChannel.trySend(data)
            }
        }
    }

    fun stop() {
        readJob?.cancel()
    }

    suspend fun initialize() {
        // Reset
        sendCommand("ATZ")
        delay(1000)
        
        // Echo off
        sendCommand("ATE0")
        
        // Linefeeds off
        sendCommand("ATL0")
        
        // Headers off
        sendCommand("ATH0")
        
        // Spaces off
        sendCommand("ATS0")
        
        // Protocol Auto
        sendCommand("ATSP0")
        delay(1000)
    }

    suspend fun getBanner(): String {
        return sendCommand("ATI")
    }

    suspend fun getProtocol(): String {
        return sendCommand("ATDP")
    }

    suspend fun querySupportedPids(): String {
        return requestPid("01", "00")
    }

    suspend fun requestPid(mode: String, pid: String): String {
        val response = sendCommand("$mode$pid")
        return parseResponse(response, mode, pid)
    }

    private suspend fun sendCommand(command: String): String {
        if (transport.connectionState.value != ObdTransportState.CONNECTED) {
            throw IllegalStateException("Transport not connected")
        }
        
        // Drain any stale data in the channel before sending the next command
        while (readChannel.tryReceive().isSuccess) { }

        val commandBytes = "$command\r".encodeToByteArray()
        transport.write(commandBytes)

        val responseBuilder = StringBuilder()
        withTimeout(5000L) { // 5s timeout for ELM responses
            while (isActive) {
                val data = readChannel.receive()
                val chunk = data.decodeToString()
                responseBuilder.append(chunk)
                if (chunk.contains(">")) { // Prompt indicates end of response
                    break
                }
            }
        }
        
        val cleanedResponse = responseBuilder.toString()
            .replace(">", "")
            .replace("\r", "")
            .replace("\n", "")
            .replace("SEARCHING...", "")
            .trim()
            
        logInteraction(command, cleanedResponse)
        return cleanedResponse
    }

    private fun parseResponse(raw: String, mode: String, pid: String): String {
        if (raw.contains("NODATA", ignoreCase = true)) return ""
        if (raw.contains("ERROR", ignoreCase = true)) return ""
        
        val expectedPrefix = "${(mode.toInt(16) + 0x40).toString(16).padStart(2, '0')}$pid".uppercase()
        val cleanRaw = raw.uppercase().replace(" ", "")
        
        // Handle multiple ECUs responding. For simplicity, take the first valid response line
        val lines = cleanRaw.split("\r", "\n").filter { it.isNotBlank() }
        for (line in lines) {
            if (line.startsWith(expectedPrefix)) {
                return line.substring(expectedPrefix.length)
            }
        }
        return ""
    }
}
