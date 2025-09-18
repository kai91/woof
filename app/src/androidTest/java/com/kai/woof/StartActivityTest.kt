package com.kai.woof

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kai.woof.screen.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for StartActivity
 */
@RunWith(AndroidJUnit4::class)
class StartActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun startButton_isVisible() {
        // Verify that the start button with text "Start" is displayed
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
    }

    @Test
    fun loadingState_notDisplayedInitially() {
        // Verify that loading state is not displayed initially
        composeTestRule.onNodeWithText("Gathering the puppies...").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Fetching dog breeds and photos").assertIsNotDisplayed()
    }

    @Test
    fun loadingState_displayedAfterClickingStart() {
        // Verify that UI elements are properly arranged
        // Check that the start button is visible and clickable
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()

        composeTestRule.onNodeWithText("Start").performClick()
        
        // Verify loading elements are shown
        composeTestRule.onNodeWithText("Gathering the puppies...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fetching dog breeds and photos").assertIsDisplayed()
    }
} 