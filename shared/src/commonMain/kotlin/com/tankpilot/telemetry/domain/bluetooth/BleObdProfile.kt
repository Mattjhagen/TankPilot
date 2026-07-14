package com.tankpilot.telemetry.domain.bluetooth

enum class BleWriteType {
    DEFAULT,
    NO_RESPONSE
}

data class BleObdProfile(
    val id: String,
    val serviceUuids: Set<String>,
    val writeCharacteristicUuids: Set<String>,
    val notifyCharacteristicUuids: Set<String>,
    val writeType: BleWriteType
)

val KnownBleProfiles = listOf(
    // Common UART Service
    BleObdProfile(
        id = "Nordic_UART",
        serviceUuids = setOf("6e400001-b5a3-f393-e0a9-e50e24dcca9e", "FFE0", "0000ffe0-0000-1000-8000-00805f9b34fb"),
        writeCharacteristicUuids = setOf("6e400002-b5a3-f393-e0a9-e50e24dcca9e", "FFE1", "0000ffe1-0000-1000-8000-00805f9b34fb", "FFF2"),
        notifyCharacteristicUuids = setOf("6e400003-b5a3-f393-e0a9-e50e24dcca9e", "FFE1", "0000ffe1-0000-1000-8000-00805f9b34fb", "FFF1"),
        writeType = BleWriteType.NO_RESPONSE
    )
)
