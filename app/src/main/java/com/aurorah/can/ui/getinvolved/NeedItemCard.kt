package com.aurorah.can.ui.getinvolved

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aurorah.can.R
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.ui.components.PriorityBadge
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppLineColor
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.ui.theme.BrandMid
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.util.NewsUtils
import java.util.Locale

@Composable
fun NeedItemCard(
    need: CommunityNeed,
    onViewDetail: () -> Unit,
    onOfferHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetail),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        border = BorderStroke(1.dp, AppLineColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Square Image Blend Header Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                val resolvedImageUrl = NewsUtils.resolveImageUrl(need.imageUrl) ?: need.imageUrl
                if (!resolvedImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = resolvedImageUrl,
                        contentDescription = need.imageAlt ?: need.title,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_news_placeholder),
                        error = painterResource(R.drawable.ic_news_placeholder),
                        modifier = Modifier.fillMaxWidth().height(220.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(BrandPrimary, BrandMid)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "COMMUNITY NEED",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Square Vertical Gradient Blend overlay transitioning smoothly to AppCardSurface
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AppCardSurface.copy(alpha = 0.65f),
                                    AppCardSurface
                                )
                            )
                        )
                )

                // Floating Badges Row (Category & Priority)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = need.category.ifBlank { "General" }.uppercase(Locale.getDefault()),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }

                    PriorityBadge(priority = need.priority)
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (need.location.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = AppTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = need.location,
                            fontSize = 12.sp,
                            color = AppTextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Title
                Text(
                    text = need.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppTextInk,
                    lineHeight = 22.sp
                )

                // Excerpt
                val rawExcerpt = when {
                    !need.articleExcerpt.isNullOrBlank() -> need.articleExcerpt
                    need.description.isNotBlank() -> need.description
                    else -> "See how you can support this priority in our community."
                }
                val excerpt = NewsUtils.parseHtmlContent(rawExcerpt).toString()

                Text(
                    text = excerpt,
                    fontSize = 13.sp,
                    color = AppTextMuted,
                    lineHeight = 19.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

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

                        if (need.currentAmount >= need.targetAmount) {
                            Text(
                                text = "Target reached. Pledges remain open.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }

                // Deadline
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

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewDetail,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "View & support",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp).size(14.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onOfferHelp,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Handshake,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 4.dp).size(14.dp)
                        )
                        Text(
                            text = stringResource(R.string.offer_assistance),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
