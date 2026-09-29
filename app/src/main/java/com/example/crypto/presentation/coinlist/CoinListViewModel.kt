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
                .onSuccess { coins ->
                    _state.update { it.copy(coins = coins, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
}