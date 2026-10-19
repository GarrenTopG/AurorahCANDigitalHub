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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.model.DonationRequest
import com.aurorah.can.ui.theme.BrandPale
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.TextInk
import com.aurorah.can.ui.theme.TextMuted
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DonateSubTab(
    repository: ApiRepository,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isMonetary by remember { mutableStateOf(true) }

    val monetaryCategories = listOf("Food Parcels", "Winter Relief", "General Fund")
    var selectedMonetaryCategory by remember { mutableStateOf(monetaryCategories[0]) }
    val amountPresets = listOf(50.0, 100.0, 250.0)
    var selectedAmount by remember { mutableStateOf(100.0) }
    var customAmountText by remember { mutableStateOf("") }
    var isCustomSelected by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("Credit Card") }

    val inKindCategories = listOf("Food Parcels", "Clothing", "Household Goods", "School Supplies")
    var selectedInKindCategory by remember { mutableStateOf(inKindCategories[0]) }
    var itemDescription by remember { mutableStateOf("") }
    val preferences = listOf("Drop-off at Hub", "Request Collection")
    var selectedPreference by remember { mutableStateOf(preferences[0]) }

    var itemDescError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    var currentStep by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var transactionRefResult by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                onClick = { isCustomSelected = true },
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
fun ReviewItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label.uppercase(), fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        Text(value.ifBlank { "-" }, fontSize = 14.sp, color = TextInk, fontWeight = FontWeight.Medium)
    }
}
