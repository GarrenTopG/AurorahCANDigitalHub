package com.aurorah.can.ui.gethelp

import android.app.Application
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.ui.components.PrivacyDialog
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.PageBackground
import com.aurorah.can.ui.theme.TextMuted

@Composable
fun GetHelpScreen(
    repository: ApiRepository,
    userEmail: String,
    userName: String = "",
    userPhone: String = "",
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: GetHelpViewModel = run {
        val context = LocalContext.current
        remember { GetHelpViewModel(context.applicationContext as Application, repository) }
    }
) {
    LaunchedEffect(userEmail, userName, userPhone) {
        viewModel.initUserInfo(userEmail, userName, userPhone)
    }

    val uiState by viewModel.uiState.collectAsState()
    var showPrivacyDialog by remember { mutableStateOf(false) }

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
                    text = stringResource(R.string.back_to_home),
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
                    text = stringResource(R.string.request_assistance_title),
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stringResource(R.string.get_help_header_subtitle),
                    color = Color(0xFFFCE7F3),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        if (uiState.isSuccess) {
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
                        text = stringResource(R.string.get_help_success_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46)
                    )
                    Text(
                        text = stringResource(R.string.get_help_success_body, uiState.submittedFullName.ifBlank { "Applicant" }),
                        fontSize = 14.sp,
                        color = Color(0xFF047857),
                        lineHeight = 20.sp
                    )
                    Button(
                        onClick = { viewModel.resetSuccess() },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.submit_another_request), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Privacy & Transparency Notice
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.privacy_notice_prefix),
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = { showPrivacyDialog = true },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(stringResource(R.string.privacy_popia_link), fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        if (!uiState.isSuccess) {
            GetHelpForm(
                uiState = uiState,
                onFirstNameChange = viewModel::onFirstNameChange,
                onLastNameChange = viewModel::onLastNameChange,
                onCountryCodeChange = viewModel::onCountryCodeChange,
                onPhoneChange = viewModel::onPhoneChange,
                onLocationChange = viewModel::onLocationChange,
                onCategoryChange = viewModel::onCategoryChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onUrgencyChange = viewModel::onUrgencyChange,
                onPopiaConsentChange = viewModel::onPopiaConsentChange,
                onSubmit = viewModel::submitRequest
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
