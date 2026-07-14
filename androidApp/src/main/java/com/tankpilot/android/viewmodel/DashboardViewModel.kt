package com.tankpilot.android.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tankpilot.dashboard.domain.*
import com.tankpilot.telemetry.domain.VehicleTelemetryProvider
import com.tankpilot.trip.domain.TripSessionProvider
import com.tankpilot.trip.domain.TripEndReason
import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.telemetry.domain.AmbientTemperatureProvider
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.android.managers.HapticManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val telemetryProvider: VehicleTelemetryProvider,
    private val tripSessionProvider: TripSessionProvider,
    private val headingProvider: HeadingProvider,
    private val ambientTemperatureProvider: AmbientTemperatureProvider,
    private val dashboardActivationCoordinator: DashboardActivationCoordinator,
    private val fuelStateUseCase: FuelStateUseCase,
    private val hapticManager: HapticManager,
    private val clock: com.tankpilot.core.AppClock
) : ViewModel() {

    private val speedSmoother = SpeedSmoother()

    private val _isFocusModeEnabled = MutableStateFlow(false)
    private val _theme = MutableStateFlow(DashboardTheme.ADAPTIVE)

    // replay = 0: effects are one-shot. Rotation or process-restore does NOT replay them.
    // extraBufferCapacity = 1: prevents suspend if no collector is attached during brief rotation gaps.
    private val _effects = MutableSharedFlow<DashboardEffect>(replay = 0, extraBufferCapacity = 1)
    val effects: SharedFlow<DashboardEffect> = _effects.asSharedFlow()

    private var wasCritical = false
    private var wasActive = false

    init {
        // Restore session state
        savedStateHandle.get<String>("dashboard_session_state")?.let { json ->
            try {
                val sessionState = Json.decodeFromString<DashboardSessionState>(json)
                dashboardActivationCoordinator.restoreState(sessionState)
                _isFocusModeEnabled.value = sessionState.isFocusModeEnabled
                _theme.value = sessionState.theme
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }

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
        fuelStateUseCase.currentVehicle,
        _isFocusModeEnabled,
        _theme
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
        val focusMode = args[11] as Boolean
        val activeTheme = args[12] as DashboardTheme

        if (mode == DashboardMode.ACTIVE && !wasActive) {
            // Can be handled as DashboardEffect if we want, but currently using manual flow below
        }
        wasActive = (mode == DashboardMode.ACTIVE)

        val rawSpeed = telemetry.speedKmh
        val speedDisplay = speedSmoother.filter(rawSpeed, if (rawSpeed != null) SpeedSource.OBD else SpeedSource.UNKNOWN)

        val tankCap = vehicle?.tankCapacity ?: 1.0
        val fuelPercent = fuel.value / tankCap
        val isLow = fuelPercent <= 0.15 && fuelPercent > 0.05
        val isCritical = fuelPercent <= 0.05
        
        if (isCritical && !wasCritical) {
            viewModelScope.launch {
                _effects.emit(DashboardEffect.CriticalFuelEntered)
            }
        }
        wasCritical = isCritical

        val warnings = mutableListOf<DashboardWarning>()
        if (isCritical) warnings.add(DashboardWarning.CRITICAL_FUEL)
        else if (isLow) warnings.add(DashboardWarning.LOW_FUEL)

        val state = DashboardUiState(
            dashboardMode = mode,
            isFocusModeEnabled = focusMode,
            theme = activeTheme,
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

        // Save session state
        val sessionState = DashboardSessionState(
            tripId = null,
            isVisible = mode == DashboardMode.ACTIVE || mode == DashboardMode.CONFIRMATION_REQUIRED,
            isTripActive = tripTime.inWholeSeconds > 0,
            enteredAutomatically = false, // Not fully tracked yet, assume false for now
            startTimeEpochMs = 0L,
            lastActivityTimestamp = clock.now().toEpochMilliseconds(),
            lastReliableTelemetryTimestamp = clock.now().toEpochMilliseconds(),
            isFocusModeEnabled = focusMode,
            theme = activeTheme
        )
        savedStateHandle["dashboard_session_state"] = Json.encodeToString(sessionState)

        state
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun toggleFocusMode() {
        _isFocusModeEnabled.value = !_isFocusModeEnabled.value
    }

    fun manualEnter() {
        dashboardActivationCoordinator.manualEnter()
    }

    fun manualExit() {
        dashboardActivationCoordinator.manualExit()
    }

    fun confirmRestore() {
        dashboardActivationCoordinator.confirmRestore()
    }

    /**
     * "End Previous Trip" — dismisses the resume dialog, then ends the in-progress
     * trip session so the repository marks it ENDED and the elapsed timer resets.
     * Trip history is preserved; nothing is deleted.
     */
    fun endPreviousTripAndDismiss() {
        dashboardActivationCoordinator.dismissRestore()
        viewModelScope.launch {
            tripSessionProvider.endTrip(TripEndReason.MANUAL)
        }
    }

    /**
     * "Dismiss" — leaves the previous trip session in whatever state it was in
     * (ACTIVE or PAUSED). Dashboard does not reopen. Trip data is preserved.
     */
    fun dismissRestore() {
        dashboardActivationCoordinator.dismissRestore()
    }

    /** The session state that prompted CONFIRMATION_REQUIRED, for display in the dialog. */
    val pendingSessionState: StateFlow<DashboardSessionState?> =
        dashboardActivationCoordinator.pendingSessionState

    override fun onCleared() {
        super.onCleared()
        speedSmoother.reset()
    }
}
