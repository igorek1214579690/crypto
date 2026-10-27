package com.example.crypto.presentation.keyinfo

import com.example.crypto.domain.model.KeyInfo
import com.example.crypto.presentation.common.ErrorKind

data class KeyInfoState(
    val keyInfo: KeyInfo? = null,
    val isLoading: Boolean = false,
    val error: ErrorKind? = null
)