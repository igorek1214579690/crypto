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
    @Json(name = "quote") val quote: QuoteDto
)

@JsonClass(generateAdapter = true)
data class QuoteDto(
    @Json(name = "USD") val usd: UsdDto
)

@JsonClass(generateAdapter = true)
data class UsdDto(
    @Json(name = "price") val price: Double,
    @Json(name = "percent_change_24h") val percentChange24h: Double
)