package com.example.crypto.data.mapper

import com.example.crypto.data.remote.dto.AltcoinSeasonResponseDto
import com.example.crypto.data.remote.dto.FearGreedDto
import com.example.crypto.data.remote.dto.FearGreedHistoricalResponseDto
import com.example.crypto.data.remote.dto.FearGreedLatestResponseDto
import com.example.crypto.data.remote.dto.GlobalMetricsResponseDto
import com.example.crypto.domain.model.FearGreed
import com.example.crypto.domain.model.MarketOverview

private fun FearGreedDto.toDomainOrNull(): FearGreed? {
    val v = value ?: return null
    val c = valueClassification ?: return null
    return FearGreed(value = v, classification = c)
}

fun buildMarketOverview(
    globalMetrics: GlobalMetricsResponseDto,
    fearGreedLatest: FearGreedLatestResponseDto?,
    fearGreedHistorical: FearGreedHistoricalResponseDto?,
    altcoinSeason: AltcoinSeasonResponseDto?,
    currency: String = "USD"
): MarketOverview {
    val quote = globalMetrics.data.quote?.get(currency)
    return MarketOverview(
        totalMarketCap = quote?.totalMarketCap,
        totalMarketCapChange = quote?.totalMarketCapChange,
        totalVolume24h = quote?.totalVolume24h,
        totalVolumeChange = quote?.totalVolumeChange,
        btcDominance = globalMetrics.data.btcDominance,
        ethDominance = globalMetrics.data.ethDominance,
        activeCryptocurrencies = globalMetrics.data.activeCryptocurrencies,
        activeExchanges = globalMetrics.data.activeExchanges,
        fearGreed = fearGreedLatest?.data?.toDomainOrNull(),
        fearGreedHistory = fearGreedHistorical?.data
            ?.sortedBy { it.timestamp }
            ?.mapNotNull { it.toDomainOrNull() }
            ?: emptyList(),
        altcoinSeasonIndex = altcoinSeason?.data?.altcoinIndex
    )
}