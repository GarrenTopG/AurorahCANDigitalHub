package com.aurorah.can.ui.getinvolved.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted

/**
 * Submission section containing POPIA compliance consent check, submission trigger button
 * with loading indicator state, and contextual error message alert banner.
 *
 * @param uiState Current UI state containing consent status, submission loading state, and error message.
 * @param viewModel ViewModel dispatching donation submission request.
 */
@Composable
fun DonationSubmitSection(uiState: DonationUiState, viewModel: DonationViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "We use the details you provide to record and review this pledge or goods offer. Name and contact details are optional; if you provide them, authorised staff may use them to follow up.",
            fontSize = 12.sp,
            color = AppTextMuted,
            lineHeight = 17.sp
        )

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.clickable {
                viewModel.onPopiaConsentChanged(!uiState.popiaConsent)
            }
        ) {
            Checkbox(
                checked = uiState.popiaConsent,
                onCheckedChange = { viewModel.onPopiaConsentChanged(it) },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "I have read the privacy information and agree that Aurorah NPC may use the details I provide to record and review this pledge or offer.",
                fontSize = 13.sp,
                color = AppTextInk,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        Button(
            onClick = { viewModel.submitDonation() },
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.padding(end = 8.dp).size(18.dp))
                Text(
                    text = if (uiState.isMonetaryTab) "Submit Donation Pledge" else "Submit In-Kind Offer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // Error Alert Banner displayed directly BELOW the Submit Button
        uiState.errorMessage?.let { errorMsg ->
            if (errorMsg.isNotBlank()) {
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDA4AF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "We could not submit this pledge.",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF881337),
                                fontSize = 13.sp
                            )
                            Text(
                                text = errorMsg,
                                color = Color(0xFF9F1239),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
