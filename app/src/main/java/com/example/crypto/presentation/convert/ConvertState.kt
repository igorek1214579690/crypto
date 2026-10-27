package com.example.crypto.presentation.convert

import com.example.crypto.domain.model.Coin
import com.example.crypto.presentation.common.ErrorKind

enum class ConvertDirection { COIN_TO_CURRENCY, CURRENCY_TO_COIN }

data class ConvertState(
    val coins: List<Coin> = emptyList(),
    val isLoadingCoins: Boolean = false,
    val coinsError: ErrorKind? = null,

    val selectedCoin: Coin? = null,
    val amountText: String = "1",
    val isAmountValid: Boolean = true,
    val selectedCurrency: String = "USD",
    val direction: ConvertDirection = ConvertDirection.COIN_TO_CURRENCY,

    val rates: Map<String, Double> = emptyMap(),
    val isLoadingRates: Boolean = false,
    val ratesError: ErrorKind? = null,

    val isCoinPickerOpen: Boolean = false,
    val coinSearchQuery: String = ""
)