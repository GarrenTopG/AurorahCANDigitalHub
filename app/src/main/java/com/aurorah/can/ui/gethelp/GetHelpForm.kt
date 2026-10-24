package com.aurorah.can.ui.gethelp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.components.CountryCodeDropdown
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.ui.theme.BrandPrimary

@Composable
fun GetHelpForm(
    uiState: GetHelpUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onCountryCodeChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onUrgencyChange: (String) -> Unit,
    onPopiaConsentChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val needTypes = listOf(
        "Food Parcel" to "Food Parcel",
        "Financial Aid" to "Financial or practical support",
        "Medical Assistance" to "Health-related support or referral",
        "Educational Support" to "Education and youth support",
        "Employment Help" to "Community support referral",
        "Housing" to "Housing-related support or referral",
        "Other" to "Other"
    )
    var needTypeExpanded by remember { mutableStateOf(false) }
    val urgencyOptions = listOf("Standard", "High", "Critical")

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Your Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTextInk)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = onFirstNameChange,
                    label = { Text("First Name *") },
                    placeholder = { Text("e.g. John") },
                    isError = uiState.firstNameError != null,
                    supportingText = uiState.firstNameError?.let { { Text(it, color = Color.Red) } },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                )
                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = onLastNameChange,
                    label = { Text("Last Name *") },
                    placeholder = { Text("e.g. Doe") },
                    isError = uiState.lastNameError != null,
                    supportingText = uiState.lastNameError?.let { { Text(it, color = Color.Red) } },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Phone Number *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    CountryCodeDropdown(
                        selectedCode = uiState.countryCode,
                        onCodeSelected = onCountryCodeChange
                    )
                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        placeholder = { Text("82 123 4567") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = uiState.phoneError != null,
                        supportingText = uiState.phoneError?.let { { Text(it, color = Color.Red) } },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.location,
                onValueChange = onLocationChange,
                label = { Text("Suburb or Area *") },
                placeholder = { Text("e.g. Sunbird Park, Kuils River") },
                supportingText = {
                    if (uiState.locationError != null) {
                        Text(uiState.locationError, color = Color.Red)
                    } else {
                        Text("Requests are served within Kuils River and nearby communities.", fontSize = 12.sp, color = AppTextMuted)
                    }
                },
                isError = uiState.locationError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
            )

            Text("Assistance Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTextInk)

            // Category Dropdown
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Type of Support Needed *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)
                Box {
                    OutlinedTextField(
                        value = needTypes.find { it.first == uiState.selectedCategory }?.second ?: uiState.selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select support type...") },
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                        isError = uiState.categoryError != null,
                        supportingText = uiState.categoryError?.let { { Text(it, color = Color.Red) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { needTypeExpanded = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = if (uiState.categoryError != null) Color.Red else Color(0xFFD1D5DB),
                            disabledTextColor = AppTextInk,
                            disabledLabelColor = AppTextMuted,
                            disabledPlaceholderColor = AppTextMuted,
                            disabledTrailingIconColor = AppTextInk
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { needTypeExpanded = true }
                    )
                    DropdownMenu(
                        expanded = needTypeExpanded,
                        onDismissRequest = { needTypeExpanded = false }
                    ) {
                        needTypes.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label, fontSize = 14.sp) },
                                onClick = {
                                    onCategoryChange(key)
                                    needTypeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Description Box
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text("Describe Your Need *") },
                placeholder = { Text("Describe what support is needed and any relevant circumstances...") },
                isError = uiState.descriptionError != null,
                supportingText = uiState.descriptionError?.let { { Text(it, color = Color.Red) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
            )

            // Urgency Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Urgency Level", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppTextInk)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    urgencyOptions.forEach { option ->
                        val isSelected = uiState.selectedUrgency.equals(option, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onUrgencyChange(option) },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // POPIA Consent
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.clickable { onPopiaConsentChange(!uiState.popiaConsent) }
                ) {
                    Checkbox(
                        checked = uiState.popiaConsent,
                        onCheckedChange = onPopiaConsentChange,
                        colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "I have read the privacy information and agree that Aurorah NPC may record and use these details to evaluate my request and contact me about potential assistance.",
                        fontSize = 13.sp,
                        color = AppTextInk,
                        lineHeight = 18.sp
                    )
                }
                if (uiState.popiaError != null) {
                    Text(uiState.popiaError, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
                }
            }

            if (uiState.serverError != null) {
                Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
                        Text(uiState.serverError, color = Color(0xFF991B1B), fontSize = 13.sp)
                    }
                }
            }

            Button(
                onClick = onSubmit,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text("Submit Request", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
