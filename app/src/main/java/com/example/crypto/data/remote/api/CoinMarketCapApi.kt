package com.example.crypto.data.remote.api



import com.example.crypto.data.remote.dto.CoinListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinMarketCapApi {
    @GET("v1/cryptocurrency/listings/latest")
    suspend fun getLatestListings(
        @Query("start") start: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("convert") convert: String = "USD"
    ): CoinListResponseDto
}