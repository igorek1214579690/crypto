package com.example.crypto.data.mapper

import com.example.crypto.data.remote.dto.KeyInfoResponseDto
import com.example.crypto.domain.model.KeyInfo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun KeyInfoResponseDto.toDomain(): KeyInfo {
    return KeyInfo(
        creditLimitDaily = data.plan.creditLimitDaily,
        creditLimitMonthly = data.plan.creditLimitMonthly,
        creditLimitMonthlyReset = data.plan.creditLimitMonthlyReset,
        creditLimitMonthlyResetAt = parseIsoMillis(data.plan.creditLimitMonthlyResetTimestamp),
        rateLimitMinute = data.plan.rateLimitMinute,
        creditsUsedToday = data.usage.currentDay?.creditsUsed,
        creditsLeftToday = data.usage.currentDay?.creditsLeft,
        creditsUsedMonth = data.usage.currentMonth?.creditsUsed,
        creditsLeftMonth = data.usage.currentMonth?.creditsLeft
    )
}

/**
 * Parses CoinMarketCap ISO 8601 timestamps like "2026-11-01T00:00:00.000Z".
 * SimpleDateFormat is used instead of java.time because minSdk is 24.
 */
private fun parseIsoMillis(value: String?): Long? {
    if (value.isNullOrBlank()) return null
    val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ssX")
    for (pattern in patterns) {
        val format = SimpleDateFormat(pattern, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
        try {
            return format.parse(value)?.time
        } catch (_: ParseException) {
            // try the next pattern
        }
    }
    return null
}
