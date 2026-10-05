package com.example.crypto.data.mapper

import com.example.crypto.data.remote.dto.KeyInfoResponseDto
import com.example.crypto.domain.model.KeyInfo

fun KeyInfoResponseDto.toDomain(): KeyInfo {
    return KeyInfo(
        creditLimitDaily = data.plan.creditLimitDaily,
        creditLimitMonthly = data.plan.creditLimitMonthly,
        creditsUsedToday = data.usage.currentDay?.creditsUsed,
        creditsLeftToday = data.usage.currentDay?.creditsLeft,
        creditsUsedMonth = data.usage.currentMonth?.creditsUsed,
        creditsLeftMonth = data.usage.currentMonth?.creditsLeft
    )
}