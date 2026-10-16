package com.example.crypto.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FearGreedLatestResponseDto(
    @param:Json(name = "data") val data: FearGreedDto
)

@JsonClass(generateAdapter = true)
data class FearGreedHistoricalResponseDto(
    @param:Json(name = "data") val data: List<FearGreedDto>
)

@JsonClass(generateAdapter = true)
data class FearGreedDto(
    @param:Json(name = "value") val value: Int? = null,
    @param:Json(name = "value_classification") val valueClassification: String? = null,
    @param:Json(name = "timestamp") val timestamp: String? = null
)