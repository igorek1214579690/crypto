package com.example.crypto.domain.usecase

import com.example.crypto.domain.model.ConversionRate
import com.example.crypto.domain.repository.ConversionRepository
import javax.inject.Inject

class GetConversionRatesUseCase @Inject constructor(
    private val repository: ConversionRepository
) {
    suspend operator fun invoke(coinId: Int): Result<Map<String, ConversionRate>> =
        repository.getRates(coinId)
}