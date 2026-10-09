package com.example.crypto.domain.model

data class Coin(
    val id: Int,
    val cmcRank: Int?,
    val name: String,
    val symbol: String,
    val price: Double,
    val percentChange1h: Double?,
    val percentChange24h: Double,
    val percentChange7d: Double?,
    val percentChange30d: Double?,
    val marketCap: Double?,
    val volume24h: Double?,
    val marketCapDominance: Double?,
    val numMarketPairs: Int?,
    val circulatingSupply: Double?,
    val maxSupply: Double?
)