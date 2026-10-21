package com.example.crypto.data.repository

import com.example.crypto.data.remote.api.CoinMarketCapApi
import com.example.crypto.data.remote.dto.PriceConversionResponseDto
import com.example.crypto.domain.model.ConversionRate
import com.example.crypto.domain.model.ConvertCurrencies
import com.example.crypto.domain.repository.ConversionRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ConversionRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi,
    private val moshi: Moshi
) : ConversionRepository {

    override suspend fun getRates(coinId: Int): Result<Map<String, ConversionRate>> {
        return coroutineScope {
            val deferred = ConvertCurrencies.CODES.map { code ->
                code to async { fetchOne(coinId, code) }
            }
            val results = deferred.associate { (code, d) -> code to d.await() }

            val rates = results.mapNotNull { (code, result) ->
                result.getOrNull()?.let { code to it }
            }.toMap()

            if (rates.isEmpty()) {
                val firstError = results.values.firstNotNullOfOrNull { it.exceptionOrNull()?.message }
                Result.failure(Exception(firstError ?: "Unknown error"))
            } else {
                Result.success(rates)
            }
        }
    }

    private suspend fun fetchOne(coinId: Int, code: String): Result<ConversionRate> {
        return try {
            val response = api.getPriceConversion(id = coinId, convert = code)
            if (response.isSuccessful) {
                val quote = response.body()?.data?.quote?.get(code)
                val price = quote?.price
                if (price != null) {
                    Result.success(ConversionRate(price = price, lastUpdated = quote.lastUpdated))
                } else {
                    Result.failure(Exception("Missing price for $code"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val message = errorBody?.let {
                    runCatching {
                        moshi.adapter(PriceConversionResponseDto::class.java).fromJson(it)
                    }.getOrNull()?.status?.errorMessage
                } ?: "no message"
                Result.failure(Exception("HTTP ${response.code()}: $message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}