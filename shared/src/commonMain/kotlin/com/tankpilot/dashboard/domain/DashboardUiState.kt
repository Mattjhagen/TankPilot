package com.tankpilot.dashboard.domain

import com.tankpilot.trip.domain.DrivingType
import com.tankpilot.location.domain.TrackingUnavailableReason

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

enum class FuelAlertLevel {
    NORMAL,
    LOW,
    CRITICAL,
    EMPTY_IMMINENT
}

data class FuelDisplay(
    val gallons: Double?,
    val isLow: Boolean,
    val isCritical: Boolean,
    val tankCapacityGallons: Double? = null,
    val fuelPercent: Double? = null
)
data class RangeDisplay(val miles: Int?)
data class ConfidenceDisplay(val percent: Int?, val level: com.tankpilot.core.ConfidenceLevel?)

data class MetricDisplay(val value: String?, val unit: String)
data class HeadingDisplay(val degrees: Int, val direction: String)
data class DurationDisplay(val formatted: String)
data class DistanceDisplay(val formatted: String)
data class MpgDisplay(
    val instant: Double?,
    val tripAverage: Double?,
    val rolling30s: Double?,
    val provenance: com.tankpilot.fuel.MpgProvenance = com.tankpilot.fuel.MpgProvenance.UNKNOWN
)
data class NearestStationDisplay(
    val name: String,
    val distanceMiles: Double,
    val pricePerGallon: Double?,
    val estimatedSavings: Double?
)

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
    val isReducedMotionEnabled: Boolean = false,
    // ── New fields for MPG, driving type, and fuel alerts ──
    val mpg: MpgDisplay = MpgDisplay(null, null, null),
    val drivingType: DrivingType = DrivingType.MIXED,
    val fuelAlertLevel: FuelAlertLevel = FuelAlertLevel.NORMAL,
    val estimatedMilesToEmpty: RangeDisplay = RangeDisplay(null),
    val nearestCheapStation: NearestStationDisplay? = null,
    val isTrackingActive: Boolean = false,
    val trackingError: TrackingUnavailableReason? = null,
    val alertText: String = ""
)
