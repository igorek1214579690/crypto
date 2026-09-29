package com.example.crypto.presentation.coinlist

sealed interface CoinListIntent {
    data object LoadCoins : CoinListIntent
    data object Refresh : CoinListIntent
}