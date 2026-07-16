package com.tankpilot.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.tankpilot.android.ui.screens.HomeScreen
import com.tankpilot.android.ui.screens.FillUpScreen
import com.tankpilot.android.ui.screens.FuelRescueScreen
import com.tankpilot.android.ui.screens.VehicleSetupScreen
import com.tankpilot.android.ui.theme.TankPilotTheme
import com.tankpilot.android.ui.theme.DarkBg
import com.tankpilot.android.viewmodel.MainViewModel
import com.tankpilot.android.viewmodel.DashboardViewModel
import com.tankpilot.android.ui.screens.DashboardScreen
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build

enum class Screen {
    SETUP,
    HOME,
    FILL_UP,
    FUEL_RESCUE,
    DASHBOARD,
    DEVELOPER_OBD,
    TEST_LAB
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()
    private val dashboardViewModel: DashboardViewModel by viewModel()

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineLocationGranted || coarseLocationGranted) {
            dashboardViewModel.startTracking()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TankPilotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBg
                ) {
                    val vehicles by viewModel.vehicles.collectAsState()
                    val currentVehicle by viewModel.currentVehicle.collectAsState()
                    val trips by viewModel.trips.collectAsState()
                    val fillUps by viewModel.fillUps.collectAsState()
                    
                    val fuelRemaining by viewModel.estimatedFuelRemaining.collectAsState()
                    val safeRange by viewModel.safeRange.collectAsState()
                    val confidenceLevel by viewModel.confidence.collectAsState()
                    val confidencePercent by viewModel.confidencePercent.collectAsState()
                    
                    val recommendations by viewModel.recommendations.collectAsState()
                    val isRefreshingRescue by viewModel.isRefreshingRescue.collectAsState()

                    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
                    val dashboardEffects = dashboardViewModel.effects
                    val pendingSessionState by dashboardViewModel.pendingSessionState.collectAsState()
                    val hapticManager = remember { com.tankpilot.android.managers.HapticManager(this@MainActivity) }

                    var currentScreen by remember { mutableStateOf(Screen.HOME) }

                    // Determine Screen state
                    LaunchedEffect(vehicles, currentVehicle) {
                        if (vehicles.isEmpty()) {
                            currentScreen = Screen.SETUP
                        } else if (currentScreen == Screen.SETUP) {
                            currentScreen = Screen.HOME
                        }
                    }

                    // Simple mock location (Impala City)
                    val mockLatitude = 37.7749
                    val mockLongitude = -122.4194

                    // Launch auto-refresh for Fuel Rescue on start
                    LaunchedEffect(currentVehicle) {
                        if (currentVehicle != null) {
                            viewModel.refreshRescue(mockLatitude, mockLongitude, false)
                        }
                    }

                    // Auto-trigger Dashboard screen if ACTIVE or CONFIRMATION_REQUIRED
                    LaunchedEffect(dashboardUiState.dashboardMode) {
                        when (dashboardUiState.dashboardMode) {
                            com.tankpilot.dashboard.domain.DashboardMode.ACTIVE,
                            com.tankpilot.dashboard.domain.DashboardMode.CONFIRMATION_REQUIRED -> {
                                if (currentScreen != Screen.DASHBOARD) currentScreen = Screen.DASHBOARD
                            }
                            else -> {
                                if (currentScreen == Screen.DASHBOARD) currentScreen = Screen.HOME
                            }
                        }
                    }

                    LaunchedEffect(dashboardEffects) {
                        dashboardEffects.collect { effect ->
                            when (effect) {
                                is com.tankpilot.dashboard.domain.DashboardEffect.CriticalFuelEntered -> {
                                    hapticManager.playCriticalWarning()
                                }
                                else -> {} // Implement other effects later
                            }
                        }
                    }

                    when (currentScreen) {
                        Screen.SETUP -> {
                            VehicleSetupScreen(
                                onVehicleCreated = { year, make, model, trim, color, engine, disp, cyls, cap, city, hwy, fuel, grade, units, res, thresh ->
                                    viewModel.createVehicle(
                                        year, make, model, trim, color, engine, disp, cyls, cap, city, hwy, fuel, grade, units, res, thresh
                                    )
                                    currentScreen = Screen.HOME
                                }
                            )
                        }
                        Screen.HOME -> {
                            currentVehicle?.let { vehicle ->
                                HomeScreen(
                                    vehicle = vehicle,
                                    remainingGallons = fuelRemaining.value,
                                    safeRangeMiles = safeRange.value,
                                    confidencePercent = confidencePercent,
                                    confidenceLevel = confidenceLevel,
                                    onFilledUpClick = { currentScreen = Screen.FILL_UP },
                                    onLogTripClick = {
                                        // Mock log a trip (distance 15 miles, 20 mins drive, 1 min idle, CITY type)
                                        viewModel.logTrip(
                                            distance = 15.0,
                                            durationSeconds = 1200L,
                                            idleTimeSeconds = 60L,
                                            type = com.tankpilot.trip.domain.DrivingType.CITY
                                        )
                                    },
                                    onFuelRescueClick = {
                                        viewModel.refreshRescue(mockLatitude, mockLongitude, false)
                                        currentScreen = Screen.FUEL_RESCUE
                                    },
                                    onSetupGarageClick = { currentScreen = Screen.SETUP },
                                    onDashboardClick = { dashboardViewModel.manualEnter() },
                                    onDeveloperObdClick = { currentScreen = Screen.DEVELOPER_OBD },
                                    onTestLabClick = { currentScreen = Screen.TEST_LAB }
                                )
                            }
                        }
                        Screen.FILL_UP -> {
                            FillUpScreen(
                                onConfirmClick = { gallons, price, odometer, isFull ->
                                    viewModel.logFillUp(gallons, price, odometer, isFull)
                                    currentScreen = Screen.HOME
                                },
                                onCancelClick = { currentScreen = Screen.HOME }
                            )
                        }
                        Screen.FUEL_RESCUE -> {
                            FuelRescueScreen(
                                recommendations = recommendations,
                                isRefreshing = isRefreshingRescue,
                                onRefreshClick = { viewModel.refreshRescue(mockLatitude, mockLongitude, true) },
                                onBackClick = { currentScreen = Screen.HOME }
                            )
                        }
                        Screen.DASHBOARD -> {
                            DashboardScreen(
                                uiState = dashboardUiState,
                                pendingSessionState = pendingSessionState,
                                vehicleName = currentVehicle?.let { "${it.year} ${it.make} ${it.model}" },
                                onToggleFocusMode = { dashboardViewModel.toggleFocusMode() },
                                onExit = {
                                    dashboardViewModel.manualExit()
                                    dashboardViewModel.stopTracking()
                                },
                                onConfirmRestore = { dashboardViewModel.confirmRestore() },
                                onEndPreviousTrip = { dashboardViewModel.endPreviousTripAndDismiss() },
                                onDismissRestore = { dashboardViewModel.dismissRestore() },
                                onStartDriveRequest = {
                                    val perms = mutableListOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        perms.add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    requestLocationPermissionLauncher.launch(perms.toTypedArray())
                                }
                            )
                        }
                        Screen.DEVELOPER_OBD -> {
                            com.tankpilot.android.ui.screens.DeveloperObdScreen()
                        }
                        Screen.TEST_LAB -> {
                            DebugScreenHost(onNavigateBack = { currentScreen = Screen.HOME })
                        }
                    }
                }
            }
        }
    }
}
