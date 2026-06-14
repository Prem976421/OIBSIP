package com.example.stopwatch.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary40,
    onPrimary = CalcButtonOperatorContent,
    secondary = Secondary40,
    onSecondary = CalcButtonOperatorContent,
    tertiary = Tertiary40,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = DisplayText,
    onSurface = DisplayText,
)

@Composable
fun StopWatchTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
