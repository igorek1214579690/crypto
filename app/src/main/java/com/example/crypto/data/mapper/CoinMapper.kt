package com.example.crypto.data.mapper

import com.example.crypto.data.remote.dto.CoinDto
import com.example.crypto.domain.model.Coin

fun CoinDto.toDomain(currency: String = "USD"): Coin {
    val q = quote.firstOrNull { it.symbol.equals(currency, ignoreCase = true) }
        ?: quote.firstOrNull()
    return Coin(
        id = id,
        cmcRank = cmcRank,
        name = name,
        symbol = symbol,
        price = q?.price ?: 0.0,
        percentChange1h = q?.percentChange1h,
        percentChange24h = q?.percentChange24h ?: 0.0,
        percentChange7d = q?.percentChange7d,
        percentChange30d = q?.percentChange30d,
        marketCap = q?.marketCap,
        volume24h = q?.volume24h,
        marketCapDominance = q?.marketCapDominance,
        numMarketPairs = numMarketPairs,
        circulatingSupply = circulatingSupply,
        maxSupply = maxSupply
    )
}