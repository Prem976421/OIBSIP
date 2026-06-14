package com.example.stopwatch.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
                        brush = Brush.sweepGradient(listOf(Tertiary80, Secondary40, Tertiary40, Primary40, Tertiary80)),
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
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            WheelPicker(range = 0..23, value = hours, onValueChange = { hours = it }, label = "Hr")
            WheelPicker(range = 0..59, value = minutes, onValueChange = { minutes = it }, label = "Min")
            WheelPicker(range = 0..59, value = seconds, onValueChange = { seconds = it }, label = "Sec")
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { 
                if (hours > 0 || minutes > 0 || seconds > 0) onStart(hours, minutes, seconds)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Primary40),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(56.dp)
        ) {
            Text("Start Timer", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(range: IntRange, value: Int, onValueChange: (Int) -> Unit, label: String) {
    val list = range.toList()
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = if (value in range) value - range.first else 0)
    
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            val newValue = range.first + index
            if (newValue != value) {
                onValueChange(newValue)
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = Primary40, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(150.dp)
                .background(Secondary80.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                modifier = Modifier.fillMaxSize().testTag("picker_$label"),
                contentPadding = PaddingValues(vertical = 50.dp)
            ) {
                items(list) { item ->
                    val isSelected = value == item
                    Box(
                        modifier = Modifier.height(50.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format("%02d", item),
                            fontSize = if (isSelected) 28.sp else 20.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Primary40 else Secondary40
                        )
                    }
                }
            }
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
