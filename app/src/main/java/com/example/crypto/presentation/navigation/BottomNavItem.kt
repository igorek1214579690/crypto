package com.example.crypto.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
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
    data object CoinList : BottomNavItem("coin_list", R.string.nav_coins, Icons.Filled.List)
    data object Market : BottomNavItem("market", R.string.nav_market, Icons.Filled.PieChart)
    data object Convert : BottomNavItem("convert", R.string.nav_convert, Icons.Filled.CurrencyExchange)
    data object KeyInfo : BottomNavItem("key_info", R.string.nav_key_info, Icons.Filled.Info)
}