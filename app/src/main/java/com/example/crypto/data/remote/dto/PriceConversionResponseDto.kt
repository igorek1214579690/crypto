package com.example.crypto.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PriceConversionResponseDto(
    @param:Json(name = "status") val status: ConversionStatusDto? = null,
    @param:Json(name = "data") val data: ConversionDataDto? = null
)

@JsonClass(generateAdapter = true)
data class ConversionStatusDto(
    @param:Json(name = "error_code") val errorCode: Int? = null,
    @param:Json(name = "error_message") val errorMessage: String? = null
)

@JsonClass(generateAdapter = true)
data class ConversionDataDto(
    @param:Json(name = "quote") val quote: Map<String, ConversionQuoteDto>? = null
)

@JsonClass(generateAdapter = true)
data class ConversionQuoteDto(
    @param:Json(name = "price") val price: Double? = null,
    @param:Json(name = "last_updated") val lastUpdated: String? = null
)