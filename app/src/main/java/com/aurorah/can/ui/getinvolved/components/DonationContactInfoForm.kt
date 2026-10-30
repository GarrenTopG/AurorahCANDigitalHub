package com.aurorah.can.ui.getinvolved.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.components.CountryCodeDropdown
import com.aurorah.can.ui.donation.DonationUiState
import com.aurorah.can.ui.donation.DonationViewModel
import com.aurorah.can.ui.theme.AppTextInk

/**
 * Donor contact information form section supporting optional anonymity, full name, email,
 * international dial code selector with phone number, and custom encouragement messages.
 *
 * @param uiState Current UI state containing contact values and anonymity preference.
 * @param viewModel ViewModel dispatching state updates.
 */
@Composable
fun DonationContactInfoForm(uiState: DonationUiState, viewModel: DonationViewModel) {
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
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
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
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
        )

        // Email Address Field
        OutlinedTextField(
            value = uiState.donorEmail,
            onValueChange = { viewModel.onDonorEmailChanged(it) },
            label = { Text("Email Address (Optional)") },
            placeholder = { Text("e.g. email@example.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
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
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
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
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
        )
    }
}
