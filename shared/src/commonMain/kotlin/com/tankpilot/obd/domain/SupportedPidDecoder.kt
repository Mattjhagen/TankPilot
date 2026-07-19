package com.tankpilot.obd.domain

object SupportedPidDecoder {

    /**
     * Decodes the standard 4-byte mask returned by PID 00, 20, 40, 60 queries.
     * Expects a normalized list of hex strings (no spaces, e.g., ["4100BE1FA813"]).
     * 
     * Returns a set of supported PIDs and a boolean indicating if the continuation PID (e.g. 20 for 00) is supported.
     */
    fun decode(normalizedLines: List<String>, basePid: String): Pair<Set<ObdPid>, Boolean> {
        val supported = mutableSetOf<ObdPid>()
        var continuationSupported = false
        val basePidInt = basePid.toIntOrNull(16) ?: return Pair(emptySet(), false)
        
        for (line in normalizedLines) {
            // A valid Mode 01 response starts with "41" + basePid + 8 hex chars (4 bytes)
            if (line.startsWith("41$basePid", ignoreCase = true) && line.length >= 12) {
                val hexMask = line.substring(4, 12)
                val binaryMask = hexMask.map { char ->
                    char.digitToIntOrNull(16)?.toString(2)?.padStart(4, '0') ?: "0000"
                }.joinToString("")
                
                for (i in 0 until 32) {
                    val isSupported = binaryMask[i] == '1'
                    if (isSupported) {
                        val actualPidInt = basePidInt + i + 1
                        val actualPidHex = actualPidInt.toString(16).padStart(2, '0').uppercase()
                        
                        if (i == 31) { // The last bit (PID 20, 40, 60, 80) indicates continuation
                            continuationSupported = true
                        } else {
                            ObdPid.fromPidString(actualPidHex)?.let { supported.add(it) }
                        }
                    }
                }
            }
        }
        
        return Pair(supported, continuationSupported)
    }
}
