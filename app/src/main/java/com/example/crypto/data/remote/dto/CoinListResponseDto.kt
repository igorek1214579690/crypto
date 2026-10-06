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
    // v3: quote is an array, one element per requested convert currency
    @Json(name = "quote") val quote: List<QuoteDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class QuoteDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "symbol") val symbol: String? = null,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "percent_change_24h") val percentChange24h: Double? = null
)
