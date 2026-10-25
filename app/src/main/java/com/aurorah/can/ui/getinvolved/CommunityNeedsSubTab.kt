package com.aurorah.can.ui.getinvolved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.ui.needs.NeedsViewModel
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.ui.theme.BrandPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunityNeedsSubTab(
    viewModel: NeedsViewModel,
    onNavigateToDonationPledge: ((CommunityNeed) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val categories = listOf(
        stringResource(R.string.filter_all),
        stringResource(R.string.filter_food),
        stringResource(R.string.filter_clothing),
        stringResource(R.string.filter_education),
        stringResource(R.string.filter_healthcare)
    )

    if (uiState.selectedNeedForDetail != null) {
        CommunityNeedDetailScreen(
            need = uiState.selectedNeedForDetail!!,
            onBack = { viewModel.selectNeedForDetail(null) },
            onContinueToDonation = { need ->
                viewModel.selectNeedForDetail(null)
                if (onNavigateToDonationPledge != null) {
                    onNavigateToDonationPledge(need)
                }
            }
        )
        return
    }

    if (uiState.selectedNeedForOffer != null) {
        OfferAssistanceDialog(
            need = uiState.selectedNeedForOffer!!,
            isSubmitting = uiState.isSubmittingOffer,
            errorMessage = uiState.offerErrorMessage,
            onDismiss = { viewModel.selectNeedForOffer(null) },
            onSubmit = { skill, notes ->
                viewModel.submitOffer(uiState.selectedNeedForOffer!!.needId, skill, notes)
            }
        )
    }

    if (uiState.offerSuccessMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearFeedbackMessage() },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearFeedbackMessage() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text(stringResource(R.string.btn_ok))
                }
            },
            title = { Text(stringResource(R.string.offer_logged_title), fontWeight = FontWeight.Bold) },
            text = { Text(uiState.offerSuccessMessage!!) },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Header Banner
        Surface(
            color = BrandPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.community_needs_title),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "See local priorities shared by our team and learn how you can help.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
            }
        }

        // Section Content
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "LOCAL PRIORITIES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
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
            }

            // Category Filter Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category.equals(uiState.selectedFilter, ignoreCase = true) ||
                            (uiState.selectedFilter == "All" && category == stringResource(R.string.filter_all))
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val filterToUse = if (category == categories[0]) "All" else category
                            viewModel.onFilterSelected(filterToUse)
                        },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                uiState.filteredNeeds.isEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppCardSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.no_needs_found_format, uiState.selectedFilter),
                                color = AppTextMuted,
                                fontSize = 14.sp
                            )
                            OutlinedButton(onClick = { viewModel.loadNeeds() }) {
                                Text(stringResource(R.string.btn_refresh_list))
                            }
                        }
                    }
                }
                else -> {
                    uiState.filteredNeeds.forEach { need ->
                        NeedItemCard(
                            need = need,
                            onViewDetail = { viewModel.selectNeedForDetail(need) },
                            onOfferHelp = { viewModel.selectNeedForOffer(need) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
