package com.example.crypto.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinListResponseDto(
    @Json(name = "data") val data: List<CoinDto>
)

@JsonClass(generateAdapter = true)
data class CoinDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "symbol") val symbol: String,
    @Json(name = "cmc_rank") val cmcRank: Int? = null,
    @Json(name = "num_market_pairs") val numMarketPairs: Int? = null,
    @Json(name = "circulating_supply") val circulatingSupply: Double? = null,
    @Json(name = "max_supply") val maxSupply: Double? = null,
    // v3: quote is an array, one element per requested convert currency
    @Json(name = "quote") val quote: List<QuoteDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class QuoteDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "symbol") val symbol: String? = null,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "percent_change_1h") val percentChange1h: Double? = null,
    @Json(name = "percent_change_24h") val percentChange24h: Double? = null,
    @Json(name = "percent_change_7d") val percentChange7d: Double? = null,
    @Json(name = "percent_change_30d") val percentChange30d: Double? = null,
    @Json(name = "market_cap") val marketCap: Double? = null,
    @Json(name = "volume_24h") val volume24h: Double? = null,
    @Json(name = "market_cap_dominance") val marketCapDominance: Double? = null
)