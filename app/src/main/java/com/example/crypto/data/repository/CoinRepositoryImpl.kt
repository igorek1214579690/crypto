package com.example.crypto.data.repository

import com.example.crypto.data.mapper.toDomain
import com.example.crypto.data.remote.api.CoinMarketCapApi
import com.example.crypto.domain.model.Coin
import com.example.crypto.domain.repository.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi
) : CoinRepository {

    @Volatile
    private var cachedCoins: List<Coin>? = null

    override suspend fun getCoinList(): Result<List<Coin>> {
        return try {
            val response = api.getLatestListings()
            val coins = response.data.map { it.toDomain() }
            cachedCoins = coins
            Result.success(coins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedCoins(): List<Coin>? = cachedCoins
}