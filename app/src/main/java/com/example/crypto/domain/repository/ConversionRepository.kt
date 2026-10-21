package com.example.crypto.domain.repository

import com.example.crypto.domain.model.ConversionRate

interface ConversionRepository {
    suspend fun getRates(coinId: Int): Result<Map<String, ConversionRate>>
}