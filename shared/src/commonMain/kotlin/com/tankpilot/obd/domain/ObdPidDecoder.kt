package com.tankpilot.obd.domain

object ObdPidDecoder {

    /**
     * Decodes standard Mode 01 PID values.
     * Expects a normalized list of hex strings.
     * Returns the parsed double value, or null if unparseable or unsupported.
     */
    fun decode(normalizedLines: List<String>, pid: ObdPid): Double? {
        val validLine = normalizedLines.firstOrNull { it.startsWith("41${pid.pid}", ignoreCase = true) } ?: return null
        val dataHex = validLine.substring(4)
        
        return try {
            when (pid) {
                ObdPid.ENGINE_RPM -> {
                    if (dataHex.length >= 4) {
                        val a = dataHex.substring(0, 2).toInt(16)
                        val b = dataHex.substring(2, 4).toInt(16)
                        ((a * 256.0) + b) / 4.0
                    } else null
                }
                ObdPid.VEHICLE_SPEED -> {
                    if (dataHex.length >= 2) {
                        dataHex.substring(0, 2).toInt(16).toDouble()
                    } else null
                }
                ObdPid.COOLANT_TEMPERATURE -> {
                    if (dataHex.length >= 2) {
                        dataHex.substring(0, 2).toInt(16) - 40.0
                    } else null
                }
                ObdPid.ENGINE_LOAD, ObdPid.THROTTLE_POSITION, ObdPid.FUEL_LEVEL -> {
                    if (dataHex.length >= 2) {
                        dataHex.substring(0, 2).toInt(16) * 100.0 / 255.0
                    } else null
                }
                ObdPid.INTAKE_AIR_TEMPERATURE -> {
                    if (dataHex.length >= 2) {
                        dataHex.substring(0, 2).toInt(16) - 40.0
                    } else null
                }
                ObdPid.MASS_AIR_FLOW -> {
                    if (dataHex.length >= 4) {
                        val a = dataHex.substring(0, 2).toInt(16)
                        val b = dataHex.substring(2, 4).toInt(16)
                        ((a * 256.0) + b) / 100.0
                    } else null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Decodes ATRV battery voltage command (e.g. "12.6V", "14.18 V")
     */
    fun decodeVoltage(normalizedLines: List<String>): Double? {
        val line = normalizedLines.firstOrNull { it.contains("V", ignoreCase = true) } ?: return null
        val numericPart = line.replace(Regex("[^0-9.]"), "")
        val voltage = numericPart.toDoubleOrNull()
        if (voltage != null && voltage in 0.0..30.0) {
            return voltage
        }
        return null
    }
}
