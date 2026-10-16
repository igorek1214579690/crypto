package com.example.crypto.data.repository

import com.example.crypto.data.mapper.buildMarketOverview
import com.example.crypto.data.remote.api.CoinMarketCapApi
import com.example.crypto.domain.model.MarketOverview
import com.example.crypto.domain.repository.MarketRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi
) : MarketRepository {

    override suspend fun getMarketOverview(): Result<MarketOverview> {
        return try {
            coroutineScope {
                val globalMetricsDeferred = async { api.getGlobalMetrics() }
                val fearGreedDeferred = async { runCatching { api.getFearGreedLatest() }.getOrNull() }
                val fearGreedHistoryDeferred = async { runCatching { api.getFearGreedHistorical() }.getOrNull() }
                val altcoinSeasonDeferred = async { runCatching { api.getAltcoinSeasonIndex() }.getOrNull() }

                Result.success(
                    buildMarketOverview(
                        globalMetrics = globalMetricsDeferred.await(),
                        fearGreedLatest = fearGreedDeferred.await(),
                        fearGreedHistorical = fearGreedHistoryDeferred.await(),
                        altcoinSeason = altcoinSeasonDeferred.await()
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}