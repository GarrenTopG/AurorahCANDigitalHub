package com.aurorah.can.ui.getinvolved

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.aurorah.can.util.NewsUtils
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.ui.components.CountryCodeDropdown
import com.aurorah.can.ui.components.PriorityBadge
import com.aurorah.can.ui.donation.DonationViewModel
import com.aurorah.can.ui.theme.AppBrandPale
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppLineColor
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.ui.theme.BrandPrimary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DonateSubTab(
    viewModel: DonationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var selectedNeedForDetail by remember { mutableStateOf<CommunityNeed?>(null) }

    if (selectedNeedForDetail != null) {
        CommunityNeedDetailScreen(
            need = selectedNeedForDetail!!,
            onBack = { selectedNeedForDetail = null },
            onContinueToDonation = { need ->
                viewModel.onFundCategorySelected(need.title)
                selectedNeedForDetail = null
            }
        )
        return
    }

    val generalFundOption = stringResource(R.string.fund_general)
    val fundOptions = listOf(generalFundOption) + uiState.communityNeeds.map { it.title }
    val amountPresets = listOf(50.0, 200.0, 500.0, 1000.0)
    val inKindCategories = listOf(
        "Non-Perishable Food / Canned Goods",
        "Clothing & Footwear",
        "Blankets & Bedding",
        "School Supplies & Books",
        "Other Physical Goods"
    )

    var fundDropdownExpanded by remember { mutableStateOf(false) }
    var inKindDropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Hero Banner Header Section
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

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Community Needs Section (Local Priorities)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

                if (uiState.isLoadingNeeds) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                    }
                } else if (uiState.communityNeeds.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.communityNeeds) { need ->
                            val isSelected = uiState.selectedFundCategory.equals(need.title, ignoreCase = true)
                            DonateNeedCard(
                                need = need,
                                isSelected = isSelected,
                                onSelect = {
                                    if (isSelected) {
                                        viewModel.onFundCategorySelected(generalFundOption)
                                    } else {
                                        viewModel.onFundCategorySelected(need.title)
                                    }
                                },
                                onViewDetail = {
                                    selectedNeedForDetail = need
                                }
                            )
                        }
                    }
                }
            }

            // FORM SECTION: MAKE A PLEDGE
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            color = BrandPrimary,
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

                    // Error Alert Banner
                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFFFF1F2),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFDA4AF))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFE11D48),
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "We could not submit this pledge.",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF881337),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = uiState.errorMessage!!,
                                        color = Color(0xFF9F1239),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Success State Banner
                    if (uiState.isSuccess && uiState.donationResponse != null) {
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFF10B981), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Text(
                                    text = "Your pledge has been recorded",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )

                                val targetNeedTitle = uiState.selectedCommunityNeed?.title
                                val confirmationText = if (!targetNeedTitle.isNullOrBlank()) {
                                    "Your pledge for $targetNeedTitle is pending admin review. No payment was processed, and it will be added to the public total only if approved."
                                } else {
                                    "No payment was taken. This confirmation records your pledge or offer only; it is not a payment confirmation or a tax receipt."
                                }

                                Text(
                                    text = confirmationText,
                                    fontSize = 13.sp,
                                    color = Color(0xFF047857),
                                    lineHeight = 19.sp
                                )

                                val txRef = uiState.donationResponse?.transactionRef ?: ""
                                if (txRef.isNotBlank()) {
                                    Text(
                                        text = stringResource(R.string.pledge_reference_format, txRef),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46),
                                        fontSize = 14.sp
                                    )
                                }

                                if (uiState.requestTaxCertificate) {
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Your sample Section 18A tax certificate is ready for download.",
                                                fontSize = 12.sp,
                                                color = Color(0xFF065F46),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            OutlinedButton(
                                                onClick = {
                                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aurorah-web-hub.onrender.com/donate"))
                                                    context.startActivity(browserIntent)
                                                },
                                                border = BorderStroke(1.dp, Color(0xFF059669)),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF059669)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Download,
                                                    contentDescription = null,
                                                    modifier = Modifier.padding(end = 6.dp).size(16.dp)
                                                )
                                                Text("Download tax certificate (PDF)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = { viewModel.resetForm() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF065F46)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Make another pledge", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Informational Note Banner
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "This page records a donation pledge. Payments are not processed online, and any tax certificate is generated from the details you provide for your records.",
                                fontSize = 12.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 17.sp
                            )
                        }
                    }

                    // Mode Toggle Tabs
                    TabRow(
                        selectedTabIndex = if (uiState.isMonetaryTab) 0 else 1,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[if (uiState.isMonetaryTab) 0 else 1]),
                                color = BrandPrimary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = uiState.isMonetaryTab,
                            onClick = { viewModel.onTabSelected(true) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Monetary Donation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            },
                            selectedContentColor = BrandPrimary,
                            unselectedContentColor = AppTextMuted
                        )
                        Tab(
                            selected = !uiState.isMonetaryTab,
                            onClick = { viewModel.onTabSelected(false) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.Inventory, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("In-Kind Goods", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            },
                            selectedContentColor = BrandPrimary,
                            unselectedContentColor = AppTextMuted
                        )
                    }

                    // SECTION: MONETARY FIELDS
                    if (uiState.isMonetaryTab) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Monetary pledge amount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTextInk)

                            // Preset Amount Grid
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                amountPresets.forEach { amt ->
                                    val isSelected = !uiState.isCustomAmountSelected && uiState.selectedAmount == amt
                                    OutlinedCard(
                                        onClick = { viewModel.onAmountSelected(amt, isCustom = false) },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) BrandPrimary else AppLineColor
                                        ),
                                        colors = CardDefaults.outlinedCardColors(
                                            containerColor = if (isSelected) AppBrandPale else AppCardSurface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("R${amt.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppTextInk)
                                            Text("Pledge", fontSize = 11.sp, color = AppTextMuted)
                                        }
                                    }
                                }
                            }

                            // Custom Amount Field
                            OutlinedTextField(
                                value = uiState.customAmountText,
                                onValueChange = { viewModel.onCustomAmountChanged(it) },
                                label = { Text("Custom Amount (ZAR) *") },
                                placeholder = { Text("0.00") },
                                prefix = { Text("R ", fontWeight = FontWeight.Bold) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = uiState.customAmountError != null,
                                supportingText = {
                                    if (uiState.customAmountError != null) {
                                        Text(uiState.customAmountError!!, color = Color.Red, fontSize = 12.sp)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                            )

                            // Request Section 18A Tax Certificate Checkbox & Fields
                            Surface(
                                color = AppCardSurface,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, AppLineColor)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            viewModel.onRequestTaxCertificateToggled(!uiState.requestTaxCertificate)
                                        }
                                    ) {
                                        Checkbox(
                                            checked = uiState.requestTaxCertificate,
                                            onCheckedChange = { viewModel.onRequestTaxCertificateToggled(it) },
                                            colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                                        )
                                        Text(
                                            text = "Request a Section 18A tax certificate (monetary pledges only)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppTextInk,
                                            modifier = Modifier.padding(start = 6.dp)
                                        )
                                    }

                                    AnimatedVisibility(visible = uiState.requestTaxCertificate) {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            OutlinedTextField(
                                                value = uiState.taxIdNumber,
                                                onValueChange = { viewModel.onTaxIdNumberChanged(it) },
                                                label = { Text("Tax reference / ID *") },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                                            )
                                            OutlinedTextField(
                                                value = uiState.taxAddress,
                                                onValueChange = { viewModel.onTaxAddressChanged(it) },
                                                label = { Text("Tax address *") },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                                            )
                                            if (!uiState.taxError.isNullOrBlank()) {
                                                Text(uiState.taxError!!, color = Color.Red, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            // Choose what your pledge supports dropdown
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Choose what your pledge supports", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedCard(
                                        onClick = { fundDropdownExpanded = !fundDropdownExpanded },
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, AppLineColor)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = uiState.selectedFundCategory,
                                                fontSize = 14.sp,
                                                color = AppTextInk,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = fundDropdownExpanded,
                                        onDismissRequest = { fundDropdownExpanded = false },
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        fundOptions.forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option, fontSize = 14.sp) },
                                                onClick = {
                                                    viewModel.onFundCategorySelected(option)
                                                    fundDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "A pledge for a community need stays linked to that need. It is included in the public total only after an administrator approves it. General fund pledges are separate.",
                                    fontSize = 12.sp,
                                    color = AppTextMuted,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    } else {
                        // SECTION: IN-KIND FIELDS
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("In-Kind Contribution Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTextInk)

                            // Item Category Selector
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Item Category *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedCard(
                                        onClick = { inKindDropdownExpanded = !inKindDropdownExpanded },
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, AppLineColor)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = uiState.inKindCategory,
                                                fontSize = 14.sp,
                                                color = AppTextInk,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = inKindDropdownExpanded,
                                        onDismissRequest = { inKindDropdownExpanded = false },
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        inKindCategories.forEach { category ->
                                            DropdownMenuItem(
                                                text = { Text(category, fontSize = 14.sp) },
                                                onClick = {
                                                    viewModel.onInKindCategorySelected(category)
                                                    inKindDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Item Description & Quantities
                            OutlinedTextField(
                                value = uiState.itemDescription,
                                onValueChange = { viewModel.onItemDescriptionChanged(it) },
                                label = { Text("Item Description & Estimated Quantities *") },
                                placeholder = { Text("e.g. 3 boxes of canned beans, 10 warm winter jackets") },
                                isError = uiState.itemDescError != null,
                                supportingText = uiState.itemDescError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                            )

                            // Fulfillment Method Radio Options
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Fulfillment Method", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedCard(
                                        onClick = { viewModel.onPickupToggled(false) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(
                                            width = if (!uiState.isPickup) 2.dp else 1.dp,
                                            color = if (!uiState.isPickup) BrandPrimary else AppLineColor
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            RadioButton(
                                                selected = !uiState.isPickup,
                                                onClick = { viewModel.onPickupToggled(false) },
                                                colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                                            )
                                            Text("I will drop off", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppTextInk)
                                        }
                                    }

                                    OutlinedCard(
                                        onClick = { viewModel.onPickupToggled(true) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(
                                            width = if (uiState.isPickup) 2.dp else 1.dp,
                                            color = if (uiState.isPickup) BrandPrimary else AppLineColor
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            RadioButton(
                                                selected = uiState.isPickup,
                                                onClick = { viewModel.onPickupToggled(true) },
                                                colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                                            )
                                            Text("Request collection", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppTextInk)
                                        }
                                    }
                                }
                            }

                            // Drop-off Info vs Pickup Address Input
                            if (!uiState.isPickup) {
                                Surface(
                                    color = AppBrandPale,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, AppLineColor)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Drop-off Location:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandPrimary)
                                        Text("Aurorah NPC, 27 Lovebird Walk, Sunbird Park, Cape Town, 7580.", fontSize = 13.sp, color = AppTextInk)
                                        Text("Hours: Monday–Friday, 08:00–17:00; Saturday, 09:00–14:00. Closed Sunday.", fontSize = 12.sp, color = AppTextMuted)
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = uiState.pickupAddress,
                                    onValueChange = { viewModel.onPickupAddressChanged(it) },
                                    label = { Text("Collection Address *") },
                                    placeholder = { Text("Enter full address for driver pickup") },
                                    isError = uiState.pickupAddressError != null,
                                    supportingText = uiState.pickupAddressError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                                )
                            }
                        }
                    }

                    // SECTION: CONTACT INFO
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Your Information", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTextInk)

                        // Anonymous Donation Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.onAnonymousToggled(!uiState.isAnonymous)
                            }
                        ) {
                            Checkbox(
                                checked = uiState.isAnonymous,
                                onCheckedChange = { viewModel.onAnonymousToggled(it) },
                                colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                            )
                            Text(
                                text = "I prefer to donate anonymously",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppTextInk,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }

                        // Your Name Field
                        OutlinedTextField(
                            value = uiState.donorName,
                            onValueChange = { viewModel.onDonorNameChanged(it) },
                            label = { Text("Your Name") },
                            placeholder = { Text("e.g. Jane Doe") },
                            enabled = !uiState.isAnonymous,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                        )

                        // Email Address Field
                        OutlinedTextField(
                            value = uiState.donorEmail,
                            onValueChange = { viewModel.onDonorEmailChanged(it) },
                            label = { Text("Email Address (Optional)") },
                            placeholder = { Text("e.g. email@example.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                        )

                        // Phone Number Field with Country Code
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Phone Number (Optional)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CountryCodeDropdown(
                                    selectedCode = uiState.countryCode,
                                    onCodeSelected = { viewModel.onCountryCodeChanged(it) },
                                    modifier = Modifier.width(110.dp)
                                )
                                OutlinedTextField(
                                    value = uiState.phone,
                                    onValueChange = { viewModel.onPhoneChanged(it) },
                                    placeholder = { Text("82 123 4567") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                                )
                            }
                        }

                        // Message Field
                        OutlinedTextField(
                            value = uiState.message,
                            onValueChange = { viewModel.onMessageChanged(it) },
                            label = { Text("Message (Optional)") },
                            placeholder = { Text("Leave a message to inspire others...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                        )
                    }

                    // SUBMIT & POPIA CONSENT
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "We use the details you provide to record and review this pledge or goods offer. Name and contact details are optional; if you provide them, authorised staff may use them to follow up.",
                            fontSize = 12.sp,
                            color = AppTextMuted,
                            lineHeight = 17.sp
                        )

                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.clickable {
                                viewModel.onPopiaConsentChanged(!uiState.popiaConsent)
                            }
                        ) {
                            Checkbox(
                                checked = uiState.popiaConsent,
                                onCheckedChange = { viewModel.onPopiaConsentChanged(it) },
                                colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                            )
                            Text(
                                text = "I have read the privacy information and agree that Aurorah NPC may use the details I provide to record and review this pledge or offer.",
                                fontSize = 13.sp,
                                color = AppTextInk,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.submitDonation() },
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.padding(end = 8.dp).size(18.dp))
                                Text(
                                    text = if (uiState.isMonetaryTab) "Submit Donation Pledge" else "Submit In-Kind Offer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DonateNeedCard(
    need: CommunityNeed,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resolvedImageUrl = NewsUtils.resolveImageUrl(need.imageUrl) ?: need.imageUrl

    Card(
        modifier = modifier
            .width(280.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppBrandPale else AppCardSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) BrandPrimary else AppLineColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Column {
            // Card Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable(onClick = onViewDetail)
            ) {
                if (!resolvedImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = resolvedImageUrl,
                        contentDescription = need.imageAlt ?: need.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppBrandPale)
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Community Need",
                                color = BrandPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

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
                        color = BrandPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = need.category.ifBlank { "General" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
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
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onViewDetail)
                )

                val excerpt = need.articleExcerpt.takeIf { !it.isNullOrBlank() } ?: need.description
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

                Row(
                    modifier = Modifier
                        .clickable(onClick = onViewDetail)
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Read story & article",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                }

                Spacer(Modifier.height(2.dp))

                if (isSelected) {
                    Button(
                        onClick = onSelect,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
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
                        border = BorderStroke(1.dp, BrandPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary),
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
}
