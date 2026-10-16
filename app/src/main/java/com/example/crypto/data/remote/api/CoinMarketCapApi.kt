package com.example.crypto.data.remote.api

import com.example.crypto.data.remote.dto.AltcoinSeasonResponseDto
import com.example.crypto.data.remote.dto.CoinListResponseDto
import com.example.crypto.data.remote.dto.FearGreedHistoricalResponseDto
import com.example.crypto.data.remote.dto.FearGreedLatestResponseDto
import com.example.crypto.data.remote.dto.GlobalMetricsResponseDto
import com.example.crypto.data.remote.dto.KeyInfoResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinMarketCapApi {
    @GET("v3/cryptocurrency/listings/latest")
    suspend fun getLatestListings(
        @Query("start") start: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("convert") convert: String = "USD"
    ): CoinListResponseDto

    @GET("v1/key/info")
    suspend fun getKeyInfo(): KeyInfoResponseDto

    @GET("v1/global-metrics/quotes/latest")
    suspend fun getGlobalMetrics(
        @Query("convert") convert: String = "USD"
    ): GlobalMetricsResponseDto

    @GET("v3/fear-and-greed/latest")
    suspend fun getFearGreedLatest(): FearGreedLatestResponseDto

    @GET("v3/fear-and-greed/historical")
    suspend fun getFearGreedHistorical(
        @Query("limit") limit: Int = 30
    ): FearGreedHistoricalResponseDto

    @GET("v1/altcoin-season-index/latest")
    suspend fun getAltcoinSeasonIndex(): AltcoinSeasonResponseDto
}