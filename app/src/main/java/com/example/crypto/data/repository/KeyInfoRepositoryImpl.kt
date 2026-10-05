package com.example.crypto.data.repository

import com.example.crypto.data.mapper.toDomain
import com.example.crypto.data.remote.api.CoinMarketCapApi
import com.example.crypto.domain.model.KeyInfo
import com.example.crypto.domain.repository.KeyInfoRepository
import javax.inject.Inject

class KeyInfoRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi
) : KeyInfoRepository {

    override suspend fun getKeyInfo(): Result<KeyInfo> {
        return try {
            val response = api.getKeyInfo()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}