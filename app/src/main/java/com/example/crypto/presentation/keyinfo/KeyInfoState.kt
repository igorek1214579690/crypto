package com.example.crypto.presentation.keyinfo

import com.example.crypto.domain.model.KeyInfo

data class KeyInfoState(
    val keyInfo: KeyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)