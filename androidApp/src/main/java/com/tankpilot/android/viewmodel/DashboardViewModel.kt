package com.tankpilot.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tankpilot.dashboard.domain.*
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import com.tankpilot.trip.domain.TripSessionProvider
import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.fuel.domain.FuelStateUseCase
import kotlinx.coroutines.flow.*

class DashboardViewModel(
    private val telemetryProvider: VehicleTelemetryProvider,
    private val tripSessionProvider: TripSessionProvider,
    private val headingProvider: HeadingProvider,
    private val ambientTemperatureProvider: AmbientTemperatureProvider,
    private val dashboardActivationCoordinator: DashboardActivationCoordinator,
    private val fuelStateUseCase: FuelStateUseCase
) : ViewModel() {

    private val speedSmoother = SpeedSmoother()

    init {
        // Feed telemetry and connection state into the coordinator
        telemetryProvider.telemetryFlow.onEach { telemetry ->
            dashboardActivationCoordinator.onTelemetryUpdate(telemetry, true) // mock connection true
        }.launchIn(viewModelScope)

        tripSessionProvider.sessionState.onEach { state ->
            dashboardActivationCoordinator.onTripStateChange(state)
        }.launchIn(viewModelScope)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        dashboardActivationCoordinator.dashboardMode,
        telemetryProvider.telemetryFlow,
        tripSessionProvider.elapsedTime,
        tripSessionProvider.distanceDriven,
        headingProvider.heading,
        ambientTemperatureProvider.temperature,
        fuelStateUseCase.estimatedFuelRemaining,
        fuelStateUseCase.safeRange,
        fuelStateUseCase.confidencePercent,
        fuelStateUseCase.confidence,
        fuelStateUseCase.currentVehicle
    ) { args ->
        val mode = args[0] as DashboardMode
        val telemetry = args[1] as com.tankpilot.telemetry.domain.TelemetryData
        val tripTime = args[2] as kotlin.time.Duration
        val tripDistance = args[3] as com.tankpilot.core.Miles
        val heading = args[4] as? com.tankpilot.location.domain.HeadingSample
        val temp = args[5] as? com.tankpilot.telemetry.domain.TemperatureSample
        val fuel = args[6] as com.tankpilot.core.Gallons
        val range = args[7] as com.tankpilot.core.Miles
        val confPercent = args[8] as Int
        val confLevel = args[9] as com.tankpilot.core.ConfidenceLevel
        val vehicle = args[10] as? com.tankpilot.vehicle.domain.Vehicle

        val rawSpeed = telemetry.speedKmh
        val speedDisplay = speedSmoother.filter(rawSpeed, if (rawSpeed != null) SpeedSource.OBD else SpeedSource.UNKNOWN)

        val tankCap = vehicle?.tankCapacity ?: 1.0
        val fuelPercent = fuel.value / tankCap
        val isLow = fuelPercent <= 0.15 && fuelPercent > 0.05
        val isCritical = fuelPercent <= 0.05

        val warnings = mutableListOf<DashboardWarning>()
        if (isCritical) warnings.add(DashboardWarning.CRITICAL_FUEL)
        else if (isLow) warnings.add(DashboardWarning.LOW_FUEL)

        DashboardUiState(
            dashboardMode = mode,
            speed = speedDisplay,
            digitalTwin = if (rawSpeed != null && rawSpeed > 0) VehicleTwinState.MOVING else VehicleTwinState.PARKED,
            fuelRemaining = FuelDisplay(fuel.value, isLow, isCritical),
            safeRange = RangeDisplay(range.value.toInt()),
            confidence = ConfidenceDisplay(confPercent, confLevel),
            rpm = telemetry.engineRpm?.let { MetricDisplay(it.toInt().toString(), "RPM") },
            coolantTemperature = telemetry.coolantTempCelsius?.let { MetricDisplay(it.toInt().toString(), "°C") },
            batteryVoltage = telemetry.batteryVoltage?.let { MetricDisplay(String.format("%.1f", it), "V") },
            engineLoad = telemetry.engineLoadPercent?.let { MetricDisplay(it.toInt().toString(), "%") },
            ambientTemperature = temp?.let { MetricDisplay(it.celsius.toInt().toString(), "°C") },
            heading = heading?.let { HeadingDisplay(it.degrees.toInt(), it.cardinalDirection) },
            tripTime = DurationDisplay(tripTime.toComponents { hours, minutes, _, _ -> 
                if (hours > 0) "$hours:${minutes.toString().padStart(2, '0')}" else "$minutes min" 
            }),
            tripDistance = DistanceDisplay(String.format("%.1f mi", tripDistance.value)),
            telemetryStatus = TelemetryStatusDisplay.CONNECTED,
            warnings = warnings
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun manualEnter() {
        dashboardActivationCoordinator.manualEnter()
    }

    fun manualExit() {
        dashboardActivationCoordinator.manualExit()
    }

    override fun onCleared() {
        super.onCleared()
        speedSmoother.reset()
    }
}
