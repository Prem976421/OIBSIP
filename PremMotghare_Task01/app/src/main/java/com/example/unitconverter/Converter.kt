package com.example.unitconverter

enum class ConversionCategory(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature")
}

enum class UnitOption(
    val displayName: String,
    val category: ConversionCategory
) {
    METER("Meter", ConversionCategory.LENGTH),
    KILOMETER("Kilometer", ConversionCategory.LENGTH),
    CENTIMETER("Centimeter", ConversionCategory.LENGTH),
    GRAM("Gram", ConversionCategory.WEIGHT),
    KILOGRAM("Kilogram", ConversionCategory.WEIGHT),
    CELSIUS("Celsius", ConversionCategory.TEMPERATURE),
    FAHRENHEIT("Fahrenheit", ConversionCategory.TEMPERATURE)
}

object Converter {
    fun unitsFor(category: ConversionCategory): List<UnitOption> {
        return UnitOption.entries.filter { it.category == category }
    }

    fun convert(
        value: Double,
        category: ConversionCategory,
        from: UnitOption,
        to: UnitOption
    ): Double {
        if (from == to) return value

        return when (category) {
            ConversionCategory.LENGTH -> convertLength(value, from, to)
            ConversionCategory.WEIGHT -> convertWeight(value, from, to)
            ConversionCategory.TEMPERATURE -> convertTemperature(value, from, to)
        }
    }

    private fun convertLength(value: Double, from: UnitOption, to: UnitOption): Double {
        val valueInMeters = when (from) {
            UnitOption.METER -> value
            UnitOption.KILOMETER -> value * 1000
            UnitOption.CENTIMETER -> value / 100
            else -> value
        }

        return when (to) {
            UnitOption.METER -> valueInMeters
            UnitOption.KILOMETER -> valueInMeters / 1000
            UnitOption.CENTIMETER -> valueInMeters * 100
            else -> valueInMeters
        }
    }

    private fun convertWeight(value: Double, from: UnitOption, to: UnitOption): Double {
        val valueInGrams = when (from) {
            UnitOption.GRAM -> value
            UnitOption.KILOGRAM -> value * 1000
            else -> value
        }

        return when (to) {
            UnitOption.GRAM -> valueInGrams
            UnitOption.KILOGRAM -> valueInGrams / 1000
            else -> valueInGrams
        }
    }

    private fun convertTemperature(value: Double, from: UnitOption, to: UnitOption): Double {
        return when {
            from == UnitOption.CELSIUS && to == UnitOption.FAHRENHEIT -> (value * 9 / 5) + 32
            from == UnitOption.FAHRENHEIT && to == UnitOption.CELSIUS -> (value - 32) * 5 / 9
            else -> value
        }
    }
}
