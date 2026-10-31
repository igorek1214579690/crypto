package com.example.crypto.domain.model

data class KeyInfo(
    val creditLimitDaily: Int?,
    val creditLimitMonthly: Int?,
    val creditLimitMonthlyReset: String?,
    /** Monthly reset moment, epoch millis (UTC). Null if the API did not send it. */
    val creditLimitMonthlyResetAt: Long?,
    val rateLimitMinute: Int?,
    val creditsUsedToday: Int?,
    val creditsLeftToday: Int?,
    val creditsUsedMonth: Int?,
    val creditsLeftMonth: Int?
)
