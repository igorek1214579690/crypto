package com.example.crypto.presentation.coinlist

import com.example.crypto.domain.model.Coin

data class CoinListState(
    val coins: List<Coin> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)