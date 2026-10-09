package com.example.crypto

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.crypto.presentation.coinlist.CoinDetailScreen
import com.example.crypto.presentation.coinlist.CoinListScreen
import com.example.crypto.presentation.keyinfo.KeyInfoScreen
import com.example.crypto.presentation.navigation.BottomNavItem
import com.example.crypto.ui.theme.CryptoTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import androidx.compose.ui.res.stringResource
@HiltAndroidApp
class MyApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(BottomNavItem.CoinList, BottomNavItem.KeyInfo)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute == BottomNavItem.CoinList.route ||
            currentRoute == BottomNavItem.KeyInfo.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                            label = { Text(stringResource(item.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.CoinList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.CoinList.route) {
                CoinListScreen(onCoinClick = { id ->
                    navController.navigate("coin_detail/$id")
                })
            }
            composable(BottomNavItem.KeyInfo.route) {
                KeyInfoScreen()
            }
            composable(
                route = "coin_detail/{coinId}",
                arguments = listOf(navArgument("coinId") { type = NavType.IntType })
            ) { backStackEntry ->
                val coinId = backStackEntry.arguments?.getInt("coinId") ?: return@composable
                CoinDetailScreen(
                    coinId = coinId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}