package com.example.crypto.presentation.market

import com.example.crypto.domain.model.MarketOverview
import com.example.crypto.presentation.common.ErrorKind

data class MarketState(
    val overview: MarketOverview? = null,
    val isLoading: Boolean = false,
    val error: ErrorKind? = null,
    val isFromCache: Boolean = false,
    val cacheUpdatedAt: Long? = null
)