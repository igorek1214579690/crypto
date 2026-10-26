package com.example.crypto.data.mapper

import com.example.crypto.data.local.entity.CoinEntity
import com.example.crypto.domain.model.Coin

fun Coin.toEntity(cachedAt: Long): CoinEntity = CoinEntity(
    id = id,
    cmcRank = cmcRank,
    name = name,
    symbol = symbol,
    price = price,
    percentChange1h = percentChange1h,
    percentChange24h = percentChange24h,
    percentChange7d = percentChange7d,
    percentChange30d = percentChange30d,
    marketCap = marketCap,
    volume24h = volume24h,
    marketCapDominance = marketCapDominance,
    numMarketPairs = numMarketPairs,
    circulatingSupply = circulatingSupply,
    maxSupply = maxSupply,
    cachedAt = cachedAt
)

fun CoinEntity.toDomain(): Coin = Coin(
    id = id,
    cmcRank = cmcRank,
    name = name,
    symbol = symbol,
    price = price,
    percentChange1h = percentChange1h,
    percentChange24h = percentChange24h,
    percentChange7d = percentChange7d,
    percentChange30d = percentChange30d,
    marketCap = marketCap,
    volume24h = volume24h,
    marketCapDominance = marketCapDominance,
    numMarketPairs = numMarketPairs,
    circulatingSupply = circulatingSupply,
    maxSupply = maxSupply
)