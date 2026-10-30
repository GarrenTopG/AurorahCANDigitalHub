package com.aurorah.can.ui.getinvolved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aurorah.can.R
import com.aurorah.can.ui.donation.DonationViewModel
import com.aurorah.can.ui.getinvolved.components.CommunityNeedsSection
import com.aurorah.can.ui.getinvolved.components.DonateHeroBanner
import com.aurorah.can.ui.getinvolved.components.DonationForms
import com.aurorah.can.ui.theme.AurorahTheme

/**
 * Main sub-tab for handling donations and pledges.
 * Uses a LazyColumn layout to optimize performance when dealing with 
 * scrollable nested elements like lists of community needs.
 * Orchestrates the hero banner, community needs showcase, and the donation forms.
 */
@Composable
fun DonateSubTab(
    viewModel: DonationViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val generalFundOption = stringResource(R.string.fund_general)
    val fundOptions = listOf(generalFundOption) + (uiState.communityNeeds ?: emptyList()).map { it.title }

    AurorahTheme {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Banner
            item {
                DonateHeroBanner()
            }

            // Community Needs & Donation Forms Area
            item {
                CommunityNeedsSection(
                    needs = uiState.communityNeeds ?: emptyList(),
                    isLoading = uiState.isLoadingNeeds,
                    selectedCategory = uiState.selectedFundCategory,
                    onNeedSelected = { category ->
                        viewModel.onFundCategorySelected(category)
                    },
                    generalFundOption = generalFundOption,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                DonationForms(
                    uiState = uiState,
                    viewModel = viewModel,
                    fundOptions = fundOptions,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}
