package com.tankpilot.obd.domain

object ElmResponseNormalizer {
    
    fun normalize(rawResponse: String, sentCommand: String): List<String> {
        // Remove spaces, > prompt, and carriage returns/line feeds
        var clean = rawResponse.replace(">", "").trim()
        
        // Remove echo if present
        if (clean.startsWith(sentCommand, ignoreCase = true)) {
            clean = clean.substring(sentCommand.length).trim()
        }
        
        val lines = clean.split('\r', '\n')
            .map { it.replace(" ", "").trim() }
            .filter { it.isNotEmpty() }
            
        return lines.filter { line ->
            !line.equals("NODATA", ignoreCase = true) &&
            !line.equals("UNABLETOCONNECT", ignoreCase = true) &&
            !line.equals("STOPPED", ignoreCase = true) &&
            !line.equals("SEARCHING...", ignoreCase = true) &&
            !line.equals("?", ignoreCase = true)
        }
    }
}
