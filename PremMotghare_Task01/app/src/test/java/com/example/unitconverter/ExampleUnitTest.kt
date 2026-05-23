package com.example.unitconverter

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun kilometer_to_meter_conversion_is_correct() {
        val result = Converter.convert(
            value = 2.0,
            category = ConversionCategory.LENGTH,
            from = UnitOption.KILOMETER,
            to = UnitOption.METER
        )

        assertEquals(2000.0, result, 0.001)
    }

    @Test
    fun celsius_to_fahrenheit_conversion_is_correct() {
        val result = Converter.convert(
            value = 0.0,
            category = ConversionCategory.TEMPERATURE,
            from = UnitOption.CELSIUS,
            to = UnitOption.FAHRENHEIT
        )

        assertEquals(32.0, result, 0.001)
    }
}
