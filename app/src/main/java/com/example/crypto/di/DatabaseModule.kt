package com.example.crypto.di

import android.content.Context
import androidx.room.Room
import com.example.crypto.data.local.CryptoDatabase
import com.example.crypto.data.local.dao.CoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.crypto.data.local.dao.MarketDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CryptoDatabase {
        return Room.databaseBuilder(
            context,
            CryptoDatabase::class.java,
            "crypto.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    @Provides
    fun provideMarketDao(database: CryptoDatabase): MarketDao = database.marketDao()

    @Provides
    fun provideCoinDao(database: CryptoDatabase): CoinDao = database.coinDao()
}
