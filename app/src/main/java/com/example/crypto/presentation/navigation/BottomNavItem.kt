package com.example.crypto.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.crypto.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    data object CoinList : BottomNavItem(
        route = "coin_list",
        labelRes = R.string.nav_coins,
        icon = Icons.Filled.List
    )

    data object Market : BottomNavItem(
        route = "market",
        labelRes = R.string.nav_market,
        icon = Icons.Filled.PieChart
    )

    data object KeyInfo : BottomNavItem(
        route = "key_info",
        labelRes = R.string.nav_key_info,
        icon = Icons.Filled.Info
    )
}