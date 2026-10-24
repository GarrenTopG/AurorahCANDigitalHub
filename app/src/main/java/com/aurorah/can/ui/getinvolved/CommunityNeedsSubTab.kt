package com.aurorah.can.ui.getinvolved

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.aurorah.can.ui.needs.NeedsViewModel
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.ui.theme.BrandPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunityNeedsSubTab(
    viewModel: NeedsViewModel,
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.community_needs_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppTextInk
        )
        Text(
            text = stringResource(R.string.community_needs_subtitle),
            fontSize = 14.sp,
            color = AppTextMuted,
            lineHeight = 20.sp
        )

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
                    NeedItemCard(need = need, onOfferHelp = { viewModel.selectNeedForOffer(need) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
