package com.aurorah.can.ui.getinvolved.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.donation.DonationUiState
import com.aurorah.can.ui.donation.DonationViewModel
import com.aurorah.can.ui.theme.AppBrandPale
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppLineColor
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted

/**
 * Monetary pledge form section supporting preset amounts, custom values, Section 18A tax
 * certificate requests, and targeted community fund selection.
 *
 * @param uiState Current UI state containing amount selections, errors, and tax preferences.
 * @param viewModel ViewModel dispatching state updates.
 * @param amountPresets List of recommended monetary pledge preset options (e.g. R50, R200).
 * @param fundOptions List of available fund allocation targets (e.g., General Fund, specific cause).
 * @param fundDropdownExpanded Controls visibility of the fund allocation dropdown menu.
 * @param onFundDropdownExpandedChange Callback toggling the dropdown state.
 */
@Composable
fun MonetaryDonationForm(
    uiState: DonationUiState,
    viewModel: DonationViewModel,
    amountPresets: List<Double>,
    fundOptions: List<String>,
    fundDropdownExpanded: Boolean,
    onFundDropdownExpandedChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Monetary pledge amount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTextInk)

        // Preset Amount Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val presetRows = amountPresets.chunked(2)
            presetRows.forEach { rowAmounts ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowAmounts.forEach { amt ->
                        val isSelected = !uiState.isCustomAmountSelected && uiState.selectedAmount == amt
                        OutlinedCard(
                            onClick = { viewModel.onAmountSelected(amt, isCustom = false) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else AppLineColor
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) AppBrandPale else AppCardSurface
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("R${amt.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppTextInk)
                                Text("Pledge", fontSize = 11.sp, color = AppTextMuted)
                            }
                        }
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
                uiState.customAmountError?.let { err ->
                    Text(err, color = Color.Red, fontSize = 12.sp)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
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
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
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
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                        )
                        OutlinedTextField(
                            value = uiState.taxAddress,
                            onValueChange = { viewModel.onTaxAddressChanged(it) },
                            label = { Text("Tax address *") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                        )
                        uiState.taxError?.let { err ->
                            Text(err, color = Color.Red, fontSize = 12.sp)
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
                    onClick = { onFundDropdownExpandedChange(!fundDropdownExpanded) },
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
                    onDismissRequest = { onFundDropdownExpandedChange(false) },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    fundOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontSize = 14.sp) },
                            onClick = {
                                viewModel.onFundCategorySelected(option)
                                onFundDropdownExpandedChange(false)
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
}
