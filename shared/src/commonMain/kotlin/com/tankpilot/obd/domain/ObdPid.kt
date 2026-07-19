package com.tankpilot.obd.domain

enum class ObdPid(val mode: String, val pid: String) {
    ENGINE_LOAD("01", "04"),
    COOLANT_TEMPERATURE("01", "05"),
    ENGINE_RPM("01", "0C"),
    VEHICLE_SPEED("01", "0D"),
    INTAKE_AIR_TEMPERATURE("01", "0F"),
    MASS_AIR_FLOW("01", "10"),
    THROTTLE_POSITION("01", "11"),
    FUEL_LEVEL("01", "2F");
    
    companion object {
        fun fromPidString(pid: String): ObdPid? {
            return entries.find { it.pid == pid }
        }
    }
}
