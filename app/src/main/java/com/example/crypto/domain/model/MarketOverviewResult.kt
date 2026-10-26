package com.example.crypto.domain.model

data class MarketOverviewResult(
    val overview: MarketOverview,
    val isFromCache: Boolean,
    val updatedAt: Long?
)