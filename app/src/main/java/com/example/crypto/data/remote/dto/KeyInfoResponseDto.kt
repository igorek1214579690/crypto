package com.example.crypto.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KeyInfoResponseDto(
    @param:Json(name = "data") val data: KeyInfoDataDto
)

@JsonClass(generateAdapter = true)
data class KeyInfoDataDto(
    @param:Json(name = "plan") val plan: PlanDto,
    @param:Json(name = "usage") val usage: UsageDto
)

@JsonClass(generateAdapter = true)
data class PlanDto(
    @param:Json(name = "credit_limit_daily") val creditLimitDaily: Int?,
    @param:Json(name = "credit_limit_monthly") val creditLimitMonthly: Int?,
    @param:Json(name = "rate_limit_minute") val rateLimitMinute: Int?
)

@JsonClass(generateAdapter = true)
data class UsageDto(
    @param:Json(name = "current_day") val currentDay: UsagePeriodDto?,
    @param:Json(name = "current_month") val currentMonth: UsagePeriodDto?
)

@JsonClass(generateAdapter = true)
data class UsagePeriodDto(
    @param:Json(name = "credits_used") val creditsUsed: Int?,
    @param:Json(name = "credits_left") val creditsLeft: Int?
)