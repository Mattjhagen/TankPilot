package com.tankpilot.dashboard.domain

data class SpeedDisplay(val speedKmh: Int?, val source: SpeedSource)

enum class DashboardMode {
    INACTIVE,
    ACTIVE,
    COOLDOWN,
    CONFIRMATION_REQUIRED
}

enum class DashboardTheme {
    ADAPTIVE,
    DAY,
    NIGHT
}

enum class VehicleTwinState {
    PARKED,
    MOVING,
    CONNECTED_IDLE
}

data class FuelDisplay(val gallons: Double?, val isLow: Boolean, val isCritical: Boolean)
data class RangeDisplay(val miles: Int?)
data class ConfidenceDisplay(val percent: Int?, val level: com.tankpilot.core.ConfidenceLevel?)

data class MetricDisplay(val value: String?, val unit: String)
data class HeadingDisplay(val degrees: Int, val direction: String)
data class DurationDisplay(val formatted: String)
data class DistanceDisplay(val formatted: String)

enum class TelemetryStatusDisplay {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    STALE
}

enum class DashboardWarning {
    NONE,
    LOW_FUEL,
    CRITICAL_FUEL,
    NO_SAFE_STATION,
    TELEMETRY_FAILURE,
    MAINTENANCE_DUE
}

data class DashboardUiState(
    val dashboardMode: DashboardMode = DashboardMode.INACTIVE,
    val isFocusModeEnabled: Boolean = false,
    val theme: DashboardTheme = DashboardTheme.ADAPTIVE,
    val speed: SpeedDisplay = SpeedDisplay(null, SpeedSource.UNKNOWN),
    val digitalTwin: VehicleTwinState = VehicleTwinState.PARKED,
    val fuelRemaining: FuelDisplay = FuelDisplay(null, false, false),
    val safeRange: RangeDisplay = RangeDisplay(null),
    val confidence: ConfidenceDisplay = ConfidenceDisplay(null, null),
    val rpm: MetricDisplay? = null,
    val coolantTemperature: MetricDisplay? = null,
    val batteryVoltage: MetricDisplay? = null,
    val engineLoad: MetricDisplay? = null,
    val ambientTemperature: MetricDisplay? = null,
    val heading: HeadingDisplay? = null,
    val tripTime: DurationDisplay = DurationDisplay("0:00"),
    val tripDistance: DistanceDisplay = DistanceDisplay("0.0 mi"),
    val telemetryStatus: TelemetryStatusDisplay = TelemetryStatusDisplay.DISCONNECTED,
    val warnings: List<DashboardWarning> = emptyList(),
    val isReducedMotionEnabled: Boolean = false
)
