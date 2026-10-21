package com.example.crypto.presentation.convert

import com.example.crypto.domain.model.Coin

sealed interface ConvertIntent {
    data object LoadCoins : ConvertIntent
    data class SelectCoin(val coin: Coin) : ConvertIntent
    data class AmountChanged(val text: String) : ConvertIntent
    data class SelectCurrency(val code: String) : ConvertIntent
    data object Swap : ConvertIntent
    data object Refresh : ConvertIntent
    data object OpenCoinPicker : ConvertIntent
    data object CloseCoinPicker : ConvertIntent
    data class SearchCoins(val query: String) : ConvertIntent
}