package com.example.crypto.presentation.coinlist

import com.example.crypto.domain.model.Coin
import com.example.crypto.presentation.common.ErrorKind

data class CoinListState(
    val coins: List<Coin> = emptyList(),
    val isLoading: Boolean = false,
    val error: ErrorKind? = null,
    val isFromCache: Boolean = false,
    val cacheUpdatedAt: Long? = null
)