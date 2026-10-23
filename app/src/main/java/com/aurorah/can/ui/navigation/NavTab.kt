package com.aurorah.can.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LiveHelp
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavTab(
    val title: String,
    val icon: ImageVector
) {
    HOME("Home", Icons.Filled.Home),
    GET_HELP("Get Help", Icons.AutoMirrored.Filled.LiveHelp),
    VOLUNTEER("Volunteer", Icons.Filled.VolunteerActivism),
    DONATE("Donate", Icons.Filled.CardGiftcard),
    PROFILE("Profile", Icons.Filled.Person)
}
