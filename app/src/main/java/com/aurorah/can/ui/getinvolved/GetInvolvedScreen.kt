package com.aurorah.can.ui.getinvolved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.aurorah.can.data.api.ApiRepository
import com.aurorah.can.ui.theme.BrandPrimary
import com.aurorah.can.ui.theme.TextMuted
import com.aurorah.can.ui.volunteer.VolunteerScreen

@Composable
fun GetInvolvedScreen(
    repository: ApiRepository,
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

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = BrandPrimary
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
                            color = if (isSelected) BrandPrimary else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> CommunityNeedsSubTab(repository = repository, userEmail = userEmail)
            1 -> DonateSubTab(repository = repository, userEmail = userEmail)
            2 -> VolunteerScreen(
                repository = repository,
                userEmail = userEmail,
                userName = userName,
                userPhone = userPhone
            )
        }
    }
}
