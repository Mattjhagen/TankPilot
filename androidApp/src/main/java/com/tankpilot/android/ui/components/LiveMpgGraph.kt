package com.tankpilot.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tankpilot.android.ui.theme.*

/**
 * Minimal Tesla-style sparkline showing MPG over time. No axes, no labels —
 * just a clean glanceable line with a subtle fill gradient.
 *
 * Color-coded against the baseline:
 * - Green when above average
 * - Gray at average
 * - Amber when below average
 */
@Composable
fun LiveMpgGraph(
    mpgHistory: List<Double>,
    baselineMpg: Double?,
    modifier: Modifier = Modifier
) {
    if (mpgHistory.size < 2) {
        // Not enough data for a line — show placeholder
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "Collecting MPG data…",
                color = TeslaGrayMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    val data = remember(mpgHistory) { mpgHistory.toList() }
    val baseline = baselineMpg ?: data.average()

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val n = data.size

        val minVal = data.min() - 2.0
        val maxVal = data.max() + 2.0
        val range = (maxVal - minVal).coerceAtLeast(1.0)

        fun xFor(i: Int) = (i.toFloat() / (n - 1)) * w
        fun yFor(v: Double) = h - ((v - minVal) / range * h).toFloat()

        // Draw baseline
        val baselineY = yFor(baseline)
        drawLine(
            color = TeslaGrayMuted,
            start = Offset(0f, baselineY),
            end = Offset(w, baselineY),
            strokeWidth = 1f,
            alpha = 0.4f
        )

        // Build the sparkline path
        val path = Path().apply {
            moveTo(xFor(0), yFor(data[0]))
            for (i in 1 until n) {
                lineTo(xFor(i), yFor(data[i]))
            }
        }

        // Determine the line color based on current trend vs baseline
        val currentMpg = data.last()
        val lineColor = when {
            currentMpg > baseline * 1.05 -> MpgGood
            currentMpg < baseline * 0.95 -> MpgPoor
            else -> MpgAverage
        }

        // Draw the line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5f)
        )

        // Draw current value dot
        val lastX = xFor(n - 1)
        val lastY = yFor(data.last())
        drawCircle(
            color = lineColor,
            radius = 4f,
            center = Offset(lastX, lastY)
        )
        // Outer glow
        drawCircle(
            color = lineColor.copy(alpha = 0.3f),
            radius = 8f,
            center = Offset(lastX, lastY)
        )
    }
}
