package com.example.crypto.domain.model

data class MarketOverview(
    val totalMarketCap: Double?,
    val totalMarketCapChange: Double?,
    val totalVolume24h: Double?,
    val totalVolumeChange: Double?,
    val btcDominance: Double?,
    val ethDominance: Double?,
    val activeCryptocurrencies: Int?,
    val activeExchanges: Int?,
    val fearGreed: FearGreed?,
    val fearGreedHistory: List<FearGreed>,
    val altcoinSeasonIndex: Int?
)

data class FearGreed(
    val value: Int,
    val classification: String
)