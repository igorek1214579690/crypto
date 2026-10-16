package com.example.crypto.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AltcoinSeasonResponseDto(
    @param:Json(name = "data") val data: AltcoinSeasonDataDto
)

@JsonClass(generateAdapter = true)
data class AltcoinSeasonDataDto(
    @param:Json(name = "altcoin_index") val altcoinIndex: Int? = null
)