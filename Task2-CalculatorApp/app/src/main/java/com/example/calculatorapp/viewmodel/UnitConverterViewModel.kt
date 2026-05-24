package com.example.calculatorapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ConversionCategory(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature")
}

data class UnitConverterState(
    val category: ConversionCategory = ConversionCategory.LENGTH,
    val fromUnit: String = "Meters",
    val toUnit: String = "Feet",
    val inputValue: String = "",
    val result: String = ""
)

class UnitConverterViewModel : ViewModel() {

    private val _state = MutableStateFlow(UnitConverterState())
    val state: StateFlow<UnitConverterState> = _state.asStateFlow()

    val unitsByCategory: Map<ConversionCategory, List<String>> = mapOf(
        ConversionCategory.LENGTH to listOf(
            "Meters", "Feet", "Inches", "Centimeters", "Kilometers", "Miles", "Yards", "Millimeters"
        ),
        ConversionCategory.WEIGHT to listOf(
            "Kilograms", "Pounds", "Grams", "Ounces", "Milligrams", "Tonnes", "Stone"
        ),
        ConversionCategory.TEMPERATURE to listOf(
            "Celsius", "Fahrenheit", "Kelvin"
        )
    )

    fun setCategory(cat: ConversionCategory) {
        val units = unitsByCategory[cat] ?: emptyList()
        _state.value = UnitConverterState(
            category = cat,
            fromUnit = units.getOrElse(0) { "" },
            toUnit = units.getOrElse(1) { "" },
            inputValue = "",
            result = ""
        )
    }

    fun setFromUnit(unit: String) {
        _state.value = _state.value.copy(fromUnit = unit)
        convert()
    }

    fun setToUnit(unit: String) {
        _state.value = _state.value.copy(toUnit = unit)
        convert()
    }

    fun setInput(value: String) {
        _state.value = _state.value.copy(inputValue = value)
        convert()
    }

    fun swapUnits() {
        _state.value = _state.value.copy(
            fromUnit = _state.value.toUnit,
            toUnit = _state.value.fromUnit
        )
        convert()
    }

    private fun convert() {
        val input = _state.value.inputValue.toDoubleOrNull()
        if (input == null) {
            _state.value = _state.value.copy(result = "")
            return
        }
        val result = when (_state.value.category) {
            ConversionCategory.LENGTH      -> convertLength(input, _state.value.fromUnit, _state.value.toUnit)
            ConversionCategory.WEIGHT      -> convertWeight(input, _state.value.fromUnit, _state.value.toUnit)
            ConversionCategory.TEMPERATURE -> convertTemperature(input, _state.value.fromUnit, _state.value.toUnit)
        }
        val formatted = formatResult(result)
        _state.value = _state.value.copy(result = formatted)
    }

    // --- Length (base: meters) ---
    private val lengthToMeters = mapOf(
        "Meters" to 1.0,
        "Feet" to 0.3048,
        "Inches" to 0.0254,
        "Centimeters" to 0.01,
        "Kilometers" to 1000.0,
        "Miles" to 1609.344,
        "Yards" to 0.9144,
        "Millimeters" to 0.001
    )

    private fun convertLength(value: Double, from: String, to: String): Double {
        val inMeters = value * (lengthToMeters[from] ?: 1.0)
        return inMeters / (lengthToMeters[to] ?: 1.0)
    }

    // --- Weight (base: grams) ---
    private val weightToGrams = mapOf(
        "Kilograms" to 1000.0,
        "Pounds" to 453.592,
        "Grams" to 1.0,
        "Ounces" to 28.3495,
        "Milligrams" to 0.001,
        "Tonnes" to 1_000_000.0,
        "Stone" to 6350.29
    )

    private fun convertWeight(value: Double, from: String, to: String): Double {
        val inGrams = value * (weightToGrams[from] ?: 1.0)
        return inGrams / (weightToGrams[to] ?: 1.0)
    }

    // --- Temperature ---
    private fun convertTemperature(value: Double, from: String, to: String): Double {
        val inCelsius = when (from) {
            "Celsius"    -> value
            "Fahrenheit" -> (value - 32) * 5.0 / 9.0
            "Kelvin"     -> value - 273.15
            else         -> value
        }
        return when (to) {
            "Celsius"    -> inCelsius
            "Fahrenheit" -> inCelsius * 9.0 / 5.0 + 32
            "Kelvin"     -> inCelsius + 273.15
            else         -> inCelsius
        }
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble() && !value.isInfinite()) {
            value.toLong().toString()
        } else {
            "%.8g".format(value).trimEnd('0').trimEnd('.')
        }
    }
}
