package com.example.crypto.domain.model

data class CoinListResult(
    val coins: List<Coin>,
    val isFromCache: Boolean,
    val updatedAt: Long?
)