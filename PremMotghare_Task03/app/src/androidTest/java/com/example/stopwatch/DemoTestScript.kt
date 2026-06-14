package com.example.stopwatch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoTestScript {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun runDemoTest() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // 1. World Clock Section
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Select Country Capital").performClick()
        Thread.sleep(1000)
        // Scroll inside dropdown using UiAutomator or Compose
        // "🇲🇽 Mexico - Mexico City"
        val mexicoNode = composeTestRule.onNodeWithText("🇲🇽 Mexico - Mexico City")
        // Expose DropdownMenu doesn't scroll easily via simple performClick if it's offscreen.
        // Let's use UiAutomator to scroll and find it.
        val mexicoText = device.findObject(UiSelector().textContains("Mexico"))
        mexicoText.waitForExists(3000)
        if(!mexicoText.exists()) {
            device.swipe(500, 1500, 500, 500, 50) // Swipe up
            Thread.sleep(1000)
            device.swipe(500, 1500, 500, 500, 50) // Swipe up again
        }
        mexicoText.click()
        Thread.sleep(2000)

        // 2. Alarm Section
        composeTestRule.onNodeWithText("Alarm").performClick()
        Thread.sleep(1000)
        
        // Handle Android 13+ Notification Permission if it appears
        val allowButton = device.findObject(UiSelector().textMatches("(?i)Allow|ALLOW"))
        if (allowButton.exists()) {
            allowButton.click()
            Thread.sleep(1000)
        }

        // Click Add Alarm FAB (It has an icon, no text, let's use content description or UiAutomator)
        val addAlarmFab = device.findObject(UiSelector().descriptionContains("Add Alarm"))
        if(addAlarmFab.exists()) addAlarmFab.click() else {
            // fallback, click middle bottom
            device.click(device.displayWidth / 2, device.displayHeight - 300)
        }
        
        Thread.sleep(1500)
        // Native TimePickerDialog interactions
        val keyboardIcon = device.findObject(UiSelector().descriptionContains("Switch to text input"))
        if (keyboardIcon.exists()) {
            keyboardIcon.click()
            Thread.sleep(1000)
            
            // Enter Hour
            val hourInput = device.findObject(UiSelector().className("android.widget.EditText").instance(0))
            if(hourInput.exists()) { hourInput.clearTextField(); hourInput.setText("5") }
            Thread.sleep(500)
            
            // Enter Minute
            val minInput = device.findObject(UiSelector().className("android.widget.EditText").instance(1))
            if(minInput.exists()) { minInput.clearTextField(); minInput.setText("00") }
            Thread.sleep(500)

            // Select AM
            val amSpinner = device.findObject(UiSelector().text("AM"))
            if(amSpinner.exists()) amSpinner.click()
            
            Thread.sleep(1000)
        }
        val okBtn = device.findObject(UiSelector().textMatches("(?i)OK"))
        if(okBtn.exists()) okBtn.click()
        Thread.sleep(2000)

        // 3. Stopwatch Section
        composeTestRule.onNodeWithText("Stopwatch").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithContentDescription("Play").performClick()
        Thread.sleep(5000) // 5 seconds
        composeTestRule.onNodeWithContentDescription("Lap").performClick() // Lap 1
        Thread.sleep(5000) // 5 seconds
        composeTestRule.onNodeWithContentDescription("Lap").performClick() // Lap 2
        Thread.sleep(1000)
        composeTestRule.onNodeWithContentDescription("Pause").performClick()
        Thread.sleep(2000)

        // 4. Timer Section
        composeTestRule.onNodeWithText("Timer").performClick()
        Thread.sleep(1000)
        
        // Scroll Sec wheel to 05
        composeTestRule.onNodeWithTag("picker_Sec").performScrollToIndex(5)
        Thread.sleep(1500)
        
        // Start Timer
        composeTestRule.onNodeWithText("Start Timer").performClick()
        
        // Wait for it to hit 0 and ring (5 seconds + 1 sec buffer)
        Thread.sleep(6000)
        
        // Wait 5 more seconds to hear the loud alarm ringtone
        Thread.sleep(5000)
    }
}
