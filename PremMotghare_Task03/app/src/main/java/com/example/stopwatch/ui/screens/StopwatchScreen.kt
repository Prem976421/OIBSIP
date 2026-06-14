package com.example.stopwatch.ui.screens

import android.content.Intent
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.ui.theme.Primary40
import com.example.stopwatch.ui.theme.Primary80
import com.example.stopwatch.ui.theme.Secondary40
import com.example.stopwatch.ui.theme.Secondary80
import com.example.stopwatch.ui.theme.SurfaceLight
import com.example.stopwatch.ui.theme.Tertiary40
import com.example.stopwatch.ui.theme.Tertiary80
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

    fun shareLaps() {
        if (laps.isEmpty()) return
        val shareText = buildString {
            appendLine("My Stopwatch Laps:")
            appendLine("Total Time: ${formatTime(timeMillis)}")
            appendLine("-------------------")
            laps.reversed().forEach { lap ->
                appendLine("Lap ${lap.id}: ${formatTime(lap.lapTime)} (Total: ${formatTime(lap.totalTime)})")
            }
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Laps"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (laps.isNotEmpty()) {
                IconButton(onClick = { 
                    performHaptic()
                    shareLaps() 
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Secondary40
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Display with Circular Progress
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(300.dp)
        ) {
            // Circular Progress Animation
            val progress = (timeMillis % 60000) / 60000f
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(durationMillis = 100), label = "progress"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background Track
                drawArc(
                    color = Secondary80,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )

                // Foreground Animated Progress (Gradient)
                val gradient = Brush.sweepGradient(
                    colors = listOf(Primary40, Tertiary40, Primary40)
                )
                drawArc(
                    brush = gradient,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(timeMillis),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Primary40
                )
                
                // Current Lap Time
                if (laps.isNotEmpty() || currentLapTimeMillis > 0) {
                    Text(
                        text = "Lap: ${formatTime(currentLapTimeMillis)}",
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

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            IconButton(
                onClick = {
                    performHaptic()
                    viewModel.reset()
                },
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
                onClick = {
                    performHaptic()
                    viewModel.togglePlayPause()
                },
                modifier = Modifier
                    .size(88.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Primary40, Primary80)
                        )
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            // Lap Button
            IconButton(
                onClick = {
                    performHaptic()
                    viewModel.lap()
                },
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
                    .padding(bottom = 16.dp, start = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(laps, key = { it.id }) { lap ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        val isFastest = lap.id == fastestLapId
                        val isSlowest = lap.id == slowestLapId
                        LapItem(lap = lap, isFastest = isFastest, isSlowest = isSlowest)
                    }
                }
            }
        }
    }
}

@Composable
fun LapItem(lap: Lap, isFastest: Boolean, isSlowest: Boolean) {
    val highlightColor = when {
        isFastest -> Color(0xFF4CAF50) // Green
        isSlowest -> Color(0xFFF44336) // Red
        else -> Secondary40
    }

    val backgroundColor = when {
        isFastest -> Color(0xFFE8F5E9)
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
        Text(
            text = "Lap ${lap.id}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = highlightColor
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(
                text = "+${formatTime(lap.lapTime, includeMillis = true)}",
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
