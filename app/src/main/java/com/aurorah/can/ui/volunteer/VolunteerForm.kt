package com.aurorah.can.ui.volunteer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.components.CountryCodeDropdown
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.TextInk
import com.aurorah.can.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VolunteerForm(
    uiState: VolunteerUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onCountryCodeChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onSkillsChange: (String) -> Unit,
    onToggleDay: (String) -> Unit,
    onPopiaConsentChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Personal Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextInk)

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
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
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
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                )
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Email Address (Optional)") },
                placeholder = { Text("name@example.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Phone Number *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextInk)
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
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
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
                        Text("Volunteer roles are based in Kuils River and nearby communities.", fontSize = 12.sp, color = TextMuted)
                    }
                },
                isError = uiState.locationError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
            )

            Text("Your Skills & Availability", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextInk)

            OutlinedTextField(
                value = uiState.skills,
                onValueChange = onSkillsChange,
                label = { Text("Your Skills & Experience *") },
                placeholder = { Text("E.g., teaching, healthcare, carpentry, cooking, admin work, logistics, driver...") },
                isError = uiState.skillsError != null,
                supportingText = uiState.skillsError?.let { { Text(it, color = Color.Red) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Days Available *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextInk)
                    Text("(Select at least one)", fontSize = 12.sp, color = TextMuted)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    daysList.forEach { day ->
                        val isChecked = uiState.selectedDays.contains(day)
                        FilterChip(
                            selected = isChecked,
                            onClick = { onToggleDay(day) },
                            label = { Text(day) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                if (uiState.daysError != null) {
                    Text(uiState.daysError, color = Color.Red, fontSize = 12.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Your name, suburb, phone number, skills and availability are required to assess and follow up on your application. Email is optional. If required information is missing, we may be unable to review your application.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 17.sp
                )

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
                        text = "I have read the privacy information and agree that Aurorah NPC may use these details to review my volunteer application and contact me about it.",
                        fontSize = 13.sp,
                        color = TextInk,
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
                    Text("Submit Application", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
