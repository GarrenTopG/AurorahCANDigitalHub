package com.aurorah.can.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.ui.components.FeedError
import com.aurorah.can.ui.theme.AurorahTheme
import com.aurorah.can.ui.theme.BrandPale
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.LineColor
import com.aurorah.can.ui.theme.PageBackground
import com.aurorah.can.ui.theme.TextInk
import com.aurorah.can.ui.theme.TextMuted

data class HomeFeedState(
    val stories: List<NewsItem> = emptyList(),
    val needs: List<CommunityNeed> = emptyList(),
    val isLoading: Boolean = true,
    val newsError: String? = null,
    val needsError: String? = null
)

@Composable
fun AurorahHomeScreen(
    feedState: HomeFeedState,
    onRefresh: () -> Unit,
    onRequestAssistance: () -> Unit,
    onExploreNeeds: () -> Unit,
    onNeedClick: (CommunityNeed) -> Unit,
    onNewsClick: (NewsItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val allStoriesFilter = stringResource(R.string.home_filter_all_stories)
    val outreachesFilter = stringResource(R.string.home_filter_outreaches)
    val impactFilter = stringResource(R.string.home_filter_impact)
    val announcementsFilter = stringResource(R.string.home_filter_announcements)

    var selectedCategoryFilter by remember { mutableStateOf(allStoriesFilter) }
    val categories = listOf(allStoriesFilter, outreachesFilter, impactFilter, announcementsFilter)

    AurorahTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(PageBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HeroCard(
                onRequestAssistance = onRequestAssistance,
                onExploreNeeds = onExploreNeeds
            )

            Surface(
                color = BrandPale,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_banner_network),
                    color = BrandPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Ways to help nearby section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeading(stringResource(R.string.home_ways_to_help_title))
                when {
                    feedState.needs.isNotEmpty() -> {
                        feedState.needs.take(3).forEach { need ->
                            NeedCard(need = need, onClick = { onNeedClick(need) })
                        }
                    }
                    feedState.isLoading -> {
                        Text(stringResource(R.string.home_loading_needs), color = TextMuted)
                    }
                    else -> {
                        Text(
                            stringResource(R.string.home_empty_needs),
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                        OutlinedButton(
                            onClick = onExploreNeeds,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary)
                        ) {
                            Text(stringResource(R.string.home_explore_ways))
                        }
                    }
                }
            }

            // Community News & Stories section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeading(stringResource(R.string.home_stories_title))
                Text(
                    text = stringResource(R.string.home_stories_subtitle),
                    color = TextMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = cat.equals(selectedCategoryFilter, ignoreCase = true)
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) BrandPrimary else Color.White,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) BrandPrimary else LineColor
                            ),
                            modifier = Modifier.clickable { selectedCategoryFilter = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else TextInk,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                val allStories = feedState.stories.ifEmpty {
                    listOf(
                        NewsItem(
                            id = 1,
                            title = "A place for young readers in Sunbird Park",
                            summary = "Aurorah CAN established a community mini-library in July 2022 as part of Nelson Mandela Day to promote literacy and provide a safe learning haven.",
                            body = "Aurorah CAN established a community mini-library in Sunbird Park in July 2022 as part of our Nelson Mandela Day initiative.\n\nThe mini-library provides local children and youth with open access to storybooks, educational resource materials, and a quiet, safe learning environment after school hours.\n\nThanks to generous book donations from local residents and community partners, the library collection now holds over 600 titles across multiple languages including English, Afrikaans, and isiXhosa.\n\nVolunteers host weekly reading circles and literacy sessions every Tuesday and Thursday afternoon, fostering a passion for lifelong learning in Kuils River and surrounding neighborhoods.",
                            category = "Impact Update",
                            timestamp = "July 2022",
                            author = "Aurorah CAN Team",
                            imageUrl = "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=800&q=80"
                        ),
                        NewsItem(
                            id = 2,
                            title = "Winter Relief Drive in Kuils River & Surrounds",
                            summary = "Providing blankets, warm clothing and warm soup packages to 150+ families across local sectors.",
                            body = "As winter weather fronts hit the Western Cape, Aurorah CAN launched its annual Winter Warmth Drive to assist vulnerable households across Sunbird Park, Happy Valley, and informal settlements nearby.\n\nThrough rapid community coordination, our teams distributed thermal blankets, winter jackets, rainproof boots, and emergency food parcels to more than 150 families.\n\nHot soup kitchens were also mobilized over three consecutive weekends, serving over 800 hot meals to elderly residents and young children.\n\nWe extend our deepest gratitude to all donors, driver volunteers, and neighborhood networks who made this rapid response possible.",
                            category = "Community Outreaches",
                            timestamp = "Recent Outreach",
                            author = "Aurorah CAN Team",
                            imageUrl = "https://images.unsplash.com/photo-1593113598332-cd288d649433?auto=format&fit=crop&w=800&q=80"
                        ),
                        NewsItem(
                            id = 3,
                            title = "Digital Hub Skills & Youth Training Workshop Launch",
                            summary = "Empowering local youth with computer literacy, digital CV preparation, and job application support.",
                            body = "Aurorah CAN is thrilled to announce the rollout of our new Community Digital Hub training sessions designed to empower local youth with digital literacy, CV creation, and online job application skills.\n\nThe initiative provides free access to internet-connected workstations, guided tutorials on mobile and desktop applications, and one-on-one mentorship for job seekers and aspiring entrepreneurs.\n\nWorkshops run weekly at the community hall. Registration is open to all community members via the Aurorah CAN Digital Hub app or in person.",
                            category = "Announcements",
                            timestamp = "Active Program",
                            author = "Aurorah CAN Team",
                            imageUrl = "https://images.unsplash.com/photo-1531482615713-2afd69097998?auto=format&fit=crop&w=800&q=80"
                        )
                    )
                }

                val filteredStories = if (selectedCategoryFilter.equals(allStoriesFilter, ignoreCase = true)) {
                    allStories
                } else {
                    allStories.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
                }

                when {
                    feedState.isLoading -> {
                        Text(stringResource(R.string.home_loading_stories), color = TextMuted)
                    }
                    feedState.newsError != null -> {
                        FeedError(
                            message = "Community updates could not be loaded.",
                            onRetry = onRefresh
                        )
                    }
                    filteredStories.isNotEmpty() -> {
                        val featured = filteredStories.first()
                        FeaturedStoryCard(story = featured, onClick = { onNewsClick(featured) })

                        filteredStories.drop(1).forEach { story ->
                            StoryCard(story = story, onClick = { onNewsClick(story) })
                        }
                    }
                    else -> {
                        Text(
                            text = "No active updates in this category.",
                            color = TextMuted,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LineColor)
                ) {
                    Text(
                        text = "Media Consent Notice: We share photos of identifiable community members, especially children, only with written media consent. Generic imagery is labelled as illustrative.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            ContactCard()

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SectionHeading(title: String) {
    Text(
        text = title,
        color = TextInk,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}
