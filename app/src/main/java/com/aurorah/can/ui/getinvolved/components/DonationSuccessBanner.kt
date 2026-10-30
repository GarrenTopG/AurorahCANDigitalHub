package com.aurorah.can.ui.getinvolved.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.ui.donation.DonationUiState
import com.aurorah.can.ui.theme.AppCardSurface

/**
 * Renders a confirmation banner displayed upon successful submission of a donation pledge.
 *
 * Provides feedback regarding the pending administrative review, transaction reference,
 * sample Section 18A tax certificate download link (if requested), and an option to reset
 * the form for additional pledges.
 *
 * @param uiState Current state of the donation workflow containing response data and user selections.
 * @param onReset Callback invoked when the user chooses to record another pledge.
 * @param onDownloadCertificate Callback triggered to view or download the Section 18A tax certificate.
 * @param modifier Optional [Modifier] for custom layout constraints.
 */
@Composable
fun DonationSuccessBanner(
    uiState: DonationUiState,
    onReset: () -> Unit,
    onDownloadCertificate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFECFDF5),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF10B981), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = "Your pledge has been recorded",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF065F46)
            )

            val targetNeedTitle = uiState.selectedCommunityNeed?.title
            val confirmationText = if (!targetNeedTitle.isNullOrBlank()) {
                "Your pledge for $targetNeedTitle is pending admin review. No payment was processed, and it will be added to the public total only if approved."
            } else {
                "No payment was taken. This confirmation records your pledge or offer only; it is not a payment confirmation or a tax receipt."
            }

            Text(
                text = confirmationText,
                fontSize = 13.sp,
                color = Color(0xFF047857),
                lineHeight = 19.sp
            )

            val txRef = uiState.donationResponse?.transactionRef ?: ""
            if (txRef.isNotBlank()) {
                Text(
                    text = stringResource(R.string.pledge_reference_format, txRef),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46),
                    fontSize = 14.sp
                )
            }

            if (uiState.requestTaxCertificate) {
                Surface(
                    color = AppCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Your sample Section 18A tax certificate is ready for download.",
                            fontSize = 12.sp,
                            color = Color(0xFF065F46),
                            fontWeight = FontWeight.SemiBold
                        )
                        OutlinedButton(
                            onClick = onDownloadCertificate,
                            border = BorderStroke(1.dp, Color(0xFF059669)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF059669)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 6.dp).size(16.dp)
                            )
                            Text("Download tax certificate (PDF)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF065F46)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Make another pledge", fontWeight = FontWeight.Bold)
            }
        }
    }
}
