package com.example.crypto.domain.repository

import com.example.crypto.domain.model.MarketOverview

interface MarketRepository {
    suspend fun getMarketOverview(): Result<MarketOverview>
}