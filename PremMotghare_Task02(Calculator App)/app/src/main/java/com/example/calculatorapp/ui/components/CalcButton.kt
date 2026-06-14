package com.example.calculatorapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
    NUMBER, OPERATOR, FUNCTION, EQUALS, SCIENTIFIC
}

@Composable
fun CalcButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.NUMBER,
    fontSize: TextUnit = 22.sp
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "buttonScale"
    )

    val (bgColor, contentColor) = when (type) {
        ButtonType.NUMBER     -> Color(0xFFFFFFFF) to Color(0xFF1A1C2E)
        ButtonType.OPERATOR   -> Color(0xFF3B5BDB) to Color.White
        ButtonType.EQUALS     -> Color(0xFF1A3AAD) to Color.White
        ButtonType.FUNCTION   -> Color(0xFFE4E7F5) to Color(0xFF1D2B6B)
        ButtonType.SCIENTIFIC -> Color(0xFFEEF0FF) to Color(0xFF3B5BDB)
    }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        shadowElevation = if (type == ButtonType.NUMBER) 2.dp else 4.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                color = contentColor,
                fontSize = fontSize,
                fontWeight = if (type == ButtonType.EQUALS || type == ButtonType.OPERATOR)
                    FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}
