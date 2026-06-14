package com.example.stopwatch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
        
        // Find the Select Country Capital dropdown
        val dropdown = device.findObject(UiSelector().textContains("Select Country Capital"))
        if (dropdown.waitForExists(3000)) {
            dropdown.click()
            Thread.sleep(1500)
            
            // Scroll to find Mexico
            val mexicoText = device.findObject(UiSelector().textContains("Mexico"))
            if (!mexicoText.waitForExists(2000)) {
                device.swipe(500, 1500, 500, 500, 50) // Swipe up
                Thread.sleep(1000)
                device.swipe(500, 1500, 500, 500, 50) // Swipe up again
            }
            if (mexicoText.exists()) {
                mexicoText.click()
            }
        }
        Thread.sleep(2000)

        // 2. Alarm Section
        val alarmTab = device.findObject(UiSelector().text("Alarm"))
        if (alarmTab.waitForExists(2000)) alarmTab.click()
        Thread.sleep(1500)
        
        // Handle Notification Permission if it appears
        val allowButton = device.findObject(UiSelector().textMatches("(?i)Allow|ALLOW"))
        if (allowButton.exists()) {
            allowButton.click()
            Thread.sleep(1000)
        }

        // Click Add Alarm FAB
        val addAlarmFab = device.findObject(UiSelector().descriptionContains("Add Alarm"))
        if(addAlarmFab.exists()) addAlarmFab.click() else {
            device.click(device.displayWidth / 2, device.displayHeight - 300)
        }
        
        Thread.sleep(1500)
        // Native TimePickerDialog interactions
        val keyboardIcon = device.findObject(UiSelector().descriptionContains("Switch to text input"))
        if (keyboardIcon.exists()) {
            keyboardIcon.click()
            Thread.sleep(1000)
            
            val hourInput = device.findObject(UiSelector().className("android.widget.EditText").instance(0))
            if(hourInput.exists()) { hourInput.clearTextField(); hourInput.setText("5") }
            Thread.sleep(500)
            
            val minInput = device.findObject(UiSelector().className("android.widget.EditText").instance(1))
            if(minInput.exists()) { minInput.clearTextField(); minInput.setText("00") }
            Thread.sleep(500)

            val amSpinner = device.findObject(UiSelector().textMatches("(?i)AM"))
            if(amSpinner.exists()) amSpinner.click()
            Thread.sleep(1000)
        }
        val okBtn = device.findObject(UiSelector().textMatches("(?i)OK"))
        if(okBtn.exists()) okBtn.click()
        Thread.sleep(2000)

        // 3. Stopwatch Section
        val stopwatchTab = device.findObject(UiSelector().text("Stopwatch"))
        if (stopwatchTab.waitForExists(2000)) stopwatchTab.click()
        Thread.sleep(1500)
        
        val playBtn = device.findObject(UiSelector().descriptionMatches("(?i)Play"))
        if (playBtn.exists()) playBtn.click()
        Thread.sleep(5000) 
        
        val lapBtn = device.findObject(UiSelector().descriptionMatches("(?i)Lap"))
        if (lapBtn.exists()) lapBtn.click()
        Thread.sleep(5000) 
        
        if (lapBtn.exists()) lapBtn.click()
        Thread.sleep(1000)
        
        val pauseBtn = device.findObject(UiSelector().descriptionMatches("(?i)Pause"))
        if (pauseBtn.exists()) pauseBtn.click()
        Thread.sleep(2000)

        // 4. Timer Section
        val timerTab = device.findObject(UiSelector().text("Timer"))
        if (timerTab.waitForExists(2000)) timerTab.click()
        Thread.sleep(1500)
        
        // Scroll Sec wheel to 05 using compose rule
        try {
            composeTestRule.onNodeWithTag("picker_Sec").performScrollToIndex(5)
        } catch (e: Exception) {
            // fallback, do nothing
        }
        Thread.sleep(1500)
        
        // Start Timer
        val startTimerBtn = device.findObject(UiSelector().textContains("Start Timer"))
        if (startTimerBtn.exists()) startTimerBtn.click()
        
        // Wait 11 seconds for ring
        Thread.sleep(11000)
    }
}
