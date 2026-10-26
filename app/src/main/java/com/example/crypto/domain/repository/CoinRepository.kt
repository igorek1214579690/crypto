package com.example.crypto.domain.repository

import com.example.crypto.domain.model.Coin
import com.example.crypto.domain.model.CoinListResult

interface CoinRepository {
    suspend fun getCoinList(): Result<CoinListResult>
    fun getCachedCoins(): List<Coin>?
}