package com.tankpilot.android.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tankpilot.android.ui.components.*
import com.tankpilot.android.ui.theme.*
import com.tankpilot.android.ui.vehicletwin.VehicleRenderer
import com.tankpilot.android.ui.vehicletwin.VehicleState
import com.tankpilot.dashboard.domain.DashboardMode
import com.tankpilot.dashboard.domain.DashboardSessionState
import com.tankpilot.dashboard.domain.DashboardUiState
import com.tankpilot.dashboard.domain.FuelAlertLevel
import com.tankpilot.location.domain.TrackingUnavailableReason

/**
 * Tesla-inspired full-screen dashboard.
 *
 * Design principles:
 * - Near-black background (#0A0A0C)
 * - Massive typography (speed at 96sp)
 * - Glassmorphic cards for secondary metrics
 * - Smooth spring animations
 * - Color-coded fuel state
 * - No skeuomorphic gauges
 * - Vehicle digital twin as focal point
 * - Distraction-safe glanceable info
 */
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    pendingSessionState: DashboardSessionState? = null,
    vehicleName: String? = null,
    onToggleFocusMode: () -> Unit,
    onExit: () -> Unit,
    onConfirmRestore: () -> Unit = {},
    onEndPreviousTrip: () -> Unit = {},
    onDismissRestore: () -> Unit = {},
    onStartDriveRequest: () -> Unit = {},
    onOpenLocationSettings: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    DashboardWakeLockEffect(enabled = uiState.dashboardMode == DashboardMode.ACTIVE)

    // Resume dialog — shown when a stale session needs user confirmation
    if (uiState.dashboardMode == DashboardMode.CONFIRMATION_REQUIRED && pendingSessionState != null) {
        SessionResumeDialog(
            pendingState = pendingSessionState,
            vehicleName = vehicleName,
            onResumeDrive = onConfirmRestore,
            onEndPreviousTrip = onEndPreviousTrip,
            onDismiss = onDismissRestore
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TeslaDarkBg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .clickable { onToggleFocusMode() }
            .padding(20.dp)
    ) {
        if (uiState.isFocusModeEnabled) {
            DashboardFocusLayout(uiState)
        } else if (isLandscape) {
            DashboardLandscapeLayout(uiState)
        } else {
            DashboardPortraitLayout(uiState)
        }

        // Start/End Drive button
        val speed = uiState.speed.speedKmh ?: 0
        if (!uiState.isTrackingActive) {
            Column(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalAlignment = Alignment.End
            ) {
                uiState.trackingError?.let { reason ->
                    TrackingErrorNotice(reason = reason, onOpenLocationSettings = onOpenLocationSettings)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Button(
                    onClick = onStartDriveRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TeslaBlue,
                        contentColor = TeslaWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Start Drive", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else if (speed < 8) {
            Button(
                onClick = onExit,
                modifier = Modifier.align(Alignment.TopEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TeslaGlassBg,
                    contentColor = TeslaGrayPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("End Drive", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Explains why Start Drive didn't start tracking and offers the right next action —
 * retry (just tap Start Drive again) or, when location services themselves are off,
 * a direct link to the system Location settings.
 */
@Composable
private fun TrackingErrorNotice(
    reason: TrackingUnavailableReason,
    onOpenLocationSettings: () -> Unit
) {
    val message = when (reason) {
        TrackingUnavailableReason.LOCATION_PERMISSION_DENIED ->
            "Location permission denied. Tap Start Drive to try again."
        TrackingUnavailableReason.LOCATION_SERVICES_DISABLED ->
            "Location services are off."
        TrackingUnavailableReason.FOREGROUND_START_NOT_ALLOWED ->
            "Couldn't start tracking from the background. Reopen the app and tap Start Drive."
        TrackingUnavailableReason.PLAY_SERVICES_UNAVAILABLE ->
            "Google Play services unavailable. Using device GPS instead — tap Start Drive to try again."
        TrackingUnavailableReason.UNKNOWN ->
            "Couldn't start tracking. Tap Start Drive to try again."
    }
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = message,
            color = FuelCritical,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        if (reason == TrackingUnavailableReason.LOCATION_SERVICES_DISABLED) {
            Text(
                text = "Open Settings",
                color = TeslaBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onOpenLocationSettings() }
            )
        }
    }
}

/**
 * Landscape layout — optimized for head unit / horizontal screens.
 * Resembles a modern instrument cluster.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │  [Status bar: OBD status | Driving type | Trip time]         │
 * │  [Fuel alert banner if applicable]                           │
 * │                                                              │
 * │  72 MPH           [Digital Twin]        ⛽ 8.2 gal │ 186 mi │
 * │                                                              │
 * │  [████████████████████████░░░░░ Fuel Bar ░░░░░░░░░]         │
 * │                                                              │
 * │  [RPM] [Coolant] [MPG] [Load]    Trip: 47 min │ 38.2 mi    │
 * └──────────────────────────────────────────────────────────────┘
 */
@Composable
fun DashboardLandscapeLayout(uiState: DashboardUiState) {
    Column(modifier = Modifier.fillMaxSize()) {
        // ── Top status bar ──────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TelemetryStatusBadge(status = uiState.telemetryStatus)
            DrivingTypeBadge(drivingType = uiState.drivingType)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = uiState.tripTime.formatted,
                    color = TeslaGrayPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = uiState.tripDistance.formatted,
                    color = TeslaGrayPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ── Fuel alert banner ───────────────────────────────
        AnimatedVisibility(
            visible = uiState.fuelAlertLevel != FuelAlertLevel.NORMAL,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            FuelAlertBanner(
                alertLevel = uiState.fuelAlertLevel,
                milesRemaining = uiState.estimatedMilesToEmpty.miles,
                overrideMessage = uiState.alertText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Main content: Speed | Twin | Fuel stats ─────────
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Speed + MPG
            Column(
                modifier = Modifier.weight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SpeedometerText(
                    speed = uiState.speed.speedKmh?.let { (it / 1.609344).toInt() } ?: 0,
                    unit = if (uiState.speed.speedKmh != null) "MPH" else "--"
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Instant MPG
                uiState.mpg.instant?.let { mpg ->
                    Text(
                        text = "%.1f".format(mpg),
                        color = when {
                            mpg > 30.0 -> MpgGood
                            mpg > 20.0 -> MpgAverage
                            else -> MpgPoor
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "MPG",
                        color = TeslaGraySecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }

            // Center: Digital Twin
            Box(
                modifier = Modifier.weight(0.4f),
                contentAlignment = Alignment.Center
            ) {
                VehicleRenderer(
                    state = VehicleState(
                        fuelPercentage = ((uiState.fuelRemaining.gallons ?: 0.0) / (uiState.fuelRemaining.tankCapacityGallons ?: 17.0)).toFloat()
                    ),
                    modifier = Modifier.size(200.dp)
                )
            }

            // Right: Fuel stats
            Column(
                modifier = Modifier.weight(0.3f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Fuel remaining
                Text(
                    text = uiState.fuelRemaining.gallons?.let { "%.1f".format(it) } ?: "--",
                    color = TeslaWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "GALLONS",
                    color = TeslaGraySecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Range
                Text(
                    text = "${uiState.safeRange.miles ?: "--"}",
                    color = TeslaWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "MILES RANGE",
                    color = TeslaGraySecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        // ── Fuel gauge bar ──────────────────────────────────
        val fuelPercent = uiState.fuelRemaining.fuelPercent?.toFloat() ?: 0f
        TeslaGaugeBar(
            fuelPercent = fuelPercent,
            gallonsRemaining = uiState.fuelRemaining.gallons,
            milesRemaining = uiState.estimatedMilesToEmpty.miles ?: uiState.safeRange.miles,
            alertLevel = uiState.fuelAlertLevel,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ── Bottom: Secondary metrics ───────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardCard(
                title = "RPM",
                value = uiState.rpm?.value ?: "---",
                unit = "",
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Coolant",
                value = uiState.coolantTemperature?.value ?: "---",
                unit = "°F",
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Avg MPG",
                value = uiState.mpg.tripAverage?.let { "%.1f".format(it) } ?: "---",
                unit = "",
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Battery",
                value = uiState.batteryVoltage?.value ?: "---",
                unit = "V",
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Confidence",
                value = "${uiState.confidence.percent ?: "--"}",
                unit = "%",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Portrait layout — phone in hand or vertical head unit.
 */
@Composable
fun DashboardPortraitLayout(uiState: DashboardUiState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TelemetryStatusBadge(status = uiState.telemetryStatus)
            DrivingTypeBadge(drivingType = uiState.drivingType)
        }

        // Fuel alert
        AnimatedVisibility(
            visible = uiState.fuelAlertLevel != FuelAlertLevel.NORMAL,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            FuelAlertBanner(
                alertLevel = uiState.fuelAlertLevel,
                milesRemaining = uiState.estimatedMilesToEmpty.miles,
                overrideMessage = uiState.alertText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Speed
        SpeedometerText(
            speed = uiState.speed.speedKmh?.let { (it / 1.609344).toInt() } ?: 0,
            unit = if (uiState.speed.speedKmh != null) "MPH" else "--"
        )

        // Digital Twin
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            VehicleRenderer(
                state = VehicleState(
                    fuelPercentage = ((uiState.fuelRemaining.gallons ?: 0.0) / (uiState.fuelRemaining.tankCapacityGallons ?: 17.0)).toFloat()
                ),
                modifier = Modifier.size(300.dp)
            )
        }

        // Fuel gauge bar
        val fuelPercent = uiState.fuelRemaining.fuelPercent?.toFloat() ?: 0f
        TeslaGaugeBar(
            fuelPercent = fuelPercent,
            gallonsRemaining = uiState.fuelRemaining.gallons,
            milesRemaining = uiState.estimatedMilesToEmpty.miles ?: uiState.safeRange.miles,
            alertLevel = uiState.fuelAlertLevel,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Bottom stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardCard(
                title = "Range",
                value = uiState.safeRange.miles?.toString() ?: "--",
                unit = "mi",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            )
            DashboardCard(
                title = "MPG",
                value = uiState.mpg.instant?.let { "%.1f".format(it) } ?: "--",
                unit = "",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            )
            DashboardCard(
                title = "Fuel",
                value = uiState.fuelRemaining.gallons?.let { "%.1f".format(it) } ?: "--",
                unit = "gal",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            )
        }

        // Trip info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Trip: ${uiState.tripTime.formatted}",
                color = TeslaGraySecondary,
                fontSize = 13.sp
            )
            Text(
                text = uiState.tripDistance.formatted,
                color = TeslaGraySecondary,
                fontSize = 13.sp
            )
            Text(
                text = "Confidence: ${uiState.confidence.percent ?: "--"}%",
                color = TeslaGraySecondary,
                fontSize = 13.sp
            )
        }
    }
}

/**
 * Focus mode — maximum glanceability, minimal info.
 * Speed dominates, with only 3 essential metrics below.
 */
@Composable
fun DashboardFocusLayout(uiState: DashboardUiState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fuel alert at top
        AnimatedVisibility(
            visible = uiState.fuelAlertLevel != FuelAlertLevel.NORMAL,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FuelAlertBanner(
                alertLevel = uiState.fuelAlertLevel,
                milesRemaining = uiState.estimatedMilesToEmpty.miles,
                overrideMessage = uiState.alertText,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Speed
        SpeedometerText(
            speed = uiState.speed.speedKmh?.let { (it / 1.609344).toInt() } ?: 0,
            unit = if (uiState.speed.speedKmh != null) "MPH" else "--",
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 3 primary stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${uiState.safeRange.miles ?: "--"} mi",
                    color = TeslaWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "RANGE",
                    color = TeslaGraySecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.fuelRemaining.gallons?.let { "%.1f".format(it) + " gal" } ?: "-- gal",
                    color = TeslaWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "FUEL",
                    color = TeslaGraySecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.mpg.instant?.let { "%.1f".format(it) } ?: "--",
                    color = TeslaWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "MPG",
                    color = TeslaGraySecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Driving type + trip info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DrivingTypeBadge(drivingType = uiState.drivingType)
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = uiState.tripTime.formatted,
                color = TeslaGrayMuted,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = uiState.tripDistance.formatted,
                color = TeslaGrayMuted,
                fontSize = 13.sp
            )
        }

        // Fuel rescue button when fuel is low
        if (uiState.fuelAlertLevel != FuelAlertLevel.NORMAL) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* Navigate to Fuel Rescue */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (uiState.fuelAlertLevel) {
                        FuelAlertLevel.CRITICAL, FuelAlertLevel.EMPTY_IMMINENT -> FuelCritical
                        else -> TeslaBlue
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 48.dp)
            ) {
                Text(
                    text = "FIND GAS",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
