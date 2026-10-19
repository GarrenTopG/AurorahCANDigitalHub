package com.aurorah.can.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurorah.can.ui.theme.TextInk
import com.aurorah.can.ui.theme.TextMuted

@Composable
fun CountryCodeDropdown(
    selectedCode: String,
    onCodeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val countryCodes = listOf(
        "+27" to "🇿🇦 +27",
        "+264" to "🇳🇳 +264",
        "+267" to "🇧🇼 +267",
        "+263" to "🇿🇼 +263",
        "+258" to "🇲🇿 +258",
        "+268" to "🇸🇿 +268",
        "+266" to "🇱🇸 +266"
    )
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = countryCodes.find { it.first == selectedCode }?.second ?: selectedCode

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .clickable { expanded = true }
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF3F4F6),
            border = BorderStroke(1.dp, Color(0xFFD1D5DB))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = displayLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextInk
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Select Country Code",
                    tint = TextMuted
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryCodes.forEach { (code, label) ->
                DropdownMenuItem(
                    text = { Text(label, fontSize = 14.sp) },
                    onClick = {
                        onCodeSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}
