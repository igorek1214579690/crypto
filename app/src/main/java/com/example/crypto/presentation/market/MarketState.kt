package com.example.crypto.presentation.market

import com.example.crypto.domain.model.MarketOverview

data class MarketState(
    val overview: MarketOverview? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFromCache: Boolean = false,
    val cacheUpdatedAt: Long? = null
)