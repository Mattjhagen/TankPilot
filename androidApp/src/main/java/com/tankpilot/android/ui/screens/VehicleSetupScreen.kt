package com.tankpilot.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tankpilot.android.ui.theme.*
import com.tankpilot.core.FuelType
import com.tankpilot.core.UnitSystem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSetupScreen(
    onVehicleCreated: (
        year: Int, make: String, model: String, trim: String?, color: String?,
        engine: String, displacement: Double?, cylinders: Long?, tankCap: Double,
        cityMpg: Double, hwyMpg: Double, fuelType: FuelType, fuelGrade: String?,
        units: UnitSystem, reserve: Double, threshold: Double
    ) -> Unit
) {
    var year by remember { mutableStateOf("2003") }
    var make by remember { mutableStateOf("Chevrolet") }
    var model by remember { mutableStateOf("Impala") }
    var trim by remember { mutableStateOf("LS") }
    var color by remember { mutableStateOf("#D3D3D3") }
    var engine by remember { mutableStateOf("3.4L V6") }
    var displacement by remember { mutableStateOf("3.4") }
    var cylinders by remember { mutableStateOf("6") }
    var tankCapacity by remember { mutableStateOf("17.0") }
    var factoryCityMpg by remember { mutableStateOf("21.0") }
    var factoryHwyMpg by remember { mutableStateOf("32.0") }
    
    var preferredFuelType by remember { mutableStateOf(FuelType.REGULAR) }
    var preferredFuelGrade by remember { mutableStateOf("87 Octane") }
    var unitSystem by remember { mutableStateOf(UnitSystem.IMPERIAL) }
    var reserveFuel by remember { mutableStateOf("2.0") }
    var lowFuelThreshold by remember { mutableStateOf("20") } // percent

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "SETUP GARAGE",
            style = MaterialTheme.typography.labelLarge,
            color = GraySecondary
        )
        Text(
            text = "Create Vehicle Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Text(
            text = "Configure your digital twin model and parameters to initialize TankPilot's tracking engine.",
            style = MaterialTheme.typography.bodyMedium,
            color = GraySecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Basic Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("BASIC INFORMATION", style = MaterialTheme.typography.labelLarge)
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Year") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = make,
                        onValueChange = { make = it },
                        label = { Text("Make") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(2f)
                    )
                }

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = trim,
                        onValueChange = { trim = it },
                        label = { Text("Trim (Optional)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Color Hex") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Engine & Spec Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ENGINE & TELEMETRY SPEC", style = MaterialTheme.typography.labelLarge)
                
                OutlinedTextField(
                    value = engine,
                    onValueChange = { engine = it },
                    label = { Text("Engine Description") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = displacement,
                        onValueChange = { displacement = it },
                        label = { Text("Displacement (L)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cylinders,
                        onValueChange = { cylinders = it },
                        label = { Text("Cylinders") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tankCapacity,
                        onValueChange = { tankCapacity = it },
                        label = { Text("Tank Capacity (Gal)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = reserveFuel,
                        onValueChange = { reserveFuel = it },
                        label = { Text("Reserve (Gal)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Fuel & Economy Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("FUEL & ECONOMY SETTINGS", style = MaterialTheme.typography.labelLarge)
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = factoryCityMpg,
                        onValueChange = { factoryCityMpg = it },
                        label = { Text("City MPG") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = factoryHwyMpg,
                        onValueChange = { factoryHwyMpg = it },
                        label = { Text("Hwy MPG") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fuel Type Selection
                Text("Fuel Type", style = MaterialTheme.typography.bodyMedium, color = GraySecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    FuelType.entries.filter { it != FuelType.UNKNOWN }.forEach { type ->
                        val selected = preferredFuelType == type
                        Button(
                            onClick = { preferredFuelType = type },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) White else GrayBorder,
                                contentColor = if (selected) DarkBg else White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(type.name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = preferredFuelGrade,
                    onValueChange = { preferredFuelGrade = it },
                    label = { Text("Preferred Grade (Optional)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = lowFuelThreshold,
                        onValueChange = { lowFuelThreshold = it },
                        label = { Text("Low Alert %") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { unitSystem = if (unitSystem == UnitSystem.IMPERIAL) UnitSystem.METRIC else UnitSystem.IMPERIAL },
                        colors = ButtonDefaults.buttonColors(containerColor = GrayBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .height(56.dp)
                    ) {
                        Text("Units: ${unitSystem.name}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val yearInt = year.toIntOrNull() ?: 2000
                val tankDouble = tankCapacity.toDoubleOrNull() ?: 15.0
                val cityDouble = factoryCityMpg.toDoubleOrNull() ?: 20.0
                val hwyDouble = factoryHwyMpg.toDoubleOrNull() ?: 25.0
                val reserveDouble = reserveFuel.toDoubleOrNull() ?: 2.0
                val thresholdDouble = (lowFuelThreshold.toDoubleOrNull() ?: 20.0) / 100.0
                val displacementDouble = displacement.toDoubleOrNull()
                val cylindersLong = cylinders.toLongOrNull()

                onVehicleCreated(
                    yearInt, make, model, trim.takeIf { it.isNotBlank() }, color.takeIf { it.isNotBlank() },
                    engine, displacementDouble, cylindersLong, tankDouble, cityDouble, hwyDouble,
                    preferredFuelType, preferredFuelGrade.takeIf { it.isNotBlank() }, unitSystem, reserveDouble, thresholdDouble
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = DarkBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Add Vehicle to Garage", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
