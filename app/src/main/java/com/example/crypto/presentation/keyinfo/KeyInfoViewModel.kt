package com.example.crypto.presentation.keyinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto.domain.usecase.GetKeyInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeyInfoViewModel @Inject constructor(
    private val getKeyInfoUseCase: GetKeyInfoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(KeyInfoState())
    val state: StateFlow<KeyInfoState> = _state.asStateFlow()

    init {
        loadKeyInfo()
    }

    fun loadKeyInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getKeyInfoUseCase()
                .onSuccess { info ->
                    _state.update { it.copy(keyInfo = info, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
}