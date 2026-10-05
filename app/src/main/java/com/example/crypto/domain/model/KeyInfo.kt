package com.example.crypto.domain.model

data class KeyInfo(
    val creditLimitDaily: Int?,
    val creditLimitMonthly: Int?,
    val creditsUsedToday: Int?,
    val creditsLeftToday: Int?,
    val creditsUsedMonth: Int?,
    val creditsLeftMonth: Int?
)