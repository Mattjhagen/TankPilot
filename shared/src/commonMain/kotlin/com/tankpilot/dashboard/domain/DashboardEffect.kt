package com.tankpilot.dashboard.domain

sealed interface DashboardEffect {
    data object CriticalFuelEntered : DashboardEffect
    data object ObdConnected : DashboardEffect
    data object TripCompleted : DashboardEffect
    data object FuelRescueActivated : DashboardEffect
}
