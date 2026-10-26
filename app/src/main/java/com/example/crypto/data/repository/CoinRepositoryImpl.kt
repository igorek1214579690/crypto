package com.example.crypto.data.repository

import com.example.crypto.data.local.dao.CoinDao
import com.example.crypto.data.mapper.toDomain
import com.example.crypto.data.mapper.toEntity
import com.example.crypto.data.remote.api.CoinMarketCapApi
import com.example.crypto.domain.model.Coin
import com.example.crypto.domain.model.CoinListResult
import com.example.crypto.domain.repository.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi,
    private val coinDao: CoinDao
) : CoinRepository {

    @Volatile
    private var cachedCoins: List<Coin>? = null

    override suspend fun getCoinList(): Result<CoinListResult> {
        return try {
            val response = api.getLatestListings()
            val coins = response.data.map { it.toDomain() }
            val now = System.currentTimeMillis()

            cachedCoins = coins
            coinDao.clear()
            coinDao.insertAll(coins.map { it.toEntity(now) })

            Result.success(CoinListResult(coins = coins, isFromCache = false, updatedAt = now))
        } catch (e: Exception) {
            val local = coinDao.getAll().map { it.toDomain() }

            if (local.isNotEmpty()) {
                val timestamp = coinDao.getCacheTimestamp().takeIf { it > 0 }
                cachedCoins = local
                Result.success(CoinListResult(coins = local, isFromCache = true, updatedAt = timestamp))
            } else {
                Result.failure(e)
            }
        }
    }

    override fun getCachedCoins(): List<Coin>? = cachedCoins
}