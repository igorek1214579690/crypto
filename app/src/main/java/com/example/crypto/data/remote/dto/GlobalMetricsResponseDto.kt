package com.example.crypto.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GlobalMetricsResponseDto(
    @param:Json(name = "data") val data: GlobalMetricsDataDto
)

@JsonClass(generateAdapter = true)
data class GlobalMetricsDataDto(
    @param:Json(name = "active_cryptocurrencies") val activeCryptocurrencies: Int? = null,
    @param:Json(name = "active_exchanges") val activeExchanges: Int? = null,
    @param:Json(name = "btc_dominance") val btcDominance: Double? = null,
    @param:Json(name = "eth_dominance") val ethDominance: Double? = null,
    @param:Json(name = "quote") val quote: Map<String, GlobalQuoteDto>? = null
)

@JsonClass(generateAdapter = true)
data class GlobalQuoteDto(
    @param:Json(name = "total_market_cap") val totalMarketCap: Double? = null,
    @param:Json(name = "total_volume_24h") val totalVolume24h: Double? = null,
    @param:Json(name = "total_market_cap_yesterday_percentage_change")
    val totalMarketCapChange: Double? = null,
    @param:Json(name = "total_volume_24h_yesterday_percentage_change")
    val totalVolumeChange: Double? = null
)