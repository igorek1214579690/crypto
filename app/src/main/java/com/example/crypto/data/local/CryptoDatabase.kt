package com.example.crypto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.crypto.data.local.dao.CoinDao
import com.example.crypto.data.local.dao.MarketDao
import com.example.crypto.data.local.entity.CoinEntity
import com.example.crypto.data.local.entity.MarketEntity

@Database(
    entities = [CoinEntity::class, MarketEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun marketDao(): MarketDao
}