package com.tankpilot.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tankpilot.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillUpScreen(
    onConfirmClick: (gallons: Double, price: Double, odometer: Double?, isFull: Boolean) -> Unit,
    onCancelClick: () -> Unit
) {
    var gallons by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var isFull by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "LOG TRANSACTION",
            style = MaterialTheme.typography.labelLarge,
            color = GraySecondary
        )
        Text(
            text = "Filled Up",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Text(
            text = "Log the exact gallons added to recalibrate the model. Complete fill-ups improve range accuracy.",
            style = MaterialTheme.typography.bodyMedium,
            color = GraySecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = gallons,
                    onValueChange = { gallons = it },
                    label = { Text("Gallons Added") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price Per Gallon ($)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = odometer,
                    onValueChange = { odometer = it },
                    label = { Text("Odometer Reading (Optional)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                // Full Tank Toggle
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Was tank completely full?", color = White, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isFull,
                        onCheckedChange = { isFull = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBg,
                            checkedTrackColor = White,
                            uncheckedThumbColor = GraySecondary,
                            uncheckedTrackColor = DarkBg
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val gDouble = gallons.toDoubleOrNull() ?: 0.0
                    val pDouble = price.toDoubleOrNull() ?: 0.0
                    val oDouble = odometer.toDoubleOrNull()
                    if (gDouble > 0.0) {
                        onConfirmClick(gDouble, pDouble, oDouble, isFull)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = DarkBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Confirm Fill Up", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
