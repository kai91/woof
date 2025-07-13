package com.kai.woof

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kai.woof.screen.start.StartActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for StartActivity
 */
@RunWith(AndroidJUnit4::class)
class StartActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<StartActivity>()

    @Test
    fun startButton_isVisible() {
        // Verify that the start button with text "Start" is displayed
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
    }
} 