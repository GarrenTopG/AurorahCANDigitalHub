package com.aurorah.can.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.aurorah.can.ui.theme.AppBrandPale
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.util.NewsUtils

@Composable
fun FeaturedStoryCard(
    story: NewsItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
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
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_news_placeholder),
                error = painterResource(R.drawable.ic_news_placeholder)
            )

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = AppBrandPale,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "FEATURED STORY  ·  ${story.category.uppercase()}",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = NewsUtils.formatNewsDate(story.timestamp),
                        color = AppTextMuted,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = story.title,
                    color = AppTextInk,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 26.sp
                )

                val cleanSummary = (if (story.summary.isNotBlank()) story.summary else story.content.ifBlank { story.body })
                    .let { NewsUtils.parseHtmlContent(it).toString().trim() }

                Text(
                    text = cleanSummary,
                    color = AppTextMuted,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

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
                        text = stringResource(R.string.read_full_story),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
