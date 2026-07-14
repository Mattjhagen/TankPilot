package com.tankpilot.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tankpilot.android.ui.components.DashboardCard
import com.tankpilot.android.ui.components.SpeedometerText
import com.tankpilot.android.ui.components.VehicleTwin
import com.tankpilot.android.viewmodel.DashboardViewModel
import com.tankpilot.android.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    mainViewModel: MainViewModel,
    dashboardViewModel: DashboardViewModel,
    onExit: () -> Unit
) {
    val telemetry by dashboardViewModel.telemetryData.collectAsState()
    val fuel by mainViewModel.estimatedFuelRemaining.collectAsState()
    val range by mainViewModel.safeRange.collectAsState()
    val confidencePercent by mainViewModel.confidencePercent.collectAsState()
    val vehicle by mainViewModel.currentVehicle.collectAsState()

    val distance by dashboardViewModel.tripDistanceMiles.collectAsState()
    val duration by dashboardViewModel.tripDurationSeconds.collectAsState()
    val heading by dashboardViewModel.compassHeading.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.startTrip()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11)) // Dark charcoal background
            .padding(24.dp)
    ) {
        // Top section: speed and safe range
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            SpeedometerText(
                speed = telemetry.speedKmh?.toInt() ?: 0,
                unit = "KM/H"
            )
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${range.value.toInt()} mi",
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    text = "SAFE RANGE",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${String.format("%.1f", fuel.value)} gal",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "REMAINING",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Center: Vehicle Twin Focal Point
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            vehicle?.let {
                VehicleTwin(
                    fuelPercentage = (fuel.value / it.tankCapacity).toFloat(),
                    modifier = Modifier.size(360.dp)
                )
            }
        }

        // Bottom section: Stats cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardCard(
                title = "RPM",
                value = telemetry.engineRpm?.toInt()?.toString() ?: "---",
                unit = "rpm",
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            DashboardCard(
                title = "Coolant",
                value = telemetry.coolantTempCelsius?.toInt()?.toString() ?: "---",
                unit = "°C",
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            DashboardCard(
                title = "Battery",
                value = telemetry.batteryVoltage?.let { String.format("%.1f", it) } ?: "---",
                unit = "V",
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            DashboardCard(
                title = "Heading",
                value = heading.toString(),
                unit = "°",
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }
    }
}
