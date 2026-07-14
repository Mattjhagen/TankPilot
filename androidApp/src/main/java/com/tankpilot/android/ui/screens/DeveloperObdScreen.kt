package com.tankpilot.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tankpilot.android.managers.BluetoothPermissionState
import com.tankpilot.android.managers.rememberBluetoothPermissionState

@Composable
fun DeveloperObdScreen() {
    val context = LocalContext.current
    // Use factory since ViewModel has a Context parameter
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DeveloperObdViewModel(context) as T
        }
    }
    val viewModel: DeveloperObdViewModel = viewModel(factory = factory)

    val (permissionState, requestPermission) = rememberBluetoothPermissionState()
    
    val isBluetoothEnabled by viewModel.isBluetoothEnabled.collectAsState()
    val isBleMode by viewModel.isBleMode.collectAsState()
    val bleDevices by viewModel.bleDevices.collectAsState()
    val bondedDevices by viewModel.bondedDevices.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val transportEvents by viewModel.transportEvents.collectAsState()
    val rawLogs by viewModel.rawLogs.collectAsState()
    val telemetryData by viewModel.telemetryData.collectAsState()
    val capabilities by viewModel.capabilities.collectAsState()

    LaunchedEffect(permissionState, isBluetoothEnabled, isBleMode) {
        if (permissionState == BluetoothPermissionState.GRANTED) {
            if (isBleMode) {
                viewModel.startScan()
            } else {
                viewModel.loadBondedDevices()
            }
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Developer OBD Validation") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            item {
                Text("1. Permissions & State", style = MaterialTheme.typography.titleMedium)
                Text("Permission State: $permissionState")
                Text("Bluetooth Enabled: $isBluetoothEnabled")
                if (permissionState != BluetoothPermissionState.GRANTED) {
                    Button(onClick = requestPermission) {
                        Text("Request Permissions")
                    }
                }
            }
            
            item { HorizontalDivider() }
            
            item {
                Text("2. Transport Mode", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = isBleMode,
                        onClick = { viewModel.setBleMode(true) },
                        label = { Text("BLE GATT") }
                    )
                    FilterChip(
                        selected = !isBleMode,
                        onClick = { viewModel.setBleMode(false) },
                        label = { Text("Classic SPP") }
                    )
                }
            }
            
            item { HorizontalDivider() }
            
            item {
                Text("3. Scanner", style = MaterialTheme.typography.titleMedium)
                if (isBleMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.startScan() }) { Text("Scan") }
                        Button(onClick = { viewModel.stopScan() }) { Text("Stop") }
                    }
                }
                
                val devices = if (isBleMode) bleDevices else bondedDevices
                
                if (devices.isEmpty()) {
                    Text("No devices found.", color = Color.Gray)
                } else {
                    devices.forEach { device ->
                        val isSelected = selectedDevice?.address == device.address
                        val maskedAddress = device.address.take(8) + ":XX:XX:XX"
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(device.name ?: "Unknown Device", style = MaterialTheme.typography.bodyLarge)
                                Text("$maskedAddress | RSSI: ${device.rssi}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Button(
                                onClick = { viewModel.selectDevice(device) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                                )
                            ) {
                                Text(if (isSelected) "Selected" else "Select")
                            }
                        }
                    }
                }
            }
            
            item { HorizontalDivider() }
            
            item {
                Text("4. Connection", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.connect() },
                        enabled = selectedDevice != null && connectionStatus == "DISCONNECTED"
                    ) {
                        Text("Connect")
                    }
                    Button(
                        onClick = { viewModel.disconnect() },
                        enabled = connectionStatus != "DISCONNECTED"
                    ) {
                        Text("Disconnect")
                    }
                }
                Text("Status: $connectionStatus", fontWeight = FontWeight.Bold)
                Text("Events: $transportEvents", fontSize = 12.sp, color = Color.Gray)
            }
            
            item { HorizontalDivider() }
            
            item {
                Text("5. Manual ELM Commands", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.initElm() }) { Text("Init ELM") }
                    Button(onClick = { viewModel.queryRpm() }) { Text("Query RPM") }
                    Button(onClick = { viewModel.querySpeed() }) { Text("Query Speed") }
                }
            }
            
            item { HorizontalDivider() }
            
            item {
                Text("6. Polling", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.startPolling() }) { Text("Start Polling") }
                    Button(onClick = { viewModel.stopPolling() }) { Text("Stop Polling") }
                }
            }
            
            item { HorizontalDivider() }
            
            item {
                Text("7. Live Data", style = MaterialTheme.typography.titleMedium)
                Text("RPM: ${telemetryData?.engineRpm ?: "---"}")
                Text("Speed: ${telemetryData?.speedKmh ?: "---"} km/h")
                Text("Coolant: ${telemetryData?.coolantTempCelsius ?: "---"} °C")
                Text("Voltage: ${telemetryData?.batteryVoltage ?: "---"} V")
            }
            
            item { HorizontalDivider() }
            
            item {
                Text("8. Raw Transcript", style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                        .background(Color.Black).padding(8.dp)
                ) {
                    LazyColumn {
                        items(rawLogs) { log ->
                            Text(log, color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
