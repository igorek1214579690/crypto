package com.example.crypto.domain.usecase

import com.example.crypto.domain.model.MarketOverviewResult
import com.example.crypto.domain.repository.MarketRepository
import javax.inject.Inject

class GetMarketOverviewUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(): Result<MarketOverviewResult> = repository.getMarketOverview()
}