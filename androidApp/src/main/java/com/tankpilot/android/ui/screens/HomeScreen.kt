package com.tankpilot.android.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tankpilot.android.ui.components.VerticalFuelTank
import com.tankpilot.android.ui.vehicletwin.VehicleTwinView
import com.tankpilot.android.ui.theme.*
import com.tankpilot.core.ConfidenceLevel
import com.tankpilot.vehicle.domain.Vehicle

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    vehicle: Vehicle,
    remainingGallons: Double,
    safeRangeMiles: Double,
    confidencePercent: Int,
    confidenceLevel: ConfidenceLevel,
    onFilledUpClick: () -> Unit,
    onLogTripClick: () -> Unit,
    onFuelRescueClick: () -> Unit,
    onSetupGarageClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onDeveloperObdClick: () -> Unit,
    onTestLabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isConfidenceExpanded by remember { mutableStateOf(false) }

    // Fuel Percentage
    val fuelPercent = (remainingGallons / vehicle.tankCapacity).toFloat().coerceIn(0f, 1f)

    // Alert Status
    val isCritical = remainingGallons <= vehicle.reserveFuelGallons
    val isLow = remainingGallons / vehicle.tankCapacity <= vehicle.lowFuelThresholdPercent && !isCritical

    // Single Haptic Cue on Entering Critical State
    var hasTriggeredCriticalHaptic by remember { mutableStateOf(false) }
    LaunchedEffect(isCritical) {
        if (isCritical && !hasTriggeredCriticalHaptic) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(200)
                }
            }
            hasTriggeredCriticalHaptic = true
        } else if (!isCritical) {
            hasTriggeredCriticalHaptic = false
        }
    }

    // Determine Theme Alert Color
    val alertColor = when {
        isCritical -> FuelRed
        isLow -> FuelYellow
        else -> GrayBorder
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${vehicle.year} ${vehicle.make.uppercase()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = GraySecondary
                )
                Text(
                    text = "[ ${vehicle.model} ${vehicle.trim ?: ""} ]",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
            Row {
                TextButton(onClick = onTestLabClick) {
                    Text("TEST LAB", color = FuelYellow, fontSize = 10.sp)
                }
                TextButton(onClick = onDeveloperObdClick) {
                    Text("OBD", color = FuelRed, fontSize = 10.sp)
                }
                IconButton(
                    onClick = onSetupGarageClick,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Vehicle", tint = White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Digital Twin Chassis View
        VehicleTwinView(
            fuelPercentage = fuelPercent,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main cluster layout: Vertical Tank next to Large stats
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // A. The Glass Fluid Gauge
            VerticalFuelTank(
                percentage = fuelPercent,
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
            )

            // B. Stats Display
            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "FUEL ESTIMATE",
                    style = MaterialTheme.typography.labelLarge,
                    color = GraySecondary
                )
                Text(
                    text = "${"%.1f".format(remainingGallons)} GAL",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SAFE RANGE",
                    style = MaterialTheme.typography.labelLarge,
                    color = GraySecondary
                )
                Text(
                    text = "${safeRangeMiles.toInt()} MI",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = alertColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tappable Confidence Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isConfidenceExpanded = !isConfidenceExpanded }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CONFIDENCE", style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = "$confidencePercent%",
                                fontWeight = FontWeight.Bold,
                                color = when (confidenceLevel) {
                                    ConfidenceLevel.VERY_HIGH, ConfidenceLevel.HIGH -> FuelGreen
                                    ConfidenceLevel.MEDIUM -> FuelYellow
                                    ConfidenceLevel.LOW -> FuelRed
                                }
                            )
                        }
                        
                        AnimatedVisibility(visible = isConfidenceExpanded) {
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Divider(color = GrayBorder)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Last Calibration: ${if (confidenceLevel >= ConfidenceLevel.HIGH) "Confirmed" else "Needs full tank"}", fontSize = 11.sp, color = GraySecondary)
                                Text("• GPS Consistency: 98%", fontSize = 11.sp, color = GraySecondary)
                                Text("• Active Calibration runs: ${if (confidenceLevel == ConfidenceLevel.VERY_HIGH) "3+" else "0-2"}", fontSize = 11.sp, color = GraySecondary)
                                Text("• Model status: Learned", fontSize = 11.sp, color = GraySecondary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fuel Rescue Prompt Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, alertColor, RoundedCornerShape(12.dp))
                .clickable { onFuelRescueClick() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FUEL RESCUE STATUS",
                    style = MaterialTheme.typography.labelLarge,
                    color = alertColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val rescueText = when {
                    isCritical -> "Critical fuel estimate — nearest safe station is 1.1 miles away"
                    isLow -> "Low fuel — 3 safe stations nearby"
                    else -> "Fuel Rescue is ready"
                }
                Text(
                    text = rescueText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
                Text(
                    text = "Tap to search compatible stations along your route.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = GraySecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onDashboardClick,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Drive", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onFilledUpClick,
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = DarkBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(56.dp)
            ) {
                Text("Filled Up", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
