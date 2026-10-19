package com.aurorah.can.ui.getinvolved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.TextInk
import com.aurorah.can.ui.theme.TextMuted
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunityNeedsSubTab(
    repository: ApiRepository,
    userEmail: String,
    modifier: Modifier = Modifier
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
            onSuccess = { msg: String ->
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
        modifier = modifier
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
