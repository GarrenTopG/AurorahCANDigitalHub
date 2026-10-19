package com.aurorah.can.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.R
import com.aurorah.can.ui.theme.BrandMid
import com.aurorah.can.ui.theme.BrandPrimary

@Composable
fun HeroCard(
    onRequestAssistance: () -> Unit,
    onExploreNeeds: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(BrandPrimary, BrandMid)),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "AURORAH NPC  ·  KUILS RIVER, CAPE TOWN",
            color = Color(0xFFFCE7F3),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = stringResource(R.string.home_hero_title),
            color = Color.White,
            fontSize = 25.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = stringResource(R.string.home_hero_subtitle),
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        Button(
            onClick = onRequestAssistance,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = BrandPrimary
            )
        ) {
            Text(stringResource(R.string.home_need_assistance), fontWeight = FontWeight.Bold)
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp).size(18.dp)
            )
        }

        OutlinedButton(
            onClick = onExploreNeeds,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text(stringResource(R.string.home_offer_help), fontWeight = FontWeight.Bold)
            Icon(
                Icons.Filled.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp).size(18.dp)
            )
        }
    }
}
