package com.aurorah.can.ui.getinvolved.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.donation.DonationUiState
import com.aurorah.can.ui.donation.DonationViewModel
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppLineColor
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted

/**
 * Encapsulates the entire donation pledge workflow UI, composing mode switching tabs,
 * monetary and in-kind pledge sub-forms, donor contact information, and submission elements.
 *
 * This modular orchestrator delegates specific sub-sections to dedicated component files:
 * - [DonationSuccessBanner] for confirmation state
 * - [DonationTypeTabs] for form mode toggling
 * - [MonetaryDonationForm] for monetary pledge options
 * - [InKindDonationForm] for physical goods contributions
 * - [DonationContactInfoForm] for donor details
 * - [DonationSubmitSection] for consent and submission triggers
 *
 * @param uiState Reactive State Flow wrapper containing current donation form selections and validation.
 * @param viewModel ViewModel handling donation actions and API dispatches.
 * @param fundOptions List of available target fund options for monetary donations.
 * @param modifier Optional layout [Modifier].
 */
@Composable
fun DonationForms(
    uiState: DonationUiState,
    viewModel: DonationViewModel,
    fundOptions: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val amountPresets = listOf(50.0, 200.0, 500.0, 1000.0)
    val inKindCategories = listOf(
        "Non-Perishable Food / Canned Goods",
        "Clothing & Footwear",
        "Blankets & Bedding",
        "School Supplies & Books",
        "Other Physical Goods"
    )

    var fundDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var inKindDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        border = BorderStroke(1.dp, AppLineColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "MAKE A PLEDGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Donation pledge or in-kind offer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppTextInk
                )
                Text(
                    text = "Choose a specific community need or the general fund. Pledges are reviewed by our team; no online payment is processed.",
                    fontSize = 13.sp,
                    color = AppTextMuted,
                    lineHeight = 18.sp
                )
            }

            // Success State Banner
            if (uiState.isSuccess && uiState.donationResponse != null) {
                DonationSuccessBanner(
                    uiState = uiState,
                    onReset = { viewModel.resetForm() },
                    onDownloadCertificate = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aurorah-web-hub.onrender.com/donate"))
                        context.startActivity(browserIntent)
                    }
                )
            }

            // Informational Note Banner
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "This page records a donation pledge. Payments are not processed online, and any tax certificate is generated from the details you provide for your records.",
                        fontSize = 12.sp,
                        color = AppTextInk,
                        lineHeight = 17.sp
                    )
                }
            }

            // Mode Toggle Tabs
            DonationTypeTabs(
                isMonetaryTab = uiState.isMonetaryTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )

            // Dynamic Form Section
            if (uiState.isMonetaryTab) {
                MonetaryDonationForm(
                    uiState = uiState,
                    viewModel = viewModel,
                    amountPresets = amountPresets,
                    fundOptions = fundOptions,
                    fundDropdownExpanded = fundDropdownExpanded,
                    onFundDropdownExpandedChange = { fundDropdownExpanded = it }
                )
            } else {
                InKindDonationForm(
                    uiState = uiState,
                    viewModel = viewModel,
                    inKindCategories = inKindCategories,
                    inKindDropdownExpanded = inKindDropdownExpanded,
                    onInKindDropdownExpandedChange = { inKindDropdownExpanded = it }
                )
            }

            // Contact Info Form
            DonationContactInfoForm(uiState, viewModel)

            // Submit Section
            DonationSubmitSection(uiState, viewModel)
        }
    }
}
