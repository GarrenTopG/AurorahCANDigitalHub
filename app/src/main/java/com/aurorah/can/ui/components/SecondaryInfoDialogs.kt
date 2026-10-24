package com.aurorah.can.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.ui.theme.AppCardSurface
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.ui.theme.BrandPrimary

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppCardSurface,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text(stringResource(R.string.btn_close))
            }
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.about_aurorah_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AppTextInk
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.about_story),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = AppTextInk
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ContactDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppCardSurface,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text(stringResource(R.string.btn_close))
            }
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.find_us_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AppTextInk
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ContactDetailItem(
                    icon = Icons.Filled.LocationOn,
                    title = "Address",
                    detail = stringResource(R.string.address_sunbird_park)
                )
                ContactDetailItem(
                    icon = Icons.Filled.AccessTime,
                    title = "Operating Hours",
                    detail = stringResource(R.string.hours_sunbird_park)
                )
                ContactDetailItem(
                    icon = Icons.Filled.Phone,
                    title = "Phone Contact",
                    detail = "+27 82 000 0000"
                )
                ContactDetailItem(
                    icon = Icons.Filled.Email,
                    title = "Email Enquiries",
                    detail = "info@aurorahcan.org"
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun ContactDetailItem(
    icon: ImageVector,
    title: String,
    detail: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppTextInk)
            Text(detail, fontSize = 13.sp, color = AppTextMuted, lineHeight = 18.sp)
        }
    }
}

@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = stringResource(R.string.privacy_policy_url)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppCardSurface,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text(stringResource(R.string.btn_understood))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    try {
                        uriHandler.openUri(privacyPolicyUrl)
                    } catch (_: Exception) {}
                }
            ) {
                Text(stringResource(R.string.btn_view_full_policy), color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            }
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.privacy_policy_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AppTextInk
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Aurorah NPC respects your personal information and complies with the Protection of Personal Information Act (POPIA).",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = AppTextInk
                )
                Text(
                    text = "How your details are used:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AppTextInk
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("• Assistance Requests: Evaluated by team members to assess community needs and coordinate support.", fontSize = 13.sp, color = AppTextMuted)
                    Text("• Volunteer Applications: Processed for background suitability, skill matching, and role placement.", fontSize = 13.sp, color = AppTextMuted)
                    Text("• Donations: Pledges are recorded securely for organization records.", fontSize = 13.sp, color = AppTextMuted)
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
