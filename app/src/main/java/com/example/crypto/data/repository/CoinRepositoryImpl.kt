package com.example.crypto.data.repository

import com.example.crypto.data.mapper.toDomain
import com.example.crypto.data.remote.api.CoinMarketCapApi
import com.example.crypto.domain.model.Coin
import com.example.crypto.domain.repository.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi
) : CoinRepository {

    override suspend fun getCoinList(): Result<List<Coin>> {
        return try {
            val response = api.getLatestListings()
            val coins = response.data.map { it.toDomain() }
            Result.success(coins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}