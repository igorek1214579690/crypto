package com.example.crypto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_overview")
data class MarketEntity(
    @PrimaryKey val id: Int = 0,
    val totalMarketCap: Double?,
    val totalMarketCapChange: Double?,
    val totalVolume24h: Double?,
    val totalVolumeChange: Double?,
    val btcDominance: Double?,
    val ethDominance: Double?,
    val activeCryptocurrencies: Int?,
    val activeExchanges: Int?,
    val fearGreedValue: Int?,
    val fearGreedClassification: String?,
    val fearGreedHistoryJson: String?,
    val altcoinSeasonIndex: Int?,
    val cachedAt: Long
)