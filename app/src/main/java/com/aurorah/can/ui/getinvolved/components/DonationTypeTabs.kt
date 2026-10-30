package com.aurorah.can.ui.getinvolved.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.theme.AppTextMuted

/**
 * Tab control for switching between monetary donation pledges and physical in-kind offers.
 *
 * @param isMonetaryTab Flag indicating whether the Monetary tab is currently active (`true`) or In-Kind (`false`).
 * @param onTabSelected Callback receiving `true` when Monetary tab is selected, or `false` for In-Kind.
 * @param modifier Optional [Modifier] for custom layout behavior.
 */
@Composable
fun DonationTypeTabs(
    isMonetaryTab: Boolean,
    onTabSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = if (isMonetaryTab) 0 else 1,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[if (isMonetaryTab) 0 else 1]),
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Tab(
            selected = isMonetaryTab,
            onClick = { onTabSelected(true) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Monetary Donation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            selectedContentColor = MaterialTheme.colorScheme.primary,
            unselectedContentColor = AppTextMuted
        )
        Tab(
            selected = !isMonetaryTab,
            onClick = { onTabSelected(false) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.Inventory, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("In-Kind Goods", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            selectedContentColor = MaterialTheme.colorScheme.primary,
            unselectedContentColor = AppTextMuted
        )
    }
}
