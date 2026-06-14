package com.example.stopwatch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorRecordingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun clickAndSleep(text: String, delay: Long = 600) {
        // Find the node by text and click it
        composeTestRule.onNodeWithText(text).performClick()
        // Wait for UI to update (animations, state changes)
        composeTestRule.waitForIdle()
        // Sleep to make the sequence viewable for a screen recording
        Thread.sleep(delay)
    }

    private fun toggleScientific(delay: Long = 800) {
        composeTestRule.onNodeWithTag("ModeToggle").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(delay)
    }

    @Test
    fun automatedRecordingSequence() {
        // Initial pause to allow starting screen recording
        Thread.sleep(2000)

        // 1. Basic Math: 2 + 3 * 4 = 14
        clickAndSleep("2")
        clickAndSleep("+")
        clickAndSleep("3")
        clickAndSleep("×")
        clickAndSleep("4")
        clickAndSleep("=", 1500)
        clickAndSleep("AC")

        // 2. Percentage: 50 %
        clickAndSleep("5")
        clickAndSleep("0")
        clickAndSleep("%")
        clickAndSleep("=", 1500)
        clickAndSleep("AC")

        // 3. Divide by zero
        clickAndSleep("8")
        clickAndSleep("÷")
        clickAndSleep("0")
        clickAndSleep("=", 1500)
        clickAndSleep("AC")

        // 4. Switch to Scientific Mode
        toggleScientific()

        // 5. Square: 5 x²
        clickAndSleep("5")
        clickAndSleep("x²", 1500)
        clickAndSleep("AC")

        // 6. Square root: √ 16
        clickAndSleep("1")
        clickAndSleep("6")
        clickAndSleep("√", 1500)
        clickAndSleep("AC")

        // 7. Sine: sin 90
        clickAndSleep("9")
        clickAndSleep("0")
        clickAndSleep("sin", 1500)
        clickAndSleep("AC")

        // Final pause before test completes and closes the app
        Thread.sleep(2000)
    }
}

