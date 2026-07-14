package com.tankpilot.android.ui.screens.testlab

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tankpilot.testsupport.MockSpeedScenario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestLabScreen(
    viewModel: TestLabViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val currentScenario by viewModel.currentScenario.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phase 4D Test Lab") },
                navigationIcon = {
                    Button(onClick = onNavigateBack) { Text("Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            if (currentScenario == null) {
                Text("Select a Validation Scenario", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(viewModel.scenarios) { scenario ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            onClick = { viewModel.loadScenario(scenario) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(scenario.title, style = MaterialTheme.typography.titleMedium)
                                Text(scenario.description, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Android Auto (Desktop Head Unit)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Sets the fixture the car's Fuel Status / Fuel Rescue screens show. No selector exists in the car itself.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(viewModel.androidAutoScenarios) { scenario ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            onClick = { viewModel.loadScenario(scenario) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(scenario.title, style = MaterialTheme.typography.titleMedium)
                                Text(scenario.description, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            } else {
                val active = currentScenario!!
                Text("Active: ${active.title}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Description: ${active.description}")
                Text("Criteria: ${active.passCriteria}", color = MaterialTheme.colorScheme.primary)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (active.id == "s5") {
                    Text("Trigger Speed Injection:")
                    Row {
                        Button(onClick = { viewModel.setSpeed(MockSpeedScenario.BRIEF_SPIKE) }) { Text("Spike") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { viewModel.setSpeed(MockSpeedScenario.SUSTAINED_SPEED) }) { Text("Sustained") }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { viewModel.reset() }) {
                        Text("Reset & Back")
                    }
                    Button(onClick = { /* manual pass/fail logic */ }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                        Text("Mark Pass")
                    }
                }
            }
        }
    }
}
