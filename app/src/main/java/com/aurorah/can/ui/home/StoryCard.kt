package com.aurorah.can.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aurorah.can.R
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.util.NewsUtils

@Composable
fun StoryCard(
    story: NewsItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val dateStr = NewsUtils.formatNewsDate(story.timestamp)
            val categoryStr = story.category.ifBlank { "Announcement" }
            Text(
                text = "$categoryStr  ·  $dateStr".uppercase(),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = story.title,
                        color = AppTextInk,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                    val cleanSummary = (if (story.summary.isNotBlank()) story.summary else story.body)
                        .let { NewsUtils.parseHtmlContent(it).toString().trim() }
                    Text(
                        text = cleanSummary,
                        color = AppTextMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }

                val resolvedUrl = NewsUtils.resolveImageUrl(story.imageUrl)
                val imageSource: Any = if (!resolvedUrl.isNullOrBlank()) {
                    resolvedUrl
                } else {
                    R.drawable.ic_news_placeholder
                }

                AsyncImage(
                    model = imageSource,
                    contentDescription = story.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_news_placeholder),
                    error = painterResource(R.drawable.ic_news_placeholder)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${story.author.ifBlank { "Aurorah CAN Team" }}",
                    color = AppTextMuted,
                    fontSize = 12.sp
                )

                Text(
                    text = stringResource(R.string.read_update),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
