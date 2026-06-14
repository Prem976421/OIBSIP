package com.example.stopwatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.ui.theme.Primary40
import com.example.stopwatch.ui.theme.Secondary40
import com.example.stopwatch.ui.theme.SurfaceLight
import com.example.stopwatch.viewmodel.Alarm
import com.example.stopwatch.viewmodel.AlarmViewModel

@Composable
fun AlarmScreen(viewModel: AlarmViewModel = viewModel()) {
    val alarms by viewModel.alarms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Primary40,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alarm")
            }
        },
        containerColor = SurfaceLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = "Alarms",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Primary40,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (alarms.isEmpty()) {
                Text("No alarms set.", color = Secondary40, fontSize = 16.sp)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(alarms, key = { it.id }) { alarm ->
                        AlarmCard(alarm = alarm, onToggle = { viewModel.toggleAlarm(alarm.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var hourStr by remember { mutableStateOf("07") }
        var minStr by remember { mutableStateOf("30") }
        var label by remember { mutableStateOf("Wake Up") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Alarm") },
            text = {
                Column {
                    OutlinedTextField(
                        value = hourStr,
                        onValueChange = { hourStr = it },
                        label = { Text("Hour (0-23)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = minStr,
                        onValueChange = { minStr = it },
                        label = { Text("Minute (0-59)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Label") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val h = hourStr.toIntOrNull() ?: 0
                    val m = minStr.toIntOrNull() ?: 0
                    viewModel.addAlarm(h % 24, m % 60, label)
                    showAddDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AlarmCard(alarm: Alarm, onToggle: () -> Unit) {
    val alpha = if (alarm.isEnabled) 1f else 0.5f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alarm.timeStr,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Primary40.copy(alpha = alpha)
            )
            Text(
                text = alarm.label,
                fontSize = 16.sp,
                color = Secondary40.copy(alpha = alpha),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Switch(
            checked = alarm.isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary40
            )
        )
    }
}
