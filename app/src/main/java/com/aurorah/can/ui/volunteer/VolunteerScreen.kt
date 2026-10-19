package com.aurorah.can.ui.volunteer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.model.VolunteerRequest
import com.aurorah.can.ui.components.PrivacyDialog
import com.aurorah.can.ui.components.*
import com.aurorah.can.ui.home.BrandPale
import com.aurorah.can.ui.home.BrandPrimary
import com.aurorah.can.ui.home.PageBackground
import com.aurorah.can.ui.home.TextInk
import com.aurorah.can.ui.home.TextMuted
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VolunteerScreen(
    repository: ApiRepository,
    userEmail: String,
    userName: String = "",
    userPhone: String = "",
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var firstName by remember {
        val split = userName.trim().split(" ")
        mutableStateOf(split.firstOrNull() ?: "")
    }
    var lastName by remember {
        val split = userName.trim().split(" ")
        mutableStateOf(if (split.size > 1) split.drop(1).joinToString(" ") else "")
    }
    var emailInput by remember { mutableStateOf(if (userEmail != "guest@aurorahcan.org") userEmail else "") }
    var countryCode by remember { mutableStateOf("+27") }
    var phoneInput by remember { mutableStateOf(userPhone) }
    var locationInput by remember { mutableStateOf("") }
    var skillsInput by remember { mutableStateOf("") }

    val daysList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val selectedDays = remember { mutableStateListOf<String>() }

    var popiaConsent by remember { mutableStateOf(false) }

    // Errors
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var skillsError by remember { mutableStateOf<String?>(null) }
    var daysError by remember { mutableStateOf<String?>(null) }
    var popiaError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var isSubmittedSuccess by remember { mutableStateOf(false) }
    var serverError by remember { mutableStateOf<String?>(null) }

    var showPrivacyDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    if (showPrivacyDialog) {
        PrivacyDialog(onDismiss = { showPrivacyDialog = false })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (onBackClick != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBackClick() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Back to Home",
                    color = BrandPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BrandPrimary)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.People,
                        contentDescription = null,
                        tint = Color(0xFFFCE7F3),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "VOLUNTEER WITH AURORAH CAN",
                        color = Color(0xFFFCE7F3),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Become a Volunteer",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Contribute time and skills to local community work",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }

        if (isSubmittedSuccess) {
            // Success State
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                border = BorderStroke(1.dp, Color(0xFFA7F3D0))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        color = Color(0xFF10B981),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Text(
                        text = "Thank You, ${firstName.ifBlank { "Volunteer" }}!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46)
                    )
                    Text(
                        text = "Your application has been received. Applying does not guarantee placement; opportunities depend on suitability, an interview and any role-specific screening.",
                        fontSize = 14.sp,
                        color = Color(0xFF047857),
                        lineHeight = 20.sp
                    )
                    Button(
                        onClick = {
                            isSubmittedSuccess = false
                            firstName = ""
                            lastName = ""
                            skillsInput = ""
                            locationInput = ""
                            phoneInput = ""
                            selectedDays.clear()
                            popiaConsent = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Another Application", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Intro Callout Notice
        Surface(
            color = BrandPale,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFFBCFE8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(48.dp)
                        .background(color = BrandPrimary, shape = RoundedCornerShape(2.dp))
                )
                Text(
                    text = "Volunteer with Aurorah CAN in Kuils River and nearby communities. These are examples of the kinds of local roles currently described by the organisation; availability may vary.",
                    fontSize = 13.sp,
                    color = TextInk,
                    lineHeight = 19.sp
                )
            }
        }

        // Example Roles Section
        Text("Volunteer Opportunities", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextInk)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            RoleCard(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                title = "Community library assistant",
                description = "Support the Sunbird Park mini-library, with an example commitment of around four hours a week."
            )
            RoleCard(
                icon = Icons.Filled.Inventory,
                title = "Food distribution and logistics",
                description = "Help sort and pack community food parcels, with weekend shifts as an example."
            )
            RoleCard(
                icon = Icons.Filled.CalendarMonth,
                title = "Events and fundraising",
                description = "Contribute to planning and logistics for local community engagement activities."
            )
        }

        // Onboarding and Safeguarding Box
        Surface(
            color = Color(0xFFFEF3C7),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFFCD34D)),
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
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Onboarding and safeguarding:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F),
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "Applications are reviewed before placement. Candidates may be invited to an interview and orientation. Roles involving direct contact with children require the applicable child-protection checks, including Child Protection Register vetting under the Children’s Act. Additional background checks may apply to a role.",
                    fontSize = 13.sp,
                    color = Color(0xFF78350F),
                    lineHeight = 18.sp
                )
            }
        }

        // Privacy Data Notice
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "We use your name, area, phone number, skills and availability to review your application and contact you. Email is optional. ",
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 17.sp,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = { showPrivacyDialog = true },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Privacy & POPIA notice", fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        if (!isSubmittedSuccess) {
            // Volunteer Application Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Personal Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextInk)

                    // First & Last Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = {
                                firstName = it
                                firstNameError = null
                            },
                            label = { Text("First Name *") },
                            placeholder = { Text("e.g. John") },
                            isError = firstNameError != null,
                            supportingText = firstNameError?.let { { Text(it, color = Color.Red) } },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                        )
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = {
                                lastName = it
                                lastNameError = null
                            },
                            label = { Text("Last Name *") },
                            placeholder = { Text("e.g. Doe") },
                            isError = lastNameError != null,
                            supportingText = lastNameError?.let { { Text(it, color = Color.Red) } },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                        )
                    }

                    // Email Address
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address (Optional)") },
                        placeholder = { Text("name@example.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                    )

                    // Country Code + Phone Number Input Row
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Phone Number *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextInk)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            CountryCodeDropdown(
                                selectedCode = countryCode,
                                onCodeSelected = { countryCode = it }
                            )
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = {
                                    phoneInput = it
                                    phoneError = null
                                },
                                placeholder = { Text("82 123 4567") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                isError = phoneError != null,
                                supportingText = phoneError?.let { { Text(it, color = Color.Red) } },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                            )
                        }
                    }

                    // Suburb or Area
                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = {
                            locationInput = it
                            locationError = null
                        },
                        label = { Text("Suburb or Area *") },
                        placeholder = { Text("e.g. Sunbird Park, Kuils River") },
                        supportingText = {
                            if (locationError != null) {
                                Text(locationError!!, color = Color.Red)
                            } else {
                                Text("Volunteer roles are based in Kuils River and nearby communities.", fontSize = 12.sp, color = TextMuted)
                            }
                        },
                        isError = locationError != null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                    )

                    Text("Your Skills & Availability", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextInk)

                    // Skills Input
                    OutlinedTextField(
                        value = skillsInput,
                        onValueChange = {
                            skillsInput = it
                            skillsError = null
                        },
                        label = { Text("Your Skills & Experience *") },
                        placeholder = { Text("E.g., teaching, healthcare, carpentry, cooking, admin work, logistics, driver...") },
                        isError = skillsError != null,
                        supportingText = skillsError?.let { { Text(it, color = Color.Red) } },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                    )

                    // Days available checkboxes
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
                                val isChecked = selectedDays.contains(day)
                                FilterChip(
                                    selected = isChecked,
                                    onClick = {
                                        if (isChecked) selectedDays.remove(day)
                                        else selectedDays.add(day)
                                        daysError = null
                                    },
                                    label = { Text(day) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                        if (daysError != null) {
                            Text(daysError!!, color = Color.Red, fontSize = 12.sp)
                        }
                    }

                    // POPIA Consent
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Your name, suburb, phone number, skills and availability are required to assess and follow up on your application. Email is optional. If required information is missing, we may be unable to review your application.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )

                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.clickable {
                                popiaConsent = !popiaConsent
                                popiaError = null
                            }
                        ) {
                            Checkbox(
                                checked = popiaConsent,
                                onCheckedChange = {
                                    popiaConsent = it
                                    popiaError = null
                                },
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
                        if (popiaError != null) {
                            Text(popiaError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                    }

                    if (serverError != null) {
                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(10.dp)) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
                                Text(serverError!!, color = Color(0xFF991B1B), fontSize = 13.sp)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            var valid = true
                            if (firstName.isBlank()) {
                                firstNameError = "First name is required"
                                valid = false
                            }
                            if (lastName.isBlank()) {
                                lastNameError = "Last name is required"
                                valid = false
                            }
                            if (phoneInput.isBlank()) {
                                phoneError = "Phone number is required"
                                valid = false
                            }
                            if (locationInput.isBlank()) {
                                locationError = "Suburb or area is required"
                                valid = false
                            }
                            if (skillsInput.isBlank()) {
                                skillsError = "Please describe your skills"
                                valid = false
                            }
                            if (selectedDays.isEmpty()) {
                                daysError = "Please select at least one day you are available."
                                valid = false
                            }
                            if (!popiaConsent) {
                                popiaError = "Consent is required to submit application"
                                valid = false
                            }

                            if (valid) {
                                isLoading = true
                                serverError = null
                                coroutineScope.launch {
                                    val response = repository.submitVolunteer(
                                        VolunteerRequest(
                                            firstName = firstName,
                                            lastName = lastName,
                                            email = emailInput.ifBlank { null },
                                            countryCode = countryCode,
                                            phone = phoneInput,
                                            location = locationInput,
                                            skills = skillsInput,
                                            availability = selectedDays.toList(),
                                            popiaConsent = popiaConsent,
                                            userEmail = userEmail
                                        )
                                    )
                                    isLoading = false
                                    if (response.success) {
                                        isSubmittedSuccess = true
                                    } else {
                                        serverError = response.message.ifBlank { "Application submission failed" }
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                            Text("Submit Application", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CountryCodeDropdown(
    selectedCode: String,
    onCodeSelected: (String) -> Unit
) {
    val countryCodes = listOf(
        "+27" to "🇿🇦 +27",
        "+264" to "🇳🇦 +264",
        "+267" to "🇧🇼 +267",
        "+263" to "🇿🇼 +263",
        "+258" to "🇲🇿 +258",
        "+268" to "🇸🇿 +268",
        "+266" to "🇱🇸 +266"
    )
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = countryCodes.find { it.first == selectedCode }?.second ?: selectedCode

    Box {
        Surface(
            modifier = Modifier
                .clickable { expanded = true }
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF3F4F6),
            border = BorderStroke(1.dp, Color(0xFFD1D5DB))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = displayLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextInk
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Select Country Code",
                    tint = TextMuted
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryCodes.forEach { (code, label) ->
                DropdownMenuItem(
                    text = { Text(label, fontSize = 14.sp) },
                    onClick = {
                        onCodeSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = BrandPale,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.padding(10.dp).size(22.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextInk)
                Text(description, fontSize = 13.sp, color = TextMuted, lineHeight = 18.sp)
            }
        }
    }
}
