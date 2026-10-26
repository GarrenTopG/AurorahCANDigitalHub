package com.aurorah.can.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppLineColor
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import java.util.Locale

@Composable
fun NeedCard(
    need: CommunityNeed,
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = need.title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = need.description.ifBlank { "See how you can help with this community need." },
                color = AppTextMuted,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            val metadata = listOf(need.location, need.priority)
                .filter { it.isNotBlank() }
                .joinToString("  ·  ")
            if (metadata.isNotBlank()) {
                Text(
                    text = metadata,
                    color = AppTextMuted,
                    fontSize = 12.sp
                )
            }

            // Target Amount & Progress Bar
            if (need.targetAmount > 0.0) {
                val percent = ((need.currentAmount / need.targetAmount) * 100)
                    .coerceIn(0.0, 100.0)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R ${String.format(Locale.US, "%.2f", need.currentAmount)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = AppTextInk
                        )
                        Text(
                            text = "of R ${String.format(Locale.US, "%.2f", need.targetAmount)} target",
                            fontSize = 12.sp,
                            color = AppTextMuted
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (percent / 100f).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = AppLineColor
                    )
                }
            }

            // Need Deadline
            if (!need.deadline.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = AppTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Need deadline: ${need.deadline}",
                        fontSize = 12.sp,
                        color = AppTextMuted
                    )
                }
            }

            Text(
                text = "View ways to help  →",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
