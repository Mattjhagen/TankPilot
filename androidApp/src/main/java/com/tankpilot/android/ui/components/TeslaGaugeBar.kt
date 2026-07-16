package com.tankpilot.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tankpilot.android.ui.theme.*
import com.tankpilot.dashboard.domain.FuelAlertLevel

/**
 * Tesla-inspired horizontal fuel gauge bar. Full-width with smooth
 * animated fill, color transitions based on fuel level, and overlay text.
 *
 * Visual design:
 * ┌──────────────────────────────────────────┐
 * │████████████████████████░░░░░░░░  8.2 gal │
 * └──────────────────────────────────────────┘
 */
@Composable
fun TeslaGaugeBar(
    fuelPercent: Float,
    gallonsRemaining: Double?,
    milesRemaining: Int?,
    alertLevel: FuelAlertLevel = FuelAlertLevel.NORMAL,
    modifier: Modifier = Modifier
) {
    val animatedFill by animateFloatAsState(
        targetValue = fuelPercent.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fuel_fill"
    )

    val barColor by animateColorAsState(
        targetValue = when {
            fuelPercent > 0.50f -> FuelFull
            fuelPercent > 0.25f -> FuelGood
            alertLevel == FuelAlertLevel.EMPTY_IMMINENT -> FuelEmptyImminent
            alertLevel == FuelAlertLevel.CRITICAL -> FuelCritical
            alertLevel == FuelAlertLevel.LOW -> FuelLow
            else -> FuelFull
        },
        label = "fuel_color"
    )

    val shape = RoundedCornerShape(12.dp)

    Column(modifier = modifier) {
        // Gauge bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(shape)
                .background(TeslaGlassBg)
                .border(1.dp, TeslaGlassBorder, shape)
        ) {
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedFill)
                    .clip(shape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(barColor.copy(alpha = 0.6f), barColor)
                        )
                    )
            )

            // Overlay text
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gallonsRemaining?.let { "%.1f gal".format(it) } ?: "-- gal",
                    color = TeslaWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = milesRemaining?.let { "$it mi" } ?: "-- mi",
                    color = TeslaGrayPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
