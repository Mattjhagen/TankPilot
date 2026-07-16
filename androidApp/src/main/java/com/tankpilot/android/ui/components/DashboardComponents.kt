package com.tankpilot.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tankpilot.android.ui.theme.*
import com.tankpilot.dashboard.domain.TelemetryStatusDisplay
import com.tankpilot.dashboard.domain.FuelAlertLevel
import com.tankpilot.trip.domain.DrivingType

/**
 * Glassmorphic card used for secondary metrics (RPM, coolant, etc.).
 * Tesla design language: frosted glass background, thin border, minimal chrome.
 */
@Composable
fun DashboardCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    accentColor: Color = TeslaGrayPrimary
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(TeslaGlassBg)
            .border(1.dp, TeslaGlassBorder, shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title.uppercase(),
            color = TeslaGraySecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = TeslaWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium
            )
            if (unit.isNotBlank()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = TeslaGrayPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

/**
 * Massive speedometer text — Tesla scale. Speed number at 96sp, unit label below.
 * No decorative animation on the speed value — SpeedSmoother already handles
 * jitter filtering in the domain layer, and any overshoot/bounce would make
 * the displayed speed inaccurate (safety-critical).
 */
@Composable
fun SpeedometerText(
    speed: Int,
    unit: String = "MPH",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = speed.toString(),
            color = TeslaWhite,
            fontSize = 96.sp,
            fontWeight = FontWeight.Thin,
            lineHeight = 96.sp
        )
        Text(
            text = unit,
            color = TeslaGraySecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )
    }
}

/**
 * Small driving-type badge showing CITY / HIGHWAY / MIXED with color-coded dot.
 */
@Composable
fun DrivingTypeBadge(
    drivingType: DrivingType,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = when (drivingType) {
            DrivingType.CITY -> CityDriving
            DrivingType.HIGHWAY -> HighwayDriving
            DrivingType.MIXED -> MixedDriving
        },
        label = "driving_color"
    )

    val label = when (drivingType) {
        DrivingType.CITY -> "STOP-AND-GO PATTERN"
        DrivingType.HIGHWAY -> "HIGH-SPEED PATTERN"
        DrivingType.MIXED -> "MIXED PATTERN"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

/**
 * Compact OBD connection status indicator.
 */
@Composable
fun TelemetryStatusBadge(
    status: TelemetryStatusDisplay,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (status) {
        TelemetryStatusDisplay.CONNECTED -> TeslaBlue to "OBD CONNECTED"
        TelemetryStatusDisplay.CONNECTING -> TeslaCyan to "CONNECTING…"
        TelemetryStatusDisplay.STALE -> FuelLow to "SIGNAL STALE"
        TelemetryStatusDisplay.DISCONNECTED -> TeslaGrayMuted to "GPS ONLY"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Fuel alert banner — slides in from top when fuel is low/critical.
 */
@Composable
fun FuelAlertBanner(
    alertLevel: FuelAlertLevel,
    milesRemaining: Int?,
    overrideMessage: String? = null,
    modifier: Modifier = Modifier
) {
    if (alertLevel == FuelAlertLevel.NORMAL) return

    val (bgColor, textColor, defaultMessage) = when (alertLevel) {
        FuelAlertLevel.LOW -> Triple(
            FuelLow.copy(alpha = 0.15f),
            FuelLow,
            "Fuel Low — ${milesRemaining ?: "--"} mi remaining"
        )
        FuelAlertLevel.CRITICAL -> Triple(
            FuelCritical.copy(alpha = 0.15f),
            FuelCritical,
            "Fuel Critical — ${milesRemaining ?: "--"} mi remaining"
        )
        FuelAlertLevel.EMPTY_IMMINENT -> Triple(
            FuelEmptyImminent.copy(alpha = 0.20f),
            FuelEmptyImminent,
            "⚠ REFUEL NOW — ${milesRemaining ?: "--"} mi remaining"
        )
        else -> return
    }
    val message = overrideMessage?.takeIf { it.isNotBlank() } ?: defaultMessage

    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), shape)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
