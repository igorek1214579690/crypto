package com.example.crypto.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.crypto.data.local.entity.MarketEntity

@Dao
interface MarketDao {
    @Query("SELECT * FROM market_overview WHERE id = 0")
    suspend fun get(): MarketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MarketEntity): Long
}