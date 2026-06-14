package com.example.stopwatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.ui.theme.Primary40
import com.example.stopwatch.ui.theme.Secondary40
import com.example.stopwatch.ui.theme.Secondary80
import com.example.stopwatch.ui.theme.SurfaceLight
import com.example.stopwatch.ui.theme.Tertiary40
import com.example.stopwatch.viewmodel.Lap
import com.example.stopwatch.viewmodel.StopwatchViewModel

@Composable
fun StopwatchScreen(viewModel: StopwatchViewModel = viewModel()) {
    val timeMillis by viewModel.timeMillis.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val laps by viewModel.laps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Time Display
        Text(
            text = formatTime(timeMillis),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Primary40,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            IconButton(
                onClick = { viewModel.reset() },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Secondary80)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = Secondary40,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Play/Pause Button
            IconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Primary40)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Lap Button
            IconButton(
                onClick = { viewModel.lap() },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Secondary80)
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Lap",
                    tint = Tertiary40,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Laps List
        if (laps.isNotEmpty()) {
            Text(
                text = "Laps",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Secondary40,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(laps) { lap ->
                    LapItem(lap = lap)
                }
            }
        }
    }
}

@Composable
fun LapItem(lap: Lap) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Lap ${lap.id}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Secondary40
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(
                text = "+${formatTime(lap.lapTime, includeMillis = true)}",
                fontSize = 16.sp,
                color = Tertiary40,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = formatTime(lap.totalTime, includeMillis = true),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Primary40,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

fun formatTime(timeMillis: Long, includeMillis: Boolean = true): String {
    val totalSeconds = timeMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = (timeMillis % 1000) / 10 // Get hundredths of a second

    return if (includeMillis) {
        String.format("%02d:%02d.%02d", minutes, seconds, millis)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
