package com.example.calculatorapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculatorapp.viewmodel.ConversionCategory
import com.example.calculatorapp.viewmodel.UnitConverterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    vm: UnitConverterViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val units = vm.unitsByCategory[state.category] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FF))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Title
        Text(
            text = "Unit Converter",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF111827),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Convert length, weight & temperature",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.height(24.dp))

        // Category selector tabs
        CategoryTabs(
            selected = state.category,
            onSelect = { vm.setCategory(it) }
        )

        Spacer(Modifier.height(24.dp))

        // Conversion card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // From unit
                UnitDropdown(
                    label = "From",
                    selectedUnit = state.fromUnit,
                    units = units,
                    onUnitSelected = { vm.setFromUnit(it) }
                )

                Spacer(Modifier.height(12.dp))

                // Input field
                OutlinedTextField(
                    value = state.inputValue,
                    onValueChange = { vm.setInput(it) },
                    label = { Text("Enter value") },
                    placeholder = { Text("0") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3B5BDB),
                        focusedLabelColor = Color(0xFF3B5BDB),
                        cursorColor = Color(0xFF3B5BDB)
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Swap button
                IconButton(
                    onClick = { vm.swapUnits() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFEEF0FF))
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap units",
                        tint = Color(0xFF3B5BDB),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // To unit
                UnitDropdown(
                    label = "To",
                    selectedUnit = state.toUnit,
                    units = units,
                    onUnitSelected = { vm.setToUnit(it) }
                )

                Spacer(Modifier.height(20.dp))

                // Result display
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEEF0FF)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Result",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (state.result.isEmpty()) "—" else "${state.result} ${state.toUnit}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3B5BDB)
                        )
                        if (state.inputValue.isNotEmpty() && state.result.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${state.inputValue} ${state.fromUnit} = ${state.result} ${state.toUnit}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun CategoryTabs(
    selected: ConversionCategory,
    onSelect: (ConversionCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE4E7F5))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ConversionCategory.entries.forEach { category ->
            val isSelected = category == selected
            Button(
                onClick = { onSelect(category) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF3B5BDB) else Color.Transparent,
                    contentColor = if (isSelected) Color.White else Color(0xFF3B5BDB)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isSelected) 2.dp else 0.dp
                )
            ) {
                Text(
                    text = category.displayName,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    label: String,
    selectedUnit: String,
    units: List<String>,
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedUnit,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3B5BDB),
                    unfocusedBorderColor = Color(0xFFBBC5E8),
                    focusedLabelColor = Color(0xFF3B5BDB)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            onUnitSelected(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
