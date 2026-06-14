package com.example.stopwatch.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.ui.theme.*
import com.example.stopwatch.viewmodel.TimerViewModel

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val totalTime by viewModel.totalTimeMillis.collectAsState()
    val remainingTime by viewModel.remainingTimeMillis.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    
    val view = LocalView.current
    fun performHaptic() = view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        if (totalTime == 0L) {
            Text(
                text = "Timer",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Primary40,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            TimerInput { h, m, s -> viewModel.setTime(h, m, s) }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                val progress = if (totalTime > 0) remainingTime.toFloat() / totalTime else 0f
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(durationMillis = 100), label = "timerProgress"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Secondary80,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawArc(
                        brush = Brush.sweepGradient(listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen, GoogleBlue)),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = formatTimerTime(remainingTime),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Primary40
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { performHaptic(); viewModel.clear() },
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Secondary80)
                ) {
                    Icon(Icons.Default.Close, "Clear", tint = Secondary40, modifier = Modifier.size(32.dp))
                }

                IconButton(
                    onClick = { performHaptic(); viewModel.togglePlayPause() },
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Primary40, Primary80)))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { performHaptic(); viewModel.reset() },
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Secondary80)
                ) {
                    Icon(Icons.Default.Refresh, "Reset", tint = Tertiary40, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun TimerInput(onStart: (Int, Int, Int) -> Unit) {
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = hours,
                onValueChange = { hours = it.take(2) },
                label = { Text("Hr") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = minutes,
                onValueChange = { minutes = it.take(2) },
                label = { Text("Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = seconds,
                onValueChange = { seconds = it.take(2) },
                label = { Text("Sec") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                val h = hours.toIntOrNull() ?: 0
                val m = minutes.toIntOrNull() ?: 0
                val s = seconds.toIntOrNull() ?: 0
                if (h > 0 || m > 0 || s > 0) onStart(h, m, s)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Primary40),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(56.dp)
        ) {
            Text("Start Timer", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun formatTimerTime(timeMillis: Long): String {
    val totalSeconds = (timeMillis + 999) / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
