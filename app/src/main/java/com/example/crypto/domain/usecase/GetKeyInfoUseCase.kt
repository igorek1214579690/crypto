package com.example.crypto.domain.usecase

import com.example.crypto.domain.model.KeyInfo
import com.example.crypto.domain.repository.KeyInfoRepository
import javax.inject.Inject

class GetKeyInfoUseCase @Inject constructor(
    private val repository: KeyInfoRepository
) {
    suspend operator fun invoke(): Result<KeyInfo> {
        return repository.getKeyInfo()
    }
}