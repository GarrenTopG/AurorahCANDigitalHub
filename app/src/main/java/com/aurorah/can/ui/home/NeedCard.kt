package com.aurorah.can.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.TextMuted

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
