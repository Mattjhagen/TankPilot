package com.tankpilot.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.tankpilot.android.ui.theme.*
import kotlin.math.sin

@Composable
fun VerticalFuelTank(
    percentage: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier
) {
    // 1. Animate fuel level percent slowly (physics feel)
    val animatedPercent by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "FuelLevelAnimation"
    )

    // 2. Continuous wave phase animation
    val infiniteTransition = rememberInfiniteTransition(label = "WaveTransition")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhaseAnimation"
    )

    // 3. Determine colors based on fuel percent
    val targetColor = when {
        animatedPercent >= 0.70f -> FuelGreen
        animatedPercent >= 0.50f -> FuelLime
        animatedPercent >= 0.30f -> FuelYellow
        animatedPercent >= 0.15f -> FuelOrange
        else -> FuelRed
    }

    val fuelColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 800),
        label = "FuelColorAnimation"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val cornerRadiusPx = 24.dp.toPx()

            // A. Glass outer wall (Subtle border + dark interior shadow look)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.08f),
                size = size,
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )

            drawRoundRect(
                color = Color.White.copy(alpha = 0.15f),
                size = size,
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = 2.dp.toPx())
            )

            // B. Clip liquid inside the rounded glass container
            val clipPath = Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        left = 0f,
                        top = 0f,
                        right = width,
                        bottom = height,
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                )
            }

            clipPath(clipPath) {
                // Calculate fluid level height
                val liquidHeight = height * animatedPercent
                val topY = height - liquidHeight

                // Draw liquid if we have any
                if (animatedPercent > 0.01f) {
                    val wavePath = Path()
                    val waveAmplitude = 12.dp.toPx() // Height of wave
                    val waveFrequency = (2 * Math.PI) / width // Width of wave

                    wavePath.moveTo(0f, height)
                    
                    // Trace top wave line
                    for (x in 0..width.toInt()) {
                        val y = topY + waveAmplitude * sin(waveFrequency * x + wavePhase)
                        wavePath.lineTo(x.toFloat(), y.toFloat())
                    }
                    
                    wavePath.lineTo(width, height)
                    wavePath.close()

                    // Glow gradient fill (underglow look)
                    val liquidBrush = Brush.verticalGradient(
                        colors = listOf(
                            fuelColor.copy(alpha = 0.85f),
                            fuelColor.copy(alpha = 0.3f)
                        ),
                        startY = topY - waveAmplitude,
                        endY = height
                    )

                    drawPath(
                        path = wavePath,
                        brush = liquidBrush
                    )

                    // Draw a highlight line on top of the liquid
                    val surfacePath = Path()
                    surfacePath.moveTo(0f, (topY + waveAmplitude * sin(wavePhase)).toFloat())
                    for (x in 0..width.toInt()) {
                        val y = topY + waveAmplitude * sin(waveFrequency * x + wavePhase)
                        surfacePath.lineTo(x.toFloat(), y.toFloat())
                    }
                    drawPath(
                        path = surfacePath,
                        color = Color.White.copy(alpha = 0.4f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}
