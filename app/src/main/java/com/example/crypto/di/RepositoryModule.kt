package com.example.crypto.di

import com.example.crypto.data.repository.CoinRepositoryImpl
import com.example.crypto.data.repository.KeyInfoRepositoryImpl
import com.example.crypto.domain.repository.CoinRepository
import com.example.crypto.domain.repository.KeyInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCoinRepository(
        impl: CoinRepositoryImpl
    ): CoinRepository

    @Binds
    abstract fun bindKeyInfoRepository(
        impl: KeyInfoRepositoryImpl
    ): KeyInfoRepository
}