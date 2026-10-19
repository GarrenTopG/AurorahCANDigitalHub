package com.aurorah.can.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.data.model.NewsItem

// Aurorah CAN Brand Colors
val BrandPrimary = Color(0xFF752D42)
val BrandMid = Color(0xFF943D56)
val PageBackground = Color(0xFFFAF8F9)
val TextInk = Color(0xFF28252A)
val TextMuted = Color(0xFF625D62)
val BrandPale = Color(0xFFFDF2F8)
val LineColor = Color(0xFFE5DDE1)

@Composable
fun AurorahTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BrandPrimary,
            onPrimary = Color.White,
            secondary = BrandMid,
            background = PageBackground,
            surface = Color.White,
            onSurface = TextInk
        ),
        content = content
    )
}

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
                    text = "A community network, not a government service.",
                    color = BrandPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Ways to help nearby section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeading("Ways to help nearby")
                when {
                    feedState.needs.isNotEmpty() -> {
                        feedState.needs.take(3).forEach { need ->
                            NeedCard(need = need, onClick = { onNeedClick(need) })
                        }
                    }
                    feedState.isLoading -> {
                        Text("Loading current community needs…", color = TextMuted)
                    }
                    feedState.needsError != null -> {
                        FeedError(
                            message = "Community needs could not be loaded.",
                            onRetry = onRefresh
                        )
                    }
                    else -> {
                        Text(
                            "See current ways to help, donate, or volunteer with Aurorah CAN.",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                        OutlinedButton(
                            onClick = onExploreNeeds,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary)
                        ) {
                            Text("Explore ways to help")
                        }
                    }
                }
            }

            // Community News & Stories section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeading("Community News & Stories")
                val stories = feedState.stories
                when {
                    stories.isNotEmpty() -> {
                        stories.forEach { story ->
                            StoryCard(story = story, onClick = { onNewsClick(story) })
                        }
                    }
                    feedState.isLoading -> {
                        Text("Loading community updates…", color = TextMuted)
                    }
                    feedState.newsError != null -> {
                        FeedError(
                            message = "Community updates could not be loaded.",
                            onRetry = onRefresh
                        )
                    }
                    else -> {
                        val fallbackStory = NewsItem(
                            id = 0,
                            title = "A place for young readers in Sunbird Park",
                            summary = "Aurorah CAN established a community mini-library in July 2022 as part of Nelson Mandela Day.",
                            body = "Aurorah CAN established a community mini-library in July 2022 as part of Nelson Mandela Day to promote literacy and provide a safe learning haven for children in Sunbird Park.",
                            category = "Community milestone",
                            timestamp = "July 2022",
                            author = "Aurorah CAN Team"
                        )
                        StoryCard(story = fallbackStory, onClick = { onNewsClick(fallbackStory) })
                    }
                }
            }

            ContactCard()

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HeroCard(
    onRequestAssistance: () -> Unit,
    onExploreNeeds: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(BrandPrimary, BrandMid)),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "AURORAH NPC  ·  KUILS RIVER, CAPE TOWN",
            color = Color(0xFFFCE7F3),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Practical community support, rooted in Kuils River.",
            color = Color.White,
            fontSize = 27.sp,
            lineHeight = 33.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Supporting vulnerable households and young people in Kuils River, Sunbird Park, and nearby Cape Flats communities.",
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Button(
            onClick = onRequestAssistance,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = BrandPrimary
            )
        ) {
            Text("Get Help", fontWeight = FontWeight.Bold)
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp).size(18.dp)
            )
        }

        OutlinedButton(
            onClick = onExploreNeeds,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("Get Involved", fontWeight = FontWeight.Bold)
            Icon(
                Icons.Filled.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp).size(18.dp)
            )
        }
    }
}

@Composable
private fun SectionHeading(title: String) {
    Text(
        text = title,
        color = TextInk,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun NeedCard(
    need: CommunityNeed,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = need.title,
                color = BrandPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = need.description.ifBlank { "See how you can help with this community need." },
                color = TextMuted,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            val metadata = listOf(need.location, need.priority)
                .filter { it.isNotBlank() }
                .joinToString("  ·  ")
            if (metadata.isNotBlank()) {
                Text(
                    text = metadata,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Text(
                text = "View ways to help  →",
                color = BrandPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StoryCard(
    story: NewsItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val dateStr = story.timestamp.take(10)
            val categoryStr = story.category.ifBlank { "Announcement" }
            Text(
                text = "$categoryStr  ·  $dateStr".uppercase(),
                color = BrandMid,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp
            )
            Text(
                text = story.title,
                color = TextInk,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = story.summary.ifBlank { story.body.take(120) },
                color = TextMuted,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Text(
                text = "Read community updates  →",
                color = BrandPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ContactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandPrimary)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Find us",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            ContactLine(
                icon = Icons.Filled.LocationOn,
                text = "27 Lovebird Walk, Sunbird Park, Cape Town, 7580"
            )
            ContactLine(
                icon = Icons.Filled.AccessTime,
                text = "Mon–Fri 08:00–17:00; Sat 09:00–14:00"
            )
        }
    }
}

@Composable
private fun ContactLine(
    icon: ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFCE7F3),
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun FeedError(
    message: String,
    onRetry: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = message,
            color = TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null
            )
            Text("Retry")
        }
    }
}
