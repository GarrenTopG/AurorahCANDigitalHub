package com.aurorah.can.ui.volunteer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.ui.components.PrivacyDialog
import com.aurorah.can.ui.components.RoleCard
import com.aurorah.can.ui.theme.AppBackground
import com.aurorah.can.ui.theme.AppBrandPale
import com.aurorah.can.ui.theme.AppTextInk
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.ui.theme.AurorahTheme
import com.aurorah.can.ui.theme.BrandPrimary

@Composable
fun VolunteerScreen(
    viewModel: VolunteerViewModel,
    userEmail: String,
    userName: String = "",
    userPhone: String = "",
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(userEmail, userName, userPhone) {
        viewModel.initUserInfo(userEmail, userName, userPhone)
    }

    val uiState by viewModel.uiState.collectAsState()
    var showPrivacyDialog by remember { mutableStateOf(false) }

    AurorahTheme {
        if (showPrivacyDialog) {
            PrivacyDialog(onDismiss = { showPrivacyDialog = false })
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(AppBackground)
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = stringResource(R.string.back_to_home),
                        color = MaterialTheme.colorScheme.primary,
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
                            text = stringResource(R.string.volunteer_header_tag),
                            color = Color(0xFFFCE7F3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = stringResource(R.string.volunteer_header_title),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.volunteer_header_subtitle),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            if (uiState.isSuccess) {
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
                            text = stringResource(R.string.volunteer_success_title, uiState.firstName.ifBlank { "Volunteer" }),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = stringResource(R.string.volunteer_success_body),
                            fontSize = 14.sp,
                            color = Color(0xFF047857),
                            lineHeight = 20.sp
                        )
                        Button(
                            onClick = { viewModel.resetSuccess() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.submit_another_application), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Intro Callout Notice
            Surface(
                color = AppBrandPale,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = stringResource(R.string.volunteer_callout_text),
                        fontSize = 13.sp,
                        color = AppTextInk,
                        lineHeight = 19.sp
                    )
                }
            }

            // Example Roles Section
            Text(stringResource(R.string.volunteer_opportunities_title), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTextInk)
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
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Onboarding and safeguarding:",
                            fontWeight = FontWeight.Bold,
                            color = AppTextInk,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "Applications are reviewed before placement. Candidates may be invited to an interview and orientation. Roles involving direct contact with children require the applicable child-protection checks, including Child Protection Register vetting under the Children’s Act. Additional background checks may apply to a role.",
                        fontSize = 13.sp,
                        color = AppTextMuted,
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
                    color = AppTextMuted,
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { showPrivacyDialog = true },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        stringResource(R.string.privacy_popia_link),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (!uiState.isSuccess) {
                VolunteerForm(
                    uiState = uiState,
                    onFirstNameChange = viewModel::onFirstNameChange,
                    onLastNameChange = viewModel::onLastNameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onCountryCodeChange = viewModel::onCountryCodeChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onLocationChange = viewModel::onLocationChange,
                    onSkillsChange = viewModel::onSkillsChange,
                    onToggleDay = viewModel::toggleDay,
                    onPopiaConsentChange = viewModel::onPopiaConsentChange,
                    onSubmit = viewModel::submitApplication
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
