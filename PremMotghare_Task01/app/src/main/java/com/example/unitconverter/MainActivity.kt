package com.example.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitConverterApp()
        }
    }
}

private enum class PickerTarget {
    FROM,
    TO
}

private data class ConversionResult(
    val displayValue: String,
    val displayUnit: String,
    val subtitle: String,
    val isValid: Boolean,
    val numericResult: Double?
)

@Composable
private fun UnitConverterApp() {
    var selectedCategory by rememberSaveable { mutableStateOf(ConversionCategory.LENGTH) }
    var inputValue by rememberSaveable { mutableStateOf("") }
    var fromUnit by rememberSaveable { mutableStateOf(Converter.unitsFor(ConversionCategory.LENGTH).first()) }
    var toUnit by rememberSaveable { mutableStateOf(Converter.unitsFor(ConversionCategory.LENGTH).last()) }
    var pickerTarget by rememberSaveable { mutableStateOf<PickerTarget?>(null) }
    val availableUnits = remember(selectedCategory) {
        Converter.unitsFor(selectedCategory)
    }

    androidx.compose.runtime.LaunchedEffect(selectedCategory) {
        if (fromUnit.category != selectedCategory) {
            fromUnit = availableUnits.first()
        }
        if (toUnit.category != selectedCategory) {
            toUnit = availableUnits.last()
        }
    }

    val conversionResult = remember(inputValue, selectedCategory, fromUnit, toUnit) {
        calculateResult(inputValue, selectedCategory, fromUnit, toUnit)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(240, 244, 244)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(240, 244, 244))
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                }
            )

            InlineInputField(
                value = inputValue,
                unitLabel = fromUnit.displayName,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                        inputValue = newValue
                    } else {
                        inputValue = newValue
                    }
                }
            )

            OutputField(
                value = if (conversionResult.isValid) {
                    "${conversionResult.displayValue} ${conversionResult.displayUnit}".trim()
                } else {
                    conversionResult.displayValue
                }
            )

            BottomSheetSelector(
                label = "From",
                value = fromUnit.displayName,
                onClick = { pickerTarget = PickerTarget.FROM }
            )

            SwapButton(
                onSwap = {
                    val currentConverted = conversionResult.numericResult
                    val previousFrom = fromUnit
                    fromUnit = toUnit
                    toUnit = previousFrom
                    if (currentConverted != null && conversionResult.isValid) {
                        inputValue = formatNumber(currentConverted)
                    }
                }
            )

            BottomSheetSelector(
                label = "To",
                value = toUnit.displayName,
                onClick = { pickerTarget = PickerTarget.TO }
            )
        }
    }

    if (pickerTarget != null) {
        UnitPickerSheet(
            options = availableUnits,
            onDismiss = { pickerTarget = null },
            onSelected = { unit ->
                if (pickerTarget == PickerTarget.FROM) {
                    fromUnit = unit
                } else {
                    toUnit = unit
                }
                pickerTarget = null
            }
        )
    }
}

@Composable
private fun OutputField(value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Output",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(28, 43, 43)
            )
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(255, 255, 255)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(28, 43, 43)
                )
            )
        }
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: ConversionCategory,
    onCategorySelected: (ConversionCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ConversionCategory.entries.forEach { category ->
            val selected = category == selectedCategory
            val backgroundColor by animateColorAsState(
                targetValue = if (selected) Color(13, 148, 136) else Color(240, 244, 244),
                animationSpec = tween(durationMillis = 220),
                label = "tabBackground"
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) Color(255, 255, 255) else Color(28, 43, 43),
                animationSpec = tween(durationMillis = 220),
                label = "tabText"
            )

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                color = backgroundColor,
                shadowElevation = 0.dp,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (selected) Color(13, 148, 136) else Color(178, 216, 216)
                ),
                onClick = { onCategorySelected(category) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.displayName,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun InlineInputField(
    value: String,
    unitLabel: String,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Input",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(28, 43, 43)
            )
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            textStyle = TextStyle(
                fontSize = 24.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(28, 43, 43)
            ),
            placeholder = {
                Text(
                    text = "Enter a number",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(90, 120, 120)
                    )
                )
            },
            suffix = {
                Text(
                    text = unitLabel,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(28, 43, 43)
                    )
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(255, 255, 255),
                unfocusedContainerColor = Color(255, 255, 255),
                disabledContainerColor = Color(255, 255, 255),
                focusedBorderColor = Color(13, 148, 136),
                unfocusedBorderColor = Color(178, 216, 216),
                focusedTextColor = Color(28, 43, 43),
                unfocusedTextColor = Color(28, 43, 43),
                cursorColor = Color(13, 148, 136)
            )
        )
    }
}

@Composable
private fun BottomSheetSelector(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(28, 43, 43)
            )
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(255, 255, 255),
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(178, 216, 216)),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(28, 43, 43)
                    )
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = "$label options",
                    tint = Color(13, 148, 136)
                )
            }
        }
    }
}

@Composable
private fun SwapButton(onSwap: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color(230, 244, 244)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            IconButton(
                onClick = onSwap,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SwapVert,
                    contentDescription = "Swap units",
                    tint = Color(13, 148, 136)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitPickerSheet(
    options: List<UnitOption>,
    onDismiss: () -> Unit,
    onSelected: (UnitOption) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(255, 255, 255)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(255, 255, 255),
                    shadowElevation = 0.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(178, 216, 216)),
                    onClick = { onSelected(option) }
                ) {
                    Text(
                        text = option.displayName,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(28, 43, 43)
                        )
                    )
                }
            }
        }
    }
}

private fun calculateResult(
    inputValue: String,
    category: ConversionCategory,
    fromUnit: UnitOption,
    toUnit: UnitOption
): ConversionResult {
    if (inputValue.isBlank()) {
        return ConversionResult(
            displayValue = "0",
            displayUnit = toUnit.displayName,
            subtitle = "Enter a number to see the conversion",
            isValid = false,
            numericResult = null
        )
    }

    val numericValue = inputValue.toDoubleOrNull()
        ?: return ConversionResult(
            displayValue = "Invalid input",
            displayUnit = "",
            subtitle = "Enter a valid number to continue",
            isValid = false,
            numericResult = null
        )

    val converted = Converter.convert(numericValue, category, fromUnit, toUnit)
    return ConversionResult(
        displayValue = formatNumber(converted),
        displayUnit = toUnit.displayName,
        subtitle = "${formatNumber(numericValue)} ${fromUnit.displayName} -> ${formatNumber(converted)} ${toUnit.displayName}",
        isValid = true,
        numericResult = converted
    )
}

private fun formatNumber(value: Double): String {
    val formatted = String.format(Locale.US, "%.4f", value)
    return formatted.trimEnd('0').trimEnd('.').ifEmpty { "0" }
}

@Preview(showBackground = true)
@Composable
private fun UnitConverterPreview() {
    UnitConverterApp()
}
