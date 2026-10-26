package com.example.crypto.data.mapper

import com.example.crypto.data.local.entity.MarketEntity
import com.example.crypto.domain.model.FearGreed
import com.example.crypto.domain.model.MarketOverview
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

fun MarketOverview.toEntity(moshi: Moshi, cachedAt: Long): MarketEntity {
    val listType = Types.newParameterizedType(List::class.java, FearGreed::class.java)
    val historyJson = moshi.adapter<List<FearGreed>>(listType).toJson(fearGreedHistory)
    return MarketEntity(
        totalMarketCap = totalMarketCap,
        totalMarketCapChange = totalMarketCapChange,
        totalVolume24h = totalVolume24h,
        totalVolumeChange = totalVolumeChange,
        btcDominance = btcDominance,
        ethDominance = ethDominance,
        activeCryptocurrencies = activeCryptocurrencies,
        activeExchanges = activeExchanges,
        fearGreedValue = fearGreed?.value,
        fearGreedClassification = fearGreed?.classification,
        fearGreedHistoryJson = historyJson,
        altcoinSeasonIndex = altcoinSeasonIndex,
        cachedAt = cachedAt
    )
}

fun MarketEntity.toDomain(moshi: Moshi): MarketOverview {
    val listType = Types.newParameterizedType(List::class.java, FearGreed::class.java)
    val history = fearGreedHistoryJson?.let {
        runCatching { moshi.adapter<List<FearGreed>>(listType).fromJson(it) }.getOrNull()
    } ?: emptyList()
    return MarketOverview(
        totalMarketCap = totalMarketCap,
        totalMarketCapChange = totalMarketCapChange,
        totalVolume24h = totalVolume24h,
        totalVolumeChange = totalVolumeChange,
        btcDominance = btcDominance,
        ethDominance = ethDominance,
        activeCryptocurrencies = activeCryptocurrencies,
        activeExchanges = activeExchanges,
        fearGreed = if (fearGreedValue != null && fearGreedClassification != null) {
            FearGreed(value = fearGreedValue, classification = fearGreedClassification)
        } else null,
        fearGreedHistory = history,
        altcoinSeasonIndex = altcoinSeasonIndex
    )
}