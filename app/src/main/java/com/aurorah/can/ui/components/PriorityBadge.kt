package com.aurorah.can.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriorityBadge(
    priority: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (priority.lowercase()) {
        "critical" -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        "high" -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
        "medium" -> Color(0xFFFEF9C3) to Color(0xFF854D0E)
        else -> Color(0xFFDCFCE7) to Color(0xFF166534)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Text(
            text = priority.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
