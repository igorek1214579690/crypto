package com.example.crypto.data.repository

import com.example.crypto.data.local.dao.MarketDao
import com.example.crypto.data.mapper.buildMarketOverview
import com.example.crypto.data.mapper.toDomain
import com.example.crypto.data.mapper.toEntity
import com.example.crypto.data.remote.api.CoinMarketCapApi
import com.example.crypto.domain.model.MarketOverviewResult
import com.example.crypto.domain.repository.MarketRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi,
    private val marketDao: MarketDao,
    private val moshi: Moshi
) : MarketRepository {

    override suspend fun getMarketOverview(): Result<MarketOverviewResult> {
        return try {
            coroutineScope {
                val globalMetricsDeferred = async { api.getGlobalMetrics() }
                val fearGreedDeferred = async { runCatching { api.getFearGreedLatest() }.getOrNull() }
                val fearGreedHistoryDeferred = async { runCatching { api.getFearGreedHistorical() }.getOrNull() }
                val altcoinSeasonDeferred = async { runCatching { api.getAltcoinSeasonIndex() }.getOrNull() }

                val overview = buildMarketOverview(
                    globalMetrics = globalMetricsDeferred.await(),
                    fearGreedLatest = fearGreedDeferred.await(),
                    fearGreedHistorical = fearGreedHistoryDeferred.await(),
                    altcoinSeason = altcoinSeasonDeferred.await()
                )
                val now = System.currentTimeMillis()
                marketDao.insert(overview.toEntity(moshi, now))
                Result.success(MarketOverviewResult(overview = overview, isFromCache = false, updatedAt = now))
            }
        } catch (e: Exception) {
            val cached = marketDao.get()
            if (cached != null) {
                Result.success(
                    MarketOverviewResult(
                        overview = cached.toDomain(moshi),
                        isFromCache = true,
                        updatedAt = cached.cachedAt
                    )
                )
            } else {
                Result.failure(e)
            }
        }
    }
}