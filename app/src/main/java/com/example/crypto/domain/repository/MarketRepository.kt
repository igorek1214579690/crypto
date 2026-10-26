package com.example.crypto.domain.repository

import com.example.crypto.domain.model.MarketOverviewResult

interface MarketRepository {
    suspend fun getMarketOverview(): Result<MarketOverviewResult>
}