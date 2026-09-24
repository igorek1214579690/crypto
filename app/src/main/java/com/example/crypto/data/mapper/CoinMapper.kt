package com.example.crypto.data.mapper
import com.example.crypto.domain.model.Coin
import com.example.crypto.data.remote.dto.CoinDto

fun CoinDto.toDomain(): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol,
        price = quote.usd.price,
        percentChange24h = quote.usd.percentChange24h
    )
}