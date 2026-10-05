package com.example.crypto.domain.repository

import com.example.crypto.domain.model.Coin

interface CoinRepository {
    suspend fun getCoinList(): Result<List<Coin>>
}