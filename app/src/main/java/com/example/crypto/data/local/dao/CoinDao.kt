package com.example.crypto.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.crypto.data.local.entity.CoinEntity

@Dao
interface CoinDao {
    @Query("SELECT * FROM coins ORDER BY cmcRank ASC")
    suspend fun getAll(): List<CoinEntity>

    @Query("SELECT COALESCE(MAX(cachedAt), 0) FROM coins")
    suspend fun getCacheTimestamp(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coins: List<CoinEntity>): List<Long>

    @Query("DELETE FROM coins")
    suspend fun clear(): Int
}