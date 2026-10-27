package com.example.crypto.presentation.coinlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto.domain.usecase.GetCoinListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.crypto.presentation.common.classifyError

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val getCoinListUseCase: GetCoinListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state: StateFlow<CoinListState> = _state.asStateFlow()

    init {
        onIntent(CoinListIntent.LoadCoins)
    }

    fun onIntent(intent: CoinListIntent) {
        when (intent) {
            is CoinListIntent.LoadCoins -> loadCoins()
            is CoinListIntent.Refresh -> loadCoins()
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getCoinListUseCase()
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            coins = result.coins,
                            isLoading = false,
                            isFromCache = result.isFromCache,
                            cacheUpdatedAt = result.updatedAt
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = classifyError(e), isLoading = false) }
                }
        }
    }
}