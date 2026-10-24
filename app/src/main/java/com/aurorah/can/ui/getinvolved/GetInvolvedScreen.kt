package com.aurorah.can.ui.getinvolved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.ui.donation.DonationViewModel
import com.aurorah.can.ui.needs.NeedsViewModel
import com.aurorah.can.ui.theme.AppTextMuted
import com.aurorah.can.ui.theme.AurorahTheme
import com.aurorah.can.ui.volunteer.VolunteerScreen
import com.aurorah.can.ui.volunteer.VolunteerViewModel

@Composable
fun GetInvolvedScreen(
    needsViewModel: NeedsViewModel,
    donationViewModel: DonationViewModel,
    volunteerViewModel: VolunteerViewModel,
    userEmail: String,
    userName: String = "",
    userPhone: String = "",
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTab.coerceIn(0, 2)) }
    val tabs = listOf(
        stringResource(R.string.tab_community_needs),
        stringResource(R.string.tab_donate),
        stringResource(R.string.tab_volunteer)
    )

    AurorahTheme {
        Column(modifier = modifier.fillMaxSize()) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else AppTextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> CommunityNeedsSubTab(viewModel = needsViewModel)
                1 -> DonateSubTab(viewModel = donationViewModel)
                2 -> VolunteerScreen(
                    viewModel = volunteerViewModel,
                    userEmail = userEmail,
                    userName = userName,
                    userPhone = userPhone
                )
            }
        }
    }
}
