package com.tankpilot.trip.domain

sealed class VehicleContext {
    data object Manual : VehicleContext()
    data object CarPlay : VehicleContext()
    data object Bluetooth : VehicleContext()
    data object Obd2 : VehicleContext()
    data object AndroidAuto : VehicleContext()
}
