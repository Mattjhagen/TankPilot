package com.tankpilot.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tankpilot.dashboard.domain.DashboardSessionState
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SessionResumeDialog(
    pendingState: DashboardSessionState,
    vehicleName: String?,
    onResumeDrive: () -> Unit,
    onEndPreviousTrip: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E1E2E),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Resume Previous Session?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Trip metadata rows
                vehicleName?.let { name ->
                    SessionMetaRow(label = "Vehicle", value = name)
                }

                val tripStart = pendingState.startTimeEpochMs
                    .takeIf { it > 0L }
                    ?.let { formatRelativeTime(it) }
                    ?: "Unknown"
                SessionMetaRow(label = "Trip started", value = tripStart)

                val lastActivity = pendingState.lastActivityTimestamp
                    .takeIf { it > 0L }
                    ?.let { formatRelativeTime(it) }
                    ?: "Unknown"
                SessionMetaRow(
                    label = "Last activity",
                    value = lastActivity,
                    valueDescription = "Last activity time: $lastActivity"
                )

                val lastTelemetry = pendingState.lastReliableTelemetryTimestamp
                    .takeIf { it > 0L }
                    ?.let { formatRelativeTime(it) }
                SessionMetaRow(
                    label = "Last telemetry",
                    value = lastTelemetry ?: "Unavailable",
                    muted = lastTelemetry == null
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Staleness advisory
                val elapsedMs = System.currentTimeMillis() - pendingState.lastActivityTimestamp
                val elapsedMinutes = (elapsedMs / 60_000).coerceAtLeast(0)
                Text(
                    text = buildString {
                        append("Session was paused ")
                        if (elapsedMinutes >= 60) {
                            val hours = elapsedMinutes / 60
                            val mins = elapsedMinutes % 60
                            append("${hours}h ${mins}m")
                        } else {
                            append("${elapsedMinutes}m")
                        }
                        append(" ago. Telemetry data may be stale.")
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFFFB74D),
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "Warning: Session data may be stale"
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Action buttons
                Button(
                    onClick = onResumeDrive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Resume Drive" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Resume Drive",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                OutlinedButton(
                    onClick = onEndPreviousTrip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "End Previous Trip and start fresh" },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350))
                ) {
                    Text(
                        "End Previous Trip",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Dismiss without changes" }
                ) {
                    Text(
                        "Dismiss",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionMetaRow(
    label: String,
    value: String,
    muted: Boolean = false,
    valueDescription: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 14.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (muted) Color.White.copy(alpha = 0.35f) else Color.White,
                fontWeight = if (muted) FontWeight.Normal else FontWeight.Medium,
                fontSize = 14.sp
            ),
            modifier = if (valueDescription != null) {
                Modifier.semantics { contentDescription = valueDescription }
            } else Modifier
        )
    }
}

/** Returns a human-readable relative time string like "2h 14m ago" or "35m ago". */
private fun formatRelativeTime(epochMs: Long): String {
    val elapsedMs = System.currentTimeMillis() - epochMs
    if (elapsedMs < 0) return "Just now"
    val minutes = elapsedMs / 60_000
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        else -> "${minutes / 60}h ${minutes % 60}m ago"
    }
}
