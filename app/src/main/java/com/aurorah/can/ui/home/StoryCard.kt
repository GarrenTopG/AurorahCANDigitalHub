package com.aurorah.can.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.ui.theme.BrandMid
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.TextInk
import com.aurorah.can.ui.theme.TextMuted

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${story.author.ifBlank { "Aurorah CAN Team" }}",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Text(
                    text = stringResource(R.string.read_update),
                    color = BrandPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
