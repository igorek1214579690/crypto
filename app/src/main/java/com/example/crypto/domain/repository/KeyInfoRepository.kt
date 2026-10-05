package com.example.crypto.domain.repository

import com.example.crypto.domain.model.KeyInfo

interface KeyInfoRepository {
    suspend fun getKeyInfo(): Result<KeyInfo>
}