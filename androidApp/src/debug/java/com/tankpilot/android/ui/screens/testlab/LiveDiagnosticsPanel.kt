package com.tankpilot.android.ui.screens.testlab

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.tankpilot.android.auto.AndroidAutoVisibilityState
import com.tankpilot.android.managers.DrivingTrackingCoordinator
import com.tankpilot.fuel.domain.FuelModelUseCase
import com.tankpilot.trip.domain.DrivingSessionCoordinator
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import org.koin.compose.koinInject

private data class DiagnosticRow(val label: String, val value: String)

/**
 * Debug-only live diagnostics — reads the same production singletons the phone
 * Dashboard and Android Auto read (DrivingSessionCoordinator, DrivingTrackingCoordinator,
 * FuelModelUseCase). Everything shown here is real/live state, not a fixture — Test Lab's
 * existing scenario fixtures are a separate, clearly-labeled section of this screen.
 * Excluded from release builds: this file lives under androidApp/src/debug, and
 * DebugScreenHost's release variant never navigates here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDiagnosticsPanel(onBack: () -> Unit) {
    val context = LocalContext.current
    val drivingSessionCoordinator = koinInject<DrivingSessionCoordinator>()
    val drivingTrackingCoordinator = koinInject<DrivingTrackingCoordinator>()
    val fuelModelUseCase = koinInject<FuelModelUseCase>()

    val sessionState by drivingSessionCoordinator.sessionState.collectAsState()
    val validatedLocation by drivingSessionCoordinator.locationPipeline.validatedLocation.collectAsState()
    val isTracking by drivingTrackingCoordinator.isTracking.collectAsState()
    val trackingStatus by drivingTrackingCoordinator.trackingStatus.collectAsState()
    val displayedFuelRemaining by fuelModelUseCase.displayedFuelRemainingGallons.collectAsState()
    val isAutoVisible by AndroidAutoVisibilityState.isVisible.collectAsState()

    // Tick once a second so "sample age" keeps counting up even with no new samples.
    var tick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            tick++
        }
    }

    val fineLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarseLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val notificationPermissionLabel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) "Granted" else "Denied"
    } else {
        "N/A (API < 33)"
    }
    val locationManager = context.getSystemService(LocationManager::class.java)
    val locationServicesEnabled = LocationManagerCompat.isLocationEnabled(locationManager)

    val sampleAgeLabel = remember(validatedLocation, tick) {
        val sample = validatedLocation
        if (sample == null) {
            "No sample yet"
        } else {
            val ageSeconds = Clock.System.now().epochSeconds - sample.timestamp.epochSeconds
            "${ageSeconds}s ago"
        }
    }

    val rows = listOf(
        DiagnosticRow("Location permission", if (fineLocationGranted) "Fine granted" else if (coarseLocationGranted) "Coarse only" else "Denied"),
        DiagnosticRow("Notification permission", notificationPermissionLabel),
        DiagnosticRow("Location services", if (locationServicesEnabled) "Enabled" else "Disabled"),
        DiagnosticRow("Selected provider", drivingTrackingCoordinator.selectedProviderLabel),
        DiagnosticRow("Foreground service", if (isTracking) "Running" else "Stopped"),
        DiagnosticRow("Tracking error", trackingStatus?.name ?: "None"),
        DiagnosticRow("Latest accepted sample age", sampleAgeLabel),
        DiagnosticRow("Selected speed", sessionState.selectedSpeed.valueKmh?.let { "%.1f km/h".format(it) } ?: "Unavailable"),
        DiagnosticRow("Speed source", sessionState.selectedSpeed.source.name),
        DiagnosticRow("Trip state", sessionState.activeTripState.name),
        DiagnosticRow("Driving pattern", sessionState.drivingPattern.name),
        DiagnosticRow("MPG", sessionState.mpgEstimate.value?.let { "%.1f".format(it) } ?: "Unavailable"),
        DiagnosticRow("MPG provenance", sessionState.mpgEstimate.source.name),
        DiagnosticRow("Active fuel burn", "%.2f gal".format(sessionState.activeFuelBurn)),
        DiagnosticRow("Displayed fuel remaining", "%.2f gal".format(displayedFuelRemaining)),
        DiagnosticRow("Active-session persistence", if (sessionState.tripId != null) "Active (tripId=${sessionState.tripId})" else "None"),
        DiagnosticRow("Android Auto session", if (isAutoVisible) "Visible" else "Not visible")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Diagnostics") },
                navigationIcon = {
                    Button(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(
                "All values below are LIVE production state — none of it is a Mock fixture.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn {
                items(rows) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(row.label, style = MaterialTheme.typography.bodyMedium)
                        Text(row.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Divider()
                }
            }
        }
    }
}
