package com.example.crypto.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object CoinList : BottomNavItem(
        route = "coin_list",
        label = "Монеты",
        icon = Icons.Filled.List
    )

    data object Favorites : BottomNavItem(
        route = "favorites",
        label = "Избранное",
        icon = Icons.Filled.Favorite
    )
}