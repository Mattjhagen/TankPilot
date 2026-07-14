package com.tankpilot.telemetry.data

object ObdParser {

    /**
     * Cleans raw OBD response from ELM327 by removing spaces, prompts, and newlines.
     */
    fun cleanResponse(raw: String): String {
        return raw.replace(" ", "")
            .replace("\r", "")
            .replace("\n", "")
            .replace(">", "")
            .trim()
            .uppercase()
    }

    /**
     * Helper to verify if response matches expected Mode 1 PID response header (e.g., "410C")
     */
    private fun getPayload(cleaned: String, pidHex: String): String? {
        val expectedHeader = "41$pidHex"
        val index = cleaned.indexOf(expectedHeader)
        if (index == -1) return null
        return cleaned.substring(index + expectedHeader.length)
    }

    /**
     * PID 0C: Engine RPM (returns value in RPM)
     * Formula: ((A * 256) + B) / 4
     */
    fun parseRpm(raw: String): Double? {
        val clean = cleanResponse(raw)
        if (clean.contains("NODATA") || clean.contains("ERROR")) return null
        val payload = getPayload(clean, "0C") ?: return null
        if (payload.length < 4) return null
        
        return try {
            val a = payload.substring(0, 2).toInt(16)
            val b = payload.substring(2, 4).toInt(16)
            ((a * 256) + b) / 4.0
        } catch (e: Exception) {
            null
        }
    }

    /**
     * PID 0D: Vehicle Speed (returns speed in km/h)
     * Formula: A
     */
    fun parseSpeed(raw: String): Double? {
        val clean = cleanResponse(raw)
        if (clean.contains("NODATA") || clean.contains("ERROR")) return null
        val payload = getPayload(clean, "0D") ?: return null
        if (payload.length < 2) return null
        
        return try {
            payload.substring(0, 2).toInt(16).toDouble()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * PID 05: Coolant Temp (returns temp in Celsius)
     * Formula: A - 40
     */
    fun parseCoolantTemp(raw: String): Double? {
        val clean = cleanResponse(raw)
        if (clean.contains("NODATA") || clean.contains("ERROR")) return null
        val payload = getPayload(clean, "05") ?: return null
        if (payload.length < 2) return null
        
        return try {
            payload.substring(0, 2).toInt(16) - 40.0
        } catch (e: Exception) {
            null
        }
    }

    /**
     * PID 04: Engine Load (returns load percent 0-100%)
     * Formula: A * 100 / 255
     */
    fun parseEngineLoad(raw: String): Double? {
        val clean = cleanResponse(raw)
        if (clean.contains("NODATA") || clean.contains("ERROR")) return null
        val payload = getPayload(clean, "04") ?: return null
        if (payload.length < 2) return null
        
        return try {
            (payload.substring(0, 2).toInt(16) * 100.0) / 255.0
        } catch (e: Exception) {
            null
        }
    }

    /**
     * PID 10: MAF Air Flow Rate (returns flow in grams/sec)
     * Formula: ((A * 256) + B) / 100
     */
    fun parseMaf(raw: String): Double? {
        val clean = cleanResponse(raw)
        if (clean.contains("NODATA") || clean.contains("ERROR")) return null
        val payload = getPayload(clean, "10") ?: return null
        if (payload.length < 4) return null
        
        return try {
            val a = payload.substring(0, 2).toInt(16)
            val b = payload.substring(2, 4).toInt(16)
            ((a * 256) + b) / 100.0
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parses VIN from Mode 9 PID 02 response.
     * Raw returns ASCII characters representing the VIN.
     */
    fun parseVin(raw: String): String? {
        val clean = cleanResponse(raw)
        if (clean.contains("NODATA") || clean.contains("ERROR")) return null
        val headerIndex = clean.indexOf("4902")
        if (headerIndex == -1) return null
        
        // Remove header and prefix bytes (e.g. 01 02 line indices from ELM)
        val payload = clean.substring(headerIndex + 4)
        val sb = StringBuilder()
        
        var i = 0
        while (i < payload.length - 1) {
            val hexPair = payload.substring(i, i + 2)
            // Skip line control bytes if present (e.g. 01, 02 line count headers in OBD multiline)
            if (hexPair == "01" || hexPair == "02" || hexPair == "03" || hexPair == "04") {
                i += 2
                continue
            }
            try {
                val charCode = hexPair.toInt(16)
                if (charCode in 32..126) { // printable ascii range
                    sb.append(charCode.toChar())
                }
            } catch (e: Exception) {
                // skip
            }
            i += 2
        }
        
        val vin = sb.toString().trim()
        return if (vin.length == 17) vin else null
    }
}
