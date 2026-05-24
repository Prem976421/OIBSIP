package com.example.calculatorapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculatorapp.ui.components.ButtonType
import com.example.calculatorapp.ui.components.CalcButton
import com.example.calculatorapp.viewmodel.CalculatorViewModel


@Composable
fun CalculatorScreen(
    vm: CalculatorViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FF)),
        verticalArrangement = Arrangement.Bottom
    ) {
        // ── Display Panel ──────────────────────────────────────────────
        DisplayPanel(
            expression = state.expression,
            result = state.result,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // ── Mode Toggle ────────────────────────────────────────────────
        ModeToggle(
            isScientific = state.isScientificMode,
            onToggle = { vm.toggleMode() }
        )

        // ── Keypad ─────────────────────────────────────────────────────
        AnimatedContent(
            targetState = state.isScientificMode,
            transitionSpec = {
                fadeIn(tween(200)) togetherWith fadeOut(tween(150))
            },
            label = "keypadMode"
        ) { isScientific ->
            if (isScientific) {
                ScientificKeypad(vm = vm)
            } else {
                BasicKeypad(vm = vm)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DisplayPanel(
    expression: String,
    result: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        // Expression (smaller, scrollable)
        Text(
            text = expression,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF6B7280),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        )

        Spacer(Modifier.height(8.dp))

        // Result (large, prominent)
        val resultFontSize = when {
            result.length > 14 -> 32.sp
            result.length > 9  -> 42.sp
            else               -> 56.sp
        }

        Text(
            text = result,
            fontSize = resultFontSize,
            fontWeight = FontWeight.Light,
            color = Color(0xFF111827),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ModeToggle(
    isScientific: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (isScientific) "Scientific" else "Basic",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF3B5BDB),
            fontWeight = FontWeight.SemiBold
        )
        Switch(
            checked = isScientific,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF3B5BDB),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBBC5E8)
            )
        )
    }
}

@Composable
private fun BasicKeypad(vm: CalculatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Row 1: AC, +/−, %, ÷
        Row(Modifier.fillMaxWidth()) {
            CalcButton("AC",  { vm.onClear()          }, Modifier.weight(1f), ButtonType.FUNCTION)
            CalcButton("+/−", { vm.onPlusMinus()      }, Modifier.weight(1f), ButtonType.FUNCTION)
            CalcButton("%",   { vm.onPercent()         }, Modifier.weight(1f), ButtonType.FUNCTION)
            CalcButton("÷",   { vm.onOperator("/")    }, Modifier.weight(1f), ButtonType.OPERATOR)
        }
        // Row 2: 7, 8, 9, ×
        Row(Modifier.fillMaxWidth()) {
            CalcButton("7",   { vm.onDigit("7")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("8",   { vm.onDigit("8")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("9",   { vm.onDigit("9")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("×",   { vm.onOperator("*")    }, Modifier.weight(1f), ButtonType.OPERATOR)
        }
        // Row 3: 4, 5, 6, −
        Row(Modifier.fillMaxWidth()) {
            CalcButton("4",   { vm.onDigit("4")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("5",   { vm.onDigit("5")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("6",   { vm.onDigit("6")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("−",   { vm.onOperator("-")    }, Modifier.weight(1f), ButtonType.OPERATOR)
        }
        // Row 4: 1, 2, 3, +
        Row(Modifier.fillMaxWidth()) {
            CalcButton("1",   { vm.onDigit("1")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("2",   { vm.onDigit("2")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("3",   { vm.onDigit("3")        }, Modifier.weight(1f), ButtonType.NUMBER)
            CalcButton("+",   { vm.onOperator("+")    }, Modifier.weight(1f), ButtonType.OPERATOR)
        }
        // Row 5: 0 (wide), ., ⌫, =
        Row(Modifier.fillMaxWidth()) {
            CalcButton("0",   { vm.onDigit("0")        }, Modifier.weight(2f), ButtonType.NUMBER, aspectRatio = 2f)
            CalcButton(".",   { vm.onDecimal()          }, Modifier.weight(1f), ButtonType.NUMBER)
            // Delete button custom
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF3B5BDB))
                    .height(70.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { vm.onDelete() }) {
                    Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
        // Row 6: = (full width)
        Row(Modifier.fillMaxWidth()) {
            CalcButton("=",   { vm.onEquals()          }, Modifier.weight(4f), ButtonType.EQUALS, aspectRatio = 4f, fontSize = 28.sp)
        }
    }
}

@Composable
private fun ScientificKeypad(vm: CalculatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        // Scientific row 1
        Row(Modifier.fillMaxWidth()) {
            CalcButton("sin",  { vm.onScientificFunction("sin")  }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 14.sp)
            CalcButton("cos",  { vm.onScientificFunction("cos")  }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 14.sp)
            CalcButton("tan",  { vm.onScientificFunction("tan")  }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 14.sp)
            CalcButton("π",    { vm.onScientificFunction("π")    }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 18.sp)
        }
        // Scientific row 2
        Row(Modifier.fillMaxWidth()) {
            CalcButton("log",  { vm.onScientificFunction("log")  }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 14.sp)
            CalcButton("ln",   { vm.onScientificFunction("ln")   }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 14.sp)
            CalcButton("√",    { vm.onScientificFunction("sqrt") }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 18.sp)
            CalcButton("e",    { vm.onScientificFunction("e")    }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 18.sp)
        }
        // Scientific row 3
        Row(Modifier.fillMaxWidth()) {
            CalcButton("x²",   { vm.onScientificFunction("x²")  }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 16.sp)
            CalcButton("x³",   { vm.onScientificFunction("x³")  }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 16.sp)
            CalcButton("1/x",  { vm.onScientificFunction("1/x") }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 14.sp)
            CalcButton("n!",   { vm.onScientificFunction("!")    }, Modifier.weight(1f), ButtonType.SCIENTIFIC, fontSize = 16.sp)
        }

        // Standard keypad rows (compact)
        Row(Modifier.fillMaxWidth()) {
            CalcButton("AC",  { vm.onClear()       }, Modifier.weight(1f), ButtonType.FUNCTION, fontSize = 16.sp)
            CalcButton("+/−", { vm.onPlusMinus()   }, Modifier.weight(1f), ButtonType.FUNCTION, fontSize = 16.sp)
            CalcButton("%",   { vm.onPercent()      }, Modifier.weight(1f), ButtonType.FUNCTION, fontSize = 16.sp)
            CalcButton("÷",   { vm.onOperator("/") }, Modifier.weight(1f), ButtonType.OPERATOR, fontSize = 18.sp)
        }
        Row(Modifier.fillMaxWidth()) {
            CalcButton("7", { vm.onDigit("7")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("8", { vm.onDigit("8")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("9", { vm.onDigit("9")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("×", { vm.onOperator("*") }, Modifier.weight(1f), ButtonType.OPERATOR, fontSize = 18.sp)
        }
        Row(Modifier.fillMaxWidth()) {
            CalcButton("4", { vm.onDigit("4")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("5", { vm.onDigit("5")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("6", { vm.onDigit("6")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("−", { vm.onOperator("-") }, Modifier.weight(1f), ButtonType.OPERATOR, fontSize = 18.sp)
        }
        Row(Modifier.fillMaxWidth()) {
            CalcButton("1", { vm.onDigit("1")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("2", { vm.onDigit("2")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("3", { vm.onDigit("3")    }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            CalcButton("+", { vm.onOperator("+") }, Modifier.weight(1f), ButtonType.OPERATOR, fontSize = 18.sp)
        }
        Row(Modifier.fillMaxWidth()) {
            CalcButton("0", { vm.onDigit("0")   }, Modifier.weight(2f), ButtonType.NUMBER, aspectRatio = 2f, fontSize = 18.sp)
            CalcButton(".", { vm.onDecimal()     }, Modifier.weight(1f), ButtonType.NUMBER, fontSize = 18.sp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF3B5BDB))
                    .height(58.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { vm.onDelete() }) {
                    Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            CalcButton("=", { vm.onEquals() }, Modifier.weight(4f), ButtonType.EQUALS, aspectRatio = 4f, fontSize = 24.sp)
        }
    }
}
