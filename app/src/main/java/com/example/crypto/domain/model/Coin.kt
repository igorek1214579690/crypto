package com.example.crypto.domain.model

data class Coin(
    val id: Int,
    val name: String,
    val symbol: String,
    val price: Double,
    val percentChange24h: Double
)