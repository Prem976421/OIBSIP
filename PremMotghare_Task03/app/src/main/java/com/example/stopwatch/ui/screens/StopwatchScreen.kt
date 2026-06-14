package com.example.stopwatch.ui.screens

import android.content.Intent
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.ui.theme.*
import com.example.stopwatch.viewmodel.Lap
import com.example.stopwatch.viewmodel.StopwatchViewModel

@Composable
fun StopwatchScreen(viewModel: StopwatchViewModel = viewModel()) {
    val timeMillis by viewModel.timeMillis.collectAsState()
    val currentLapTimeMillis by viewModel.currentLapTimeMillis.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val laps by viewModel.laps.collectAsState()
    val fastestLapId by viewModel.fastestLapId.collectAsState()
    val slowestLapId by viewModel.slowestLapId.collectAsState()

    val context = LocalContext.current
    val view = LocalView.current

    fun performHaptic() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(300.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "infinite")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Secondary80,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )

                val gradientBrush = Brush.sweepGradient(listOf(Tertiary40, Primary40, Primary80, Tertiary40))

                if (isPlaying) {
                    rotate(rotation) {
                        drawArc(
                            brush = gradientBrush,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                } else if (timeMillis > 0) {
                    drawArc(
                        brush = gradientBrush,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(timeMillis),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Primary40
                )
                
                if (laps.isNotEmpty() || currentLapTimeMillis > 0) {
                    Text(
                        text = "Lap: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = Secondary40,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { performHaptic(); viewModel.reset() },
                modifier = Modifier.size(64.dp).clip(CircleShape).background(Secondary80)
            ) {
                Icon(Icons.Default.Refresh, "Reset", tint = Secondary40, modifier = Modifier.size(32.dp))
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
                onClick = { performHaptic(); viewModel.lap() },
                modifier = Modifier.size(64.dp).clip(CircleShape).background(Secondary80)
            ) {
                Icon(Icons.Default.Flag, "Lap", tint = Tertiary40, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (laps.isNotEmpty()) {
            Text(
                text = "Laps",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Secondary40,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp, start = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(laps, key = { it.id }) { lap ->
                    AnimatedVisibility(visible = true, enter = slideInVertically() + fadeIn()) {
                        val isFastest = lap.id == fastestLapId
                        val isSlowest = lap.id == slowestLapId
                        LapItem(lap = lap, isFastest = isFastest, isSlowest = isSlowest) {
                            viewModel.deleteLap(lap.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LapItem(lap: Lap, isFastest: Boolean, isSlowest: Boolean, onDelete: () -> Unit) {
    val highlightColor = when {
        isFastest -> Tertiary40
        isSlowest -> Color(0xFFE53935)
        else -> Secondary40
    }

    val backgroundColor = when {
        isFastest -> Color(0xFFE0F2F1)
        isSlowest -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Lap ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = highlightColor
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "+",
                    fontSize = 16.sp,
                    color = highlightColor,
                    fontWeight = if (isFastest || isSlowest) FontWeight.Bold else FontWeight.Normal,
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
        
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Lap", tint = Secondary40)
        }
    }
}

fun formatTime(timeMillis: Long, includeMillis: Boolean = true): String {
    val totalSeconds = timeMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = (timeMillis % 1000) / 10

    return if (includeMillis) {
        String.format("%02d:%02d.%02d", minutes, seconds, millis)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
