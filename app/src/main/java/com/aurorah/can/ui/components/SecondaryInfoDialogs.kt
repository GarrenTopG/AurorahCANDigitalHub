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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.TextInk
import com.aurorah.can.ui.theme.TextMuted

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Close")
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
                    tint = BrandPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "About Aurorah CAN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextInk
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
                    text = "Aurorah Community Action Network (CAN) is a non-profit company (NPC) based in Kuils River, Cape Town.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextInk
                )
                Text(
                    text = "We focus on practical, community-led initiatives to support vulnerable households, young people, and local residents. Key programs include:",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextInk
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("• Sunbird Park Mini-Library & Youth Literacy", fontSize = 13.sp, color = TextMuted)
                    Text("• Community Food Parcels & Relief Logistics", fontSize = 13.sp, color = TextMuted)
                    Text("• Local Skill Sharing & Volunteer Coordination", fontSize = 13.sp, color = TextMuted)
                }
                Text(
                    text = "Our work is built on transparency, community trust, and mutual assistance.",
                    fontSize = 13.sp,
                    color = TextInk,
                    fontWeight = FontWeight.Medium
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
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Close")
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
                    tint = BrandPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Contact Aurorah CAN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextInk
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
                    detail = "27 Lovebird Walk, Sunbird Park, Cape Town, 7580"
                )
                ContactDetailItem(
                    icon = Icons.Filled.AccessTime,
                    title = "Operating Hours",
                    detail = "Mon–Fri: 08:00 – 17:00\nSat: 09:00 – 14:00"
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
            tint = BrandPrimary,
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextInk)
            Text(detail, fontSize = 13.sp, color = TextMuted, lineHeight = 18.sp)
        }
    }
}

@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Understood")
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
                    tint = BrandPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Privacy & POPIA Notice",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextInk
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
                    color = TextInk
                )
                Text(
                    text = "How your details are used:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextInk
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("• Assistance Requests: Evaluated by team members to assess community needs and coordinate support.", fontSize = 13.sp, color = TextMuted)
                    Text("• Volunteer Applications: Processed for background suitability, skill matching, and role placement.", fontSize = 13.sp, color = TextMuted)
                    Text("• Donations: Pledges are recorded securely for organization records.", fontSize = 13.sp, color = TextMuted)
                }
                Text(
                    text = "Your data is never sold to third parties. If required details are missing, we may be unable to process your request.",
                    fontSize = 12.sp,
                    color = TextInk,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
