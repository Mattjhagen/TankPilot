package com.tankpilot.android.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tankpilot.android.ui.theme.*
import com.tankpilot.core.Money
import com.tankpilot.fuelrescue.domain.FuelStationRecommendation
import com.tankpilot.fuelrescue.domain.ReachabilityStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelRescueScreen(
    recommendations: List<FuelStationRecommendation>,
    isRefreshing: Boolean,
    onRefreshClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
            }
            Text(
                text = "FUEL RESCUE",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isRefreshing) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = White)
            } else {
                Text(
                    text = "REFRESH",
                    style = MaterialTheme.typography.labelLarge,
                    color = White,
                    modifier = Modifier.clickable { onRefreshClick() }
                )
            }
        }

        // A. Custom Canvas Stylized Map (Apple Maps meets Tesla style Mock)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0F0F0F))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw roads (clean gray lines)
                drawLine(Color(0xFF2C2C2E), Offset(0f, h * 0.4f), Offset(w, h * 0.4f), strokeWidth = 8.dp.toPx())
                drawLine(Color(0xFF2C2C2E), Offset(w * 0.3f, 0f), Offset(w * 0.3f, h), strokeWidth = 8.dp.toPx())
                drawLine(Color(0xFF2C2C2E), Offset(0f, h * 0.7f), Offset(w, h * 0.8f), strokeWidth = 8.dp.toPx())

                // Draw dots representing stations
                // Station A
                drawCircle(FuelGreen, center = Offset(w * 0.4f, h * 0.35f), radius = 10.dp.toPx())
                // Station B
                drawCircle(FuelLime, center = Offset(w * 0.25f, h * 0.65f), radius = 10.dp.toPx())
                // Station C
                drawCircle(FuelOrange, center = Offset(w * 0.7f, h * 0.75f), radius = 10.dp.toPx())

                // User current location dot
                drawCircle(Color(0xFF007AFF), center = Offset(w * 0.5f, h * 0.5f), radius = 8.dp.toPx())
                drawCircle(Color(0xFF007AFF).copy(alpha = 0.2f), center = Offset(w * 0.5f, h * 0.5f), radius = 24.dp.toPx())
            }

            // Warnings Card at top
            val safeStations = recommendations.filter { it.reachabilityStatus == ReachabilityStatus.SAFELY_REACHABLE }
            if (safeStations.isEmpty() && recommendations.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FuelRed.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SAFETY ALERT", fontWeight = FontWeight.Bold, color = White, fontSize = 12.sp)
                        Text(
                            "Critical fuel — no station is safely within the current estimate.",
                            fontWeight = FontWeight.SemiBold,
                            color = White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = FuelRed),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Call Roadside", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // B. Horizontal Recommendation List Card (Slides up)
        if (recommendations.isNotEmpty()) {
            val selectedRec = recommendations.getOrNull(selectedIndex) ?: recommendations.first()
            
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            val badge = when (selectedIndex) {
                                0 -> "BEST OVERALL"
                                1 -> "CHEAPEST REACHABLE"
                                else -> "CLOSEST SAFE"
                            }
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selectedRec.reachabilityStatus == ReachabilityStatus.SAFELY_REACHABLE) FuelGreen else FuelOrange
                            )
                            Text(
                                text = selectedRec.station.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }

                        // Display formatted price per gallon
                        val priceText = selectedRec.advertisedPrice?.let {
                            val dollars = it.money.amountMicros.value.toDouble() / 1_000_000.0
                            "$${"%.2f".format(dollars)}"
                        } ?: "Price Unknown"
                        Text(
                            text = priceText,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }

                    // Reasons and warnings
                    selectedRec.warningMessages.forEach { warning ->
                        Text(warning, color = FuelRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    selectedRec.recommendationReasons.forEach { reason ->
                        Text(reason, color = FuelGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Divider(color = GrayBorder)

                    // Distances and travel info
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("ROUTE DISTANCE", style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = "${"%.1f".format(selectedRec.station.routeDistanceMiles ?: 0.0)} miles",
                                color = White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column {
                            Text("DRIVE TIME", style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = "${selectedRec.station.estimatedDriveMinutes?.toInt() ?: 0} min",
                                color = White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column {
                            Text("ON ARRIVAL", style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = "${"%.1f".format(selectedRec.estimatedFuelRemainingOnArrival.value)} gal",
                                color = if (selectedRec.reachabilityStatus == ReachabilityStatus.SAFELY_REACHABLE) FuelGreen else FuelOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Swipe / Next Button
                        Button(
                            onClick = {
                                selectedIndex = (selectedIndex + 1) % recommendations.size
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GrayBorder, contentColor = White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {
                            Text("Next Station")
                        }

                        // Navigate (System Maps handoff)
                        Button(
                            onClick = {
                                val gmmIntentUri = Uri.parse(selectedRec.station.navigationDestination ?: "geo:${selectedRec.station.latitude},${selectedRec.station.longitude}?q=${selectedRec.station.name}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                context.startActivity(mapIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = DarkBg),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(56.dp)
                        ) {
                            Text("Navigate", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Empty view
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Text("No compatible fuel stations nearby.", color = GraySecondary)
            }
        }
    }
}
