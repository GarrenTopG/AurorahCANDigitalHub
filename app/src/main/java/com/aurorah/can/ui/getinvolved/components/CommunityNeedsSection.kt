package com.aurorah.can.ui.getinvolved.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.ui.components.PriorityBadge
import com.aurorah.can.ui.theme.AppBrandPale
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppLineColor
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.util.NewsUtils
import java.util.Locale

/**
 * A section displaying local community priorities and needs.
 * It renders a horizontal scrolling list of specific needs, allowing the user
 * to select a need to allocate their donation to.
 */
@Composable
fun CommunityNeedsSection(
    needs: List<CommunityNeed>,
    isLoading: Boolean,
    selectedCategory: String,
    onNeedSelected: (String) -> Unit,
    generalFundOption: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "LOCAL PRIORITIES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Community needs",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AppTextInk
        )
        Text(
            text = stringResource(R.string.community_needs_donate_subtitle),
            fontSize = 13.sp,
            color = AppTextMuted,
            lineHeight = 18.sp
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary, 
                    strokeWidth = 2.dp
                )
            }
        } else if (needs.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = needs,
                    key = { it.title } // Good practice for performance
                ) { need ->
                    val isSelected = selectedCategory.equals(need.title, ignoreCase = true)
                    DonateNeedCard(
                        need = need,
                        isSelected = isSelected,
                        onSelect = {
                            if (isSelected) {
                                onNeedSelected(generalFundOption)
                            } else {
                                onNeedSelected(need.title)
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * A card representing a single community need.
 * Displays the need's title, category, priority, progress against its target amount,
 * and allows the user to select this need for their donation.
 */
@Composable
fun DonateNeedCard(
    need: CommunityNeed,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppBrandPale else AppCardSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else AppLineColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = need.category.ifBlank { "General" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

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
                            color = AppTextMuted
                        )
                    }
                }

                PriorityBadge(priority = need.priority)
            }

            Text(
                text = need.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            val rawExcerpt = need.articleExcerpt.takeIf { !it.isNullOrBlank() } ?: need.description
            val excerpt = NewsUtils.parseHtmlContent(rawExcerpt).toString()
            if (excerpt.isNotBlank()) {
                Text(
                    text = excerpt,
                    fontSize = 13.sp,
                    color = AppTextMuted,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
                            fontSize = 13.sp,
                            color = AppTextInk
                        )
                        Text(
                            text = "of R ${String.format(Locale.US, "%.2f", need.targetAmount)} target",
                            fontSize = 11.sp,
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
                        fontSize = 11.sp,
                        color = AppTextMuted
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            if (isSelected) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp).size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.selected_for_donation),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onSelect,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.btn_donate_this_need),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
