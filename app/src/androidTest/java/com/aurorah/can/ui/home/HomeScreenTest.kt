package com.aurorah.can.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.data.model.NewsItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysHeadingsAndContent() {
        val testStories = listOf(
            NewsItem(id = 1, title = "Library Opening in Sunbird Park", summary = "Mini-library launched.")
        )
        val testNeeds = listOf(CommunityNeed(needId = 1, title = "Winter Clothing Drive"))

        composeTestRule.setContent {
            AurorahHomeScreen(
                feedState = HomeFeedState(
                    stories = testStories,
                    needs = testNeeds,
                    isLoading = false
                ),
                onRefresh = {},
                onRequestAssistance = {},
                onExploreNeeds = {},
                onNeedClick = {},
                onNewsClick = {}
            )
        }

        // Verify key section headings are displayed
        composeTestRule.onNodeWithText("Ways to Help Nearby", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Community News & Stories", substring = true).assertIsDisplayed()
    }

    @Test
    fun homeScreen_clicksRequestAssistance_triggersCallback() {
        var assistanceClicked = false

        composeTestRule.setContent {
            AurorahHomeScreen(
                feedState = HomeFeedState(isLoading = false),
                onRefresh = {},
                onRequestAssistance = { assistanceClicked = true },
                onExploreNeeds = {},
                onNeedClick = {},
                onNewsClick = {}
            )
        }

        // Click request assistance button in hero card
        composeTestRule.onNodeWithText("Request Assistance", substring = true).performClick()
        assertTrue(assistanceClicked)
    }

    @Test
    fun homeScreen_displaysEmptyNeedsState_whenNeedsEmpty() {
        composeTestRule.setContent {
            AurorahHomeScreen(
                feedState = HomeFeedState(needs = emptyList(), isLoading = false),
                onRefresh = {},
                onRequestAssistance = {},
                onExploreNeeds = {},
                onNeedClick = {},
                onNewsClick = {}
            )
        }

        composeTestRule.onNodeWithText("Explore Ways to Help", substring = true).assertIsDisplayed()
    }
}
