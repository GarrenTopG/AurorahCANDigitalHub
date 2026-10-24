package com.aurorah.can.ui.getinvolved

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
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

    val monetaryCategories = listOf("Food Parcels", "Winter Relief", "General Fund")
    val amountPresets = listOf(50.0, 100.0, 250.0)
    val inKindCategories = listOf("Food Parcels", "Clothing", "Household Goods", "School Supplies")

    var currentStep by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.donation_pledge_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppTextInk
        )

        Surface(
            color = AppBrandPale,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(stringResource(R.string.donation_pledge_notice_title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
                Text(
                    text = stringResource(R.string.donation_pledge_notice_body),
                    fontSize = 13.sp,
                    color = AppTextInk,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardSurface),
            border = BorderStroke(1.dp, AppLineColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.section_18a_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AppTextInk
                )
                Text(
                    text = stringResource(R.string.section_18a_body),
                    fontSize = 13.sp,
                    color = AppTextMuted,
                    lineHeight = 18.sp
                )
                OutlinedButton(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aurorah-web-hub.onrender.com/donate"))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Launch,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp).size(16.dp)
                    )
                    Text(stringResource(R.string.section_18a_button), fontSize = 13.sp)
                }
            }
        }

        if (uiState.isSuccess && uiState.donationResponse != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(56.dp))
                    Text(
                        text = if (uiState.isMonetaryTab) stringResource(R.string.pledge_received_monetary) else stringResource(R.string.pledge_received_inkind),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextInk
                    )
                    val txRef = uiState.donationResponse?.transactionRef ?: ""
                    if (txRef.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.pledge_reference_format, txRef),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = stringResource(R.string.pledge_received_thank_you),
                        fontSize = 14.sp,
                        color = AppTextMuted,
                        lineHeight = 20.sp
                    )
                    Button(
                        onClick = {
                            currentStep = 1
                            viewModel.resetForm()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.make_another_donation))
                    }
                }
            }
        } else if (currentStep == 2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(stringResource(R.string.review_donation_pledge), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTextInk)

                    if (uiState.isMonetaryTab) {
                        ReviewItem(stringResource(R.string.review_donation_type), stringResource(R.string.review_monetary))
                        ReviewItem(stringResource(R.string.review_fund_category), uiState.selectedCategory)
                        ReviewItem(stringResource(R.string.review_pledge_amount), "R ${String.format(Locale.US, "%.2f", uiState.selectedAmount)}")
                    } else {
                        ReviewItem(stringResource(R.string.review_donation_type), stringResource(R.string.review_inkind_goods))
                        ReviewItem(stringResource(R.string.review_item_category), uiState.inKindCategory)
                        ReviewItem(stringResource(R.string.review_description), uiState.itemDescription)
                        ReviewItem(stringResource(R.string.review_fulfillment), if (uiState.isPickup) "Request Collection" else "Drop-off at Hub")
                    }

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(10.dp)) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
                                Text(uiState.errorMessage!!, color = Color(0xFF991B1B), fontSize = 13.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(onClick = { currentStep = 1 }, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.btn_back))
                        }
                        Button(
                            onClick = { viewModel.submitDonation() },
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(stringResource(R.string.btn_submit_pledge))
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.isMonetaryTab,
                            onClick = { viewModel.onTabSelected(true) },
                            label = { Text(stringResource(R.string.chip_monetary_pledge)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !uiState.isMonetaryTab,
                            onClick = { viewModel.onTabSelected(false) },
                            label = { Text(stringResource(R.string.chip_inkind_goods)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (uiState.isMonetaryTab) {
                        Text(stringResource(R.string.monetary_category_label), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            monetaryCategories.forEach { cat ->
                                FilterChip(
                                    selected = uiState.selectedCategory == cat,
                                    onClick = { viewModel.onMonetaryCategorySelected(cat) },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                                )
                            }
                        }

                        Text(stringResource(R.string.select_amount_label), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            amountPresets.forEach { amt ->
                                FilterChip(
                                    selected = !uiState.isCustomAmountSelected && uiState.selectedAmount == amt,
                                    onClick = { viewModel.onAmountSelected(amt, isCustom = false) },
                                    label = { Text("R ${amt.toInt()}") },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                                )
                            }
                            FilterChip(
                                selected = uiState.isCustomAmountSelected,
                                onClick = { viewModel.onAmountSelected(0.0, isCustom = true) },
                                label = { Text(stringResource(R.string.chip_custom_amount)) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                            )
                        }

                        if (uiState.isCustomAmountSelected) {
                            OutlinedTextField(
                                value = uiState.customAmountText,
                                onValueChange = { viewModel.onCustomAmountChanged(it) },
                                label = { Text(stringResource(R.string.custom_amount_label)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = uiState.customAmountError != null,
                                supportingText = uiState.customAmountError?.let { { Text(it, color = Color.Red) } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                        }

                        Text(stringResource(R.string.payment_method_label), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = true,
                                onClick = {},
                                colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                            )
                            Text(stringResource(R.string.payment_card_ref), fontSize = 13.sp)
                            Spacer(Modifier.width(16.dp))
                            RadioButton(
                                selected = false,
                                onClick = {},
                                colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                            )
                            Text(stringResource(R.string.payment_eft_ref), fontSize = 13.sp)
                        }
                    } else {
                        Text(stringResource(R.string.item_category_label), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            inKindCategories.forEach { cat ->
                                FilterChip(
                                    selected = uiState.inKindCategory == cat,
                                    onClick = { viewModel.onInKindCategorySelected(cat) },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = uiState.itemDescription,
                            onValueChange = { viewModel.onItemDescriptionChanged(it) },
                            label = { Text(stringResource(R.string.item_desc_quantity_label)) },
                            placeholder = { Text(stringResource(R.string.item_desc_quantity_placeholder)) },
                            isError = uiState.itemDescError != null,
                            supportingText = uiState.itemDescError?.let { { Text(it, color = Color.Red) } },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                        )

                        Text(stringResource(R.string.fulfillment_preference_label), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !uiState.isPickup,
                                onClick = { viewModel.onPickupToggled(false) },
                                label = { Text("Drop-off at Hub") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                            )
                            FilterChip(
                                selected = uiState.isPickup,
                                onClick = { viewModel.onPickupToggled(true) },
                                label = { Text("Request Collection") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            var valid = true
                            if (uiState.isMonetaryTab && uiState.isCustomAmountSelected) {
                                val parsed = uiState.customAmountText.toDoubleOrNull()
                                if (parsed == null || parsed <= 0.0) {
                                    viewModel.onCustomAmountChanged("")
                                    valid = false
                                }
                            }
                            if (!uiState.isMonetaryTab && uiState.itemDescription.isBlank()) {
                                viewModel.onItemDescriptionChanged("")
                                valid = false
                            }

                            if (valid) {
                                currentStep = 2
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.btn_review_pledge), fontWeight = FontWeight.Bold)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ReviewItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label.uppercase(), fontSize = 11.sp, color = AppTextMuted, fontWeight = FontWeight.Bold)
        Text(value.ifBlank { "-" }, fontSize = 14.sp, color = AppTextInk, fontWeight = FontWeight.Medium)
    }
}
