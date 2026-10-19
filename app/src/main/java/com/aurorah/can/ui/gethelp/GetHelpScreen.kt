package com.aurorah.can.ui.gethelp

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.model.AssistanceRequest
import com.aurorah.can.ui.components.PrivacyDialog
import com.aurorah.can.ui.components.*
import com.aurorah.can.ui.home.BrandPale
import com.aurorah.can.ui.home.BrandPrimary
import com.aurorah.can.ui.home.PageBackground
import com.aurorah.can.ui.home.TextInk
import com.aurorah.can.ui.home.TextMuted
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GetHelpScreen(
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
    var countryCode by remember { mutableStateOf("+27") }
    var phoneInput by remember { mutableStateOf(userPhone) }
    var locationInput by remember { mutableStateOf("") }

    val needTypes = listOf(
        "Food Parcel" to "Food Parcel",
        "Financial Aid" to "Financial or practical support",
        "Medical Assistance" to "Health-related support or referral",
        "Educational Support" to "Education and youth support",
        "Employment Help" to "Community support referral",
        "Housing" to "Housing-related support or referral",
        "Other" to "Other"
    )
    var selectedNeedType by remember { mutableStateOf("") }
    var needTypeExpanded by remember { mutableStateOf(false) }

    var descriptionInput by remember { mutableStateOf("") }

    val urgencyOptions = listOf("Standard", "High", "Critical")
    var selectedUrgencyLabel by remember { mutableStateOf("Standard") }

    var popiaConsent by remember { mutableStateOf(false) }

    // Errors
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var needTypeError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var popiaError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var isSubmittedSuccess by remember { mutableStateOf(false) }
    var submittedFullName by remember { mutableStateOf("") }
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

        // Header Banner Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BrandPrimary)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Handshake,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Text(
                    text = "Request Assistance",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Local support requests for Kuils River and nearby communities",
                    color = Color(0xFFFCE7F3),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        if (isSubmittedSuccess) {
            // Success State Box
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
                        shape = CircleShape,
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
                        text = "Request Submitted",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46)
                    )
                    Text(
                        text = "Thank you, ${submittedFullName.ifBlank { "Applicant" }}. Your request has been received for review. Submitting a request does not guarantee assistance; support depends on the circumstances and resources available.",
                        fontSize = 14.sp,
                        color = Color(0xFF047857),
                        lineHeight = 20.sp
                    )
                    Button(
                        onClick = {
                            isSubmittedSuccess = false
                            descriptionInput = ""
                            selectedNeedType = ""
                            locationInput = ""
                            popiaConsent = false
                            serverError = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Another Request", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Introductory Banners & Callouts
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Left Border Callout
                Surface(
                    color = BrandPale,
                    shape = RoundedCornerShape(12.dp),
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
                                .height(56.dp)
                                .background(color = BrandPrimary, shape = RoundedCornerShape(2.dp))
                        )
                        Text(
                            text = "Aurorah CAN considers requests from Kuils River, Sunbird Park and nearby Cape Flats communities. Support may include community care, food support or referral to another service. Requests are reviewed individually and assistance depends on eligibility and available resources.",
                            fontSize = 13.sp,
                            color = TextInk,
                            lineHeight = 19.sp
                        )
                    }
                }

                // Emergency Services Warning Notice
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(20.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Not an emergency service:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "If you are in immediate danger or need urgent medical help, call 112 from a mobile phone. For a police emergency, call 10111.",
                                fontSize = 13.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Privacy hint paragraph
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "We ask for your name, area, phone number, the type of support and a short description so authorised staff can review the request and contact you. Do not include information that is not needed. Read the ",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { showPrivacyDialog = true }) {
                        Text("Privacy & POPIA notice", fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Main Assistance Application Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Section 1: Personal Information
                    Text(
                        text = "Personal Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextInk
                    )
                    HorizontalDivider(color = Color(0xFFE5E7EB))

                    // First Name & Last Name
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
                            placeholder = { Text("e.g. Jane") },
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

                    // Country Code + Phone Number Input
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
                        label = { Text("Suburb or area *") },
                        placeholder = { Text("e.g. Sunbird Park, Kuils River") },
                        isError = locationError != null,
                        supportingText = locationError?.let { { Text(it, color = Color.Red) } },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Section 2: Your Need
                    Text(
                        text = "Your Need",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextInk
                    )
                    HorizontalDivider(color = Color(0xFFE5E7EB))

                    // Type of Assistance Needed Dropdown
                    ExposedDropdownMenuBox(
                        expanded = needTypeExpanded,
                        onExpandedChange = { needTypeExpanded = !needTypeExpanded }
                    ) {
                        val displayValue = needTypes.find { it.first == selectedNeedType }?.second ?: selectedNeedType
                        OutlinedTextField(
                            value = displayValue,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type of Assistance Needed *") },
                            placeholder = { Text("Select type of need...") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = needTypeExpanded) },
                            isError = needTypeError != null,
                            supportingText = needTypeError?.let { { Text(it, color = Color.Red) } },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = needTypeExpanded,
                            onDismissRequest = { needTypeExpanded = false }
                        ) {
                            needTypes.forEach { (value, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, fontSize = 14.sp) },
                                    onClick = {
                                        selectedNeedType = value
                                        needTypeExpanded = false
                                        needTypeError = null
                                    }
                                )
                            }
                        }
                    }

                    // Describe Your Need Textarea
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = {
                            descriptionInput = it
                            descriptionError = null
                        },
                        label = { Text("Describe Your Need *") },
                        placeholder = { Text("Please provide details about what assistance you need and why...") },
                        isError = descriptionError != null,
                        supportingText = descriptionError?.let { { Text(it, color = Color.Red) } },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                    )

                    // How Urgent Is Your Need? FilterChips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("How Urgent Is Your Need?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextInk)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            urgencyOptions.forEach { option ->
                                FilterChip(
                                    selected = selectedUrgencyLabel == option,
                                    onClick = { selectedUrgencyLabel = option },
                                    label = { Text(option) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Section 3: POPIA Agreement & Submit
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Your name, suburb, phone number, support category and description are required for review and contact. If you do not provide them, Aurorah CAN may be unable to assess or respond to your request. Your details are used by authorised staff and, where needed, relevant community partners; they are not published as a public story.",
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
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "I have read the privacy information and agree that Aurorah NPC may use these details to assess this assistance request and contact me about it.",
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
                            if (selectedNeedType.isBlank()) {
                                needTypeError = "Please select the type of assistance needed"
                                valid = false
                            }
                            if (descriptionInput.isBlank()) {
                                descriptionError = "Please describe what assistance you need"
                                valid = false
                            }
                            if (!popiaConsent) {
                                popiaError = "Consent is required to submit a request"
                                valid = false
                            }

                            if (valid) {
                                isLoading = true
                                serverError = null
                                val mapUrgency = when (selectedUrgencyLabel) {
                                    "Critical" -> "Critical"
                                    "High" -> "High"
                                    else -> "Medium"
                                }
                                val full = "$firstName $lastName".trim()

                                coroutineScope.launch {
                                    val response = repository.requestAssistance(
                                        AssistanceRequest(
                                            firstName = firstName,
                                            lastName = lastName,
                                            countryCode = countryCode,
                                            contactPhone = phoneInput,
                                            areaLocation = locationInput,
                                            category = selectedNeedType,
                                            description = descriptionInput,
                                            urgency = mapUrgency,
                                            privacyConsent = popiaConsent,
                                            userEmail = userEmail
                                        )
                                    )
                                    isLoading = false
                                    if (response.success) {
                                        submittedFullName = full
                                        isSubmittedSuccess = true
                                    } else {
                                        serverError = response.message.ifBlank { "Request submission failed" }
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp).size(20.dp)
                            )
                            Text("Submit Request", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
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
