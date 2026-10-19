package com.aurorah.can.ui.getinvolved

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.data.model.DonationRequest
import com.aurorah.can.data.model.VolunteerRequest
import com.aurorah.can.ui.components.PrivacyDialog
import com.aurorah.can.ui.home.BrandPale
import com.aurorah.can.ui.home.BrandPrimary
import com.aurorah.can.ui.home.PageBackground
import com.aurorah.can.ui.home.TextInk
import com.aurorah.can.ui.home.TextMuted
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetInvolvedScreen(
    repository: ApiRepository,
    userEmail: String,
    userName: String = "",
    userPhone: String = "",
    initialSubTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(initialSubTab) }
    val tabTitles = listOf("Needs", "Donate", "Volunteer")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = PageBackground)
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color.White,
            contentColor = BrandPrimary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        when (selectedSubTab) {
            0 -> CommunityNeedsSubTab(repository = repository, userEmail = userEmail)
            1 -> DonateSubTab(repository = repository, userEmail = userEmail)
            2 -> VolunteerSubTab(repository = repository, userEmail = userEmail, initialName = userName, initialPhone = userPhone)
        }
    }
}

// ---------------------------------------------------------------------------
// SUB-TAB 1: COMMUNITY NEEDS
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CommunityNeedsSubTab(
    repository: ApiRepository,
    userEmail: String
) {
    var needsList by remember { mutableStateOf<List<CommunityNeed>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Food", "Clothing", "Education", "Healthcare")

    var selectedNeedForOffer by remember { mutableStateOf<CommunityNeed?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun loadNeeds() {
        isLoading = true
        coroutineScope.launch {
            needsList = repository.getNeeds()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadNeeds()
    }

    if (selectedNeedForOffer != null) {
        OfferAssistanceDialog(
            need = selectedNeedForOffer!!,
            repository = repository,
            userEmail = userEmail,
            onDismiss = { selectedNeedForOffer = null },
            onSuccess = { msg ->
                feedbackMessage = msg
            }
        )
    }

    if (feedbackMessage != null) {
        AlertDialog(
            onDismissRequest = { feedbackMessage = null },
            confirmButton = {
                Button(
                    onClick = { feedbackMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("OK")
                }
            },
            title = { Text("Offer Logged", fontWeight = FontWeight.Bold) },
            text = { Text(feedbackMessage!!) },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Current Community Needs",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextInk
        )
        Text(
            text = "Explore urgent needs identified across Kuils River communities and offer your time or skills to help.",
            fontSize = 14.sp,
            color = TextMuted,
            lineHeight = 20.sp
        )

        // Category Filter Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedFilter == category,
                    onClick = { selectedFilter = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        val filteredNeeds = remember(needsList, selectedFilter) {
            if (selectedFilter == "All") needsList
            else needsList.filter { it.category.equals(selectedFilter, ignoreCase = true) }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPrimary)
                }
            }
            filteredNeeds.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No community needs found for '$selectedFilter'",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                        OutlinedButton(onClick = { loadNeeds() }) {
                            Text("Refresh List")
                        }
                    }
                }
            }
            else -> {
                filteredNeeds.forEach { need ->
                    NeedItemCard(need = need, onOfferHelp = { selectedNeedForOffer = need })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NeedItemCard(
    need: CommunityNeed,
    onOfferHelp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = need.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextInk,
                    modifier = Modifier.weight(1f)
                )
                PriorityBadge(priority = need.priority)
            }

            if (need.description.isNotBlank()) {
                Text(
                    text = need.description,
                    fontSize = 14.sp,
                    color = TextMuted,
                    lineHeight = 20.sp
                )
            }

            if (need.location.isNotBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(text = need.location, fontSize = 13.sp, color = TextMuted)
                }
            }

            Button(
                onClick = onOfferHelp,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Handshake,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp).size(18.dp)
                )
                Text("Offer Assistance", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val (bgColor, textColor) = when (priority.lowercase()) {
        "critical" -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        "high" -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
        "medium" -> Color(0xFFFEF9C3) to Color(0xFF854D0E)
        else -> Color(0xFFDCFCE7) to Color(0xFF166534)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = priority.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// SUB-TAB 2: DONATE
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DonateSubTab(
    repository: ApiRepository,
    userEmail: String
) {
    val context = LocalContext.current
    var isMonetary by remember { mutableStateOf(true) }

    // Monetary state
    val monetaryCategories = listOf("Food Parcels", "Winter Relief", "General Fund")
    var selectedMonetaryCategory by remember { mutableStateOf(monetaryCategories[0]) }
    val amountPresets = listOf(50.0, 100.0, 250.0)
    var selectedAmount by remember { mutableStateOf(100.0) }
    var customAmountText by remember { mutableStateOf("") }
    var isCustomSelected by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("Credit Card") }

    // In-Kind state
    val inKindCategories = listOf("Food Parcels", "Clothing", "Household Goods", "School Supplies")
    var selectedInKindCategory by remember { mutableStateOf(inKindCategories[0]) }
    var itemDescription by remember { mutableStateOf("") }
    val preferences = listOf("Drop-off at Hub", "Request Collection")
    var selectedPreference by remember { mutableStateOf(preferences[0]) }

    var itemDescError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    // Flow Step: 1 = Form, 2 = Review, 3 = Confirmed
    var currentStep by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var transactionRefResult by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Make a Donation Pledge",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextInk
        )

        // Notice: Pledge only
        Surface(
            color = BrandPale,
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
                    Icon(Icons.Filled.Info, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                    Text("Donation Pledge Notice", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 14.sp)
                }
                Text(
                    text = "Submitting a donation in the app records a pledge reference. The mobile app does not process direct card/bank payments.",
                    fontSize = 13.sp,
                    color = TextInk,
                    lineHeight = 18.sp
                )
            }
        }

        // Section 18A Tax Receipt Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Need a Section 18A Tax Receipt?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextInk
                )
                Text(
                    text = "Section 18A tax receipts are managed through our web platform. Tap below to visit the official portal.",
                    fontSize = 13.sp,
                    color = TextMuted,
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
                    Text("Visit Website for Section 18A Receipt", fontSize = 13.sp)
                }
            }
        }

        if (currentStep == 3) {
            // Confirmation Screen
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(56.dp))
                    Text(
                        text = if (isMonetary) "Monetary Pledge Received" else "In-Kind Pledge Received",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextInk
                    )
                    if (transactionRefResult.isNotBlank()) {
                        Text(
                            text = "Reference: $transactionRefResult",
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = "Thank you for supporting Aurorah CAN. Our team will reference your pledge and keep you updated.",
                        fontSize = 14.sp,
                        color = TextMuted,
                        lineHeight = 20.sp
                    )
                    Button(
                        onClick = {
                            currentStep = 1
                            itemDescription = ""
                            customAmountText = ""
                            isCustomSelected = false
                            serverError = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Make Another Donation")
                    }
                }
            }
        } else if (currentStep == 2) {
            // Review Pledge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Review Donation Pledge", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextInk)

                    if (isMonetary) {
                        val finalAmount = if (isCustomSelected) customAmountText.toDoubleOrNull() ?: selectedAmount else selectedAmount
                        ReviewItem("Donation Type", "Monetary")
                        ReviewItem("Fund / Category", selectedMonetaryCategory)
                        ReviewItem("Pledge Amount", "R ${String.format(Locale.US, "%.2f", finalAmount)}")
                        ReviewItem("Preferred Method", paymentMethod)
                    } else {
                        ReviewItem("Donation Type", "In-Kind Goods")
                        ReviewItem("Item Category", selectedInKindCategory)
                        ReviewItem("Description", itemDescription)
                        ReviewItem("Fulfillment", selectedPreference)
                    }

                    if (serverError != null) {
                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(10.dp)) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
                                Text(serverError!!, color = Color(0xFF991B1B), fontSize = 13.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(onClick = { currentStep = 1 }, modifier = Modifier.weight(1f)) {
                            Text("Back")
                        }
                        Button(
                            onClick = {
                                isLoading = true
                                serverError = null
                                coroutineScope.launch {
                                    val req = if (isMonetary) {
                                        val finalAmt = if (isCustomSelected) customAmountText.toDoubleOrNull() ?: selectedAmount else selectedAmount
                                        DonationRequest(
                                            donationType = "Monetary",
                                            category = selectedMonetaryCategory,
                                            amount = finalAmt,
                                            paymentMethod = paymentMethod,
                                            userEmail = userEmail
                                        )
                                    } else {
                                        DonationRequest(
                                            donationType = "In-Kind",
                                            category = selectedInKindCategory,
                                            itemDescription = itemDescription,
                                            preference = selectedPreference,
                                            userEmail = userEmail
                                        )
                                    }

                                    val response = repository.submitDonation(req)
                                    isLoading = false
                                    if (response.success) {
                                        transactionRefResult = response.transactionRef
                                        currentStep = 3
                                    } else {
                                        serverError = response.message.ifBlank { "Failed to process pledge" }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Submit Pledge")
                            }
                        }
                    }
                }
            }
        } else {
            // Form Step
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Type Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isMonetary,
                            onClick = { isMonetary = true },
                            label = { Text("Monetary Pledge") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !isMonetary,
                            onClick = { isMonetary = false },
                            label = { Text("In-Kind Goods") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (isMonetary) {
                        // Monetary Form
                        Text("Monetary Category *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            monetaryCategories.forEach { cat ->
                                FilterChip(
                                    selected = selectedMonetaryCategory == cat,
                                    onClick = { selectedMonetaryCategory = cat },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                                )
                            }
                        }

                        Text("Select Amount *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            amountPresets.forEach { amt ->
                                FilterChip(
                                    selected = !isCustomSelected && selectedAmount == amt,
                                    onClick = {
                                        selectedAmount = amt
                                        isCustomSelected = false
                                        amountError = null
                                    },
                                    label = { Text("R ${amt.toInt()}") },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                                )
                            }
                            FilterChip(
                                selected = isCustomSelected,
                                onClick = {
                                    isCustomSelected = true
                                },
                                label = { Text("Custom Amount") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                            )
                        }

                        if (isCustomSelected) {
                            OutlinedTextField(
                                value = customAmountText,
                                onValueChange = {
                                    customAmountText = it
                                    amountError = null
                                },
                                label = { Text("Custom Amount (ZAR) *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = amountError != null,
                                supportingText = amountError?.let { { Text(it, color = Color.Red) } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                            )
                        }

                        Text("Payment Reference Method", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = paymentMethod == "Credit Card", onClick = { paymentMethod = "Credit Card" }, colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary))
                            Text("Credit Card Reference", fontSize = 13.sp)
                            Spacer(Modifier.width(16.dp))
                            RadioButton(selected = paymentMethod == "Direct Bank Transfer", onClick = { paymentMethod = "Direct Bank Transfer" }, colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary))
                            Text("EFT / Direct Transfer", fontSize = 13.sp)
                        }
                    } else {
                        // In-Kind Form
                        Text("Item Category *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            inKindCategories.forEach { cat ->
                                FilterChip(
                                    selected = selectedInKindCategory == cat,
                                    onClick = { selectedInKindCategory = cat },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = itemDescription,
                            onValueChange = {
                                itemDescription = it
                                itemDescError = null
                            },
                            label = { Text("Item Description & Quantity *") },
                            placeholder = { Text("e.g. 5 boxes of non-perishable canned food, 10 winter blankets") },
                            isError = itemDescError != null,
                            supportingText = itemDescError?.let { { Text(it, color = Color.Red) } },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                        )

                        Text("Fulfillment Preference *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            preferences.forEach { pref ->
                                FilterChip(
                                    selected = selectedPreference == pref,
                                    onClick = { selectedPreference = pref },
                                    label = { Text(pref) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            var valid = true
                            if (isMonetary && isCustomSelected) {
                                val parsed = customAmountText.toDoubleOrNull()
                                if (parsed == null || parsed <= 0.0) {
                                    amountError = "Enter a valid donation amount"
                                    valid = false
                                }
                            }
                            if (!isMonetary && itemDescription.isBlank()) {
                                itemDescError = "Please describe the items you wish to donate"
                                valid = false
                            }

                            if (valid) {
                                currentStep = 2
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Review Pledge", fontWeight = FontWeight.Bold)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ReviewItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label.uppercase(), fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        Text(value.ifBlank { "-" }, fontSize = 14.sp, color = TextInk, fontWeight = FontWeight.Medium)
    }
}

// ---------------------------------------------------------------------------
// SUB-TAB 3: VOLUNTEER
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VolunteerSubTab(
    repository: ApiRepository,
    userEmail: String,
    initialName: String = "",
    initialPhone: String = ""
) {
    var firstName by remember {
        val split = initialName.trim().split(" ")
        mutableStateOf(split.firstOrNull() ?: "")
    }
    var lastName by remember {
        val split = initialName.trim().split(" ")
        mutableStateOf(if (split.size > 1) split.drop(1).joinToString(" ") else "")
    }
    var emailInput by remember { mutableStateOf(if (userEmail != "guest@aurorahcan.org") userEmail else "") }
    var countryCode by remember { mutableStateOf("+27") }
    var phoneInput by remember { mutableStateOf(initialPhone) }
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                            text = "Your name, area, phone number, skills and availability are required to assess and follow up on your application. Email is optional. If required information is missing, we may be unable to review your application.",
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
