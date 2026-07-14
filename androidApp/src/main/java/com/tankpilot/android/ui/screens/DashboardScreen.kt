package com.tankpilot.android.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.tankpilot.android.ui.components.DashboardCard
import com.tankpilot.android.ui.components.SpeedometerText
import com.tankpilot.android.ui.components.VehicleTwin
import com.tankpilot.dashboard.domain.DashboardUiState

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onExit: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11))
            .padding(24.dp)
    ) {
        if (isLandscape) {
            DashboardLandscapeLayout(uiState)
        } else {
            DashboardPortraitLayout(uiState)
        }

        // Only allow exit if speed is low, or if the user forces it (for this phase, we allow it anywhere as a button, but hide it if speed > 5mph ideally)
        val speed = uiState.speed.speedKmh ?: 0
        if (speed < 8) { // 5 mph roughly
            Button(
                onClick = onExit,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text("End Drive")
            }
        }
    }
}

@Composable
fun DashboardLandscapeLayout(uiState: DashboardUiState) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left Column: Speed and Twin
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SpeedometerText(
                speed = uiState.speed.speedKmh ?: 0,
                unit = if (uiState.speed.speedKmh != null) "KM/H" else "--"
            )
            VehicleTwin(
                fuelPercentage = ((uiState.fuelRemaining.gallons ?: 0.0) / 18.0).toFloat(), // hardcoded max for now
                modifier = Modifier.size(240.dp).padding(top = 16.dp)
            )
        }

        // Right Column: Stats
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SAFE RANGE", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                Text("${uiState.safeRange.miles ?: "--"} mi", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("FUEL", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                Text("${uiState.fuelRemaining.gallons?.let { String.format("%.1f", it) } ?: "--"} gal", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("CONFIDENCE", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                Text("${uiState.confidence.percent ?: "--"}%", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            }
            
            // Secondary metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    title = "RPM",
                    value = uiState.rpm?.value ?: "---",
                    unit = "rpm",
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Coolant",
                    value = uiState.coolantTemperature?.value ?: "---",
                    unit = "°C",
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Trip",
                    value = uiState.tripTime.formatted,
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DashboardPortraitLayout(uiState: DashboardUiState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top section: speed
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            SpeedometerText(
                speed = uiState.speed.speedKmh ?: 0,
                unit = if (uiState.speed.speedKmh != null) "KM/H" else "--"
            )
        }

        // Center: Vehicle Twin Focal Point
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            VehicleTwin(
                fuelPercentage = ((uiState.fuelRemaining.gallons ?: 0.0) / 18.0).toFloat(),
                modifier = Modifier.size(360.dp)
            )
        }

        // Bottom section: Stats cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardCard(
                title = "Range",
                value = uiState.safeRange.miles?.toString() ?: "--",
                unit = "mi",
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            DashboardCard(
                title = "Fuel",
                value = uiState.fuelRemaining.gallons?.let { String.format("%.1f", it) } ?: "--",
                unit = "gal",
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            DashboardCard(
                title = "Temp",
                value = uiState.ambientTemperature?.value ?: "--",
                unit = "°C",
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }
    }
}
