package com.aurorah.can.ui.getinvolved.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.donation.DonationUiState
import com.aurorah.can.ui.donation.DonationViewModel
import com.aurorah.can.ui.theme.AppBrandPale
import com.aurorah.can.ui.theme.AppLineColor
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted

/**
 * In-kind donation form section for capturing physical goods offers, item categories,
 * item descriptions, and fulfillment preferences (Drop-off vs. Collection).
 *
 * @param uiState Current UI state containing category, description, and fulfillment details.
 * @param viewModel ViewModel dispatching state updates.
 * @param inKindCategories Supported category options (e.g. Non-Perishable Food, Clothing, Books).
 * @param inKindDropdownExpanded Controls visibility of the item category dropdown menu.
 * @param onInKindDropdownExpandedChange Callback toggling dropdown expanded state.
 */
@Composable
fun InKindDonationForm(
    uiState: DonationUiState,
    viewModel: DonationViewModel,
    inKindCategories: List<String>,
    inKindDropdownExpanded: Boolean,
    onInKindDropdownExpandedChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("In-Kind Contribution Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTextInk)

        // Item Category Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Item Category *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedCard(
                    onClick = { onInKindDropdownExpandedChange(!inKindDropdownExpanded) },
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
                    onDismissRequest = { onInKindDropdownExpandedChange(false) },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    inKindCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category, fontSize = 14.sp) },
                            onClick = {
                                viewModel.onInKindCategorySelected(category)
                                onInKindDropdownExpandedChange(false)
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
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
        )

        // Fulfillment Method Radio Options
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Fulfillment Method", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedCard(
                    onClick = { viewModel.onPickupToggled(false) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (!uiState.isPickup) 2.dp else 1.dp,
                        color = if (!uiState.isPickup) MaterialTheme.colorScheme.primary else AppLineColor
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
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("I will drop off", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppTextInk)
                    }
                }

                OutlinedCard(
                    onClick = { viewModel.onPickupToggled(true) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (uiState.isPickup) 2.dp else 1.dp,
                        color = if (uiState.isPickup) MaterialTheme.colorScheme.primary else AppLineColor
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
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
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
                    Text("Drop-off Location:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
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
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}
