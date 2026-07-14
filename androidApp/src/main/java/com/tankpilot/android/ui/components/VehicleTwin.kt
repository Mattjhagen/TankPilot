package com.tankpilot.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tankpilot.android.ui.theme.*

@Composable
fun VehicleTwin(
    fuelPercentage: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier
) {
    // 1. Determine glow color
    val glowTargetColor = when {
        fuelPercentage >= 0.70f -> FuelGreen
        fuelPercentage >= 0.50f -> FuelLime
        fuelPercentage >= 0.30f -> FuelYellow
        fuelPercentage >= 0.15f -> FuelOrange
        else -> FuelRed
    }

    val glowColor by animateColorAsState(
        targetValue = glowTargetColor,
        animationSpec = tween(durationMillis = 800),
        label = "TwinGlowColor"
    )

    // 2. Headlight intensity
    val lightAlpha = if (fuelPercentage < 0.15f) 0.1f else 0.25f
    val animatedLightAlpha by animateFloatAsState(
        targetValue = lightAlpha,
        animationSpec = tween(durationMillis = 500),
        label = "HeadlightAlpha"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Center coordinates for drawing the vehicle
            val cx = width / 2
            val cy = height / 2

            // A. Draw soft underglow
            // Brush vertical gradient centered under the chassis
            val glowBrush = Brush.radialGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(cx, cy + 30.dp.toPx()),
                radius = 120.dp.toPx()
            )
            drawCircle(
                brush = glowBrush,
                center = Offset(cx, cy + 30.dp.toPx()),
                radius = 120.dp.toPx()
            )

            // B. Draw headlights casting forward (to the right)
            val lightPath = Path().apply {
                moveTo(cx + 80.dp.toPx(), cy + 10.dp.toPx()) // Front bumper center
                lineTo(width, cy - 20.dp.toPx()) // Upper beam
                lineTo(width, cy + 60.dp.toPx()) // Lower beam
                close()
            }
            val lightBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFFFFE0).copy(alpha = animatedLightAlpha), // Warm light
                    Color.Transparent
                ),
                startX = cx + 80.dp.toPx(),
                endX = width
            )
            drawPath(path = lightPath, brush = lightBrush)

            // C. Draw stylized car silhouette (side view)
            val chassisColor = Color(0xFF2C2C2E)
            val windowColor = Color(0xFF141414)
            val wheelColor = Color(0xFF1C1C1E)
            val metallicDetailColor = Color(0xFF48484A)

            // Cabin (Upper body)
            val cabinPath = Path().apply {
                moveTo(cx - 50.dp.toPx(), cy)
                lineTo(cx - 30.dp.toPx(), cy - 25.dp.toPx()) // windshield rear
                lineTo(cx + 25.dp.toPx(), cy - 25.dp.toPx()) // roof line
                lineTo(cx + 50.dp.toPx(), cy) // windshield front
                close()
            }
            drawPath(path = cabinPath, color = chassisColor)

            // Cabin inner window gap
            val windowPath = Path().apply {
                moveTo(cx - 42.dp.toPx(), cy - 2.dp.toPx())
                lineTo(cx - 26.dp.toPx(), cy - 21.dp.toPx())
                lineTo(cx + 20.dp.toPx(), cy - 21.dp.toPx())
                lineTo(cx + 42.dp.toPx(), cy - 2.dp.toPx())
                close()
            }
            drawPath(path = windowPath, color = windowColor)

            // Split window line
            drawLine(
                color = chassisColor,
                start = Offset(cx, cy - 21.dp.toPx()),
                end = Offset(cx, cy - 2.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )

            // Chassis (Lower body)
            drawRoundRect(
                color = chassisColor,
                topLeft = Offset(cx - 85.dp.toPx(), cy - 5.dp.toPx()),
                size = Size(170.dp.toPx(), 25.dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Metallic side molding line
            drawLine(
                color = metallicDetailColor,
                start = Offset(cx - 80.dp.toPx(), cy + 10.dp.toPx()),
                end = Offset(cx + 75.dp.toPx(), cy + 10.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )

            // D. Wheels
            val rearWheelCenter = Offset(cx - 45.dp.toPx(), cy + 20.dp.toPx())
            val frontWheelCenter = Offset(cx + 45.dp.toPx(), cy + 20.dp.toPx())
            val wheelRadius = 15.dp.toPx()

            // Rear wheel
            drawCircle(color = wheelColor, center = rearWheelCenter, radius = wheelRadius)
            drawCircle(color = Color.Black, center = rearWheelCenter, radius = wheelRadius - 4.dp.toPx())
            drawCircle(color = metallicDetailColor, center = rearWheelCenter, radius = 5.dp.toPx(), style = Stroke(width = 2.dp.toPx()))

            // Front wheel
            drawCircle(color = wheelColor, center = frontWheelCenter, radius = wheelRadius)
            drawCircle(color = Color.Black, center = frontWheelCenter, radius = wheelRadius - 4.dp.toPx())
            drawCircle(color = metallicDetailColor, center = frontWheelCenter, radius = 5.dp.toPx(), style = Stroke(width = 2.dp.toPx()))
        }
    }
}
