package com.example.crypto.presentation.convert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto.domain.model.Coin
import com.example.crypto.domain.usecase.GetConversionRatesUseCase
import com.example.crypto.domain.usecase.GetConvertibleCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.crypto.presentation.common.classifyError

private const val REFRESH_COOLDOWN_MS = 30_000L
private val AMOUNT_REGEX = Regex("^[0-9]*[.,]?[0-9]*$")

@HiltViewModel
class ConvertViewModel @Inject constructor(
    private val getConvertibleCoinsUseCase: GetConvertibleCoinsUseCase,
    private val getConversionRatesUseCase: GetConversionRatesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ConvertState())
    val state: StateFlow<ConvertState> = _state.asStateFlow()

    private var ratesJob: Job? = null
    private var lastRefreshAt = 0L

    init {
        onIntent(ConvertIntent.LoadCoins)
    }

    fun onIntent(intent: ConvertIntent) {
        when (intent) {
            is ConvertIntent.LoadCoins -> loadCoins()
            is ConvertIntent.SelectCoin -> selectCoin(intent.coin)
            is ConvertIntent.AmountChanged -> onAmountChanged(intent.text)
            is ConvertIntent.SelectCurrency -> _state.update { it.copy(selectedCurrency = intent.code) }
            is ConvertIntent.Swap -> _state.update {
                it.copy(
                    direction = if (it.direction == ConvertDirection.COIN_TO_CURRENCY)
                        ConvertDirection.CURRENCY_TO_COIN else ConvertDirection.COIN_TO_CURRENCY
                )
            }
            is ConvertIntent.Refresh -> refresh()
            is ConvertIntent.OpenCoinPicker -> _state.update { it.copy(isCoinPickerOpen = true, coinSearchQuery = "") }
            is ConvertIntent.CloseCoinPicker -> _state.update { it.copy(isCoinPickerOpen = false) }
            is ConvertIntent.SearchCoins -> _state.update { it.copy(coinSearchQuery = intent.query) }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCoins = true, coinsError = null) }
            getConvertibleCoinsUseCase()
                .onSuccess { coins ->
                    val firstCoin = coins.firstOrNull()
                    _state.update { it.copy(coins = coins, isLoadingCoins = false, selectedCoin = firstCoin) }
                    firstCoin?.let { loadRates(it.id) }
                }
                .onFailure { e -> _state.update { it.copy(isLoadingRates = false, ratesError = classifyError(e)) } }
        }
    }

    private fun selectCoin(coin: Coin) {
        _state.update { it.copy(selectedCoin = coin, isCoinPickerOpen = false, coinSearchQuery = "") }
        loadRates(coin.id)
    }

    private fun loadRates(coinId: Int) {
        ratesJob?.cancel()
        ratesJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingRates = true, ratesError = null) }
            getConversionRatesUseCase(coinId)
                .onSuccess { rates ->
                    _state.update { it.copy(rates = rates.mapValues { (_, v) -> v.price }, isLoadingRates = false) }
                }
                .onFailure { e -> _state.update { it.copy(isLoadingCoins = false, coinsError = classifyError(e)) } }
        }
    }

    private fun onAmountChanged(text: String) {
        val trimmed = text.take(18)
        val isValid = trimmed.isNotEmpty() &&
                AMOUNT_REGEX.matches(trimmed) &&
                trimmed.replace(',', '.').toDoubleOrNull() != null
        _state.update { it.copy(amountText = trimmed, isAmountValid = isValid) }
    }

    private fun refresh() {
        val now = System.currentTimeMillis()
        if (now - lastRefreshAt < REFRESH_COOLDOWN_MS) return
        lastRefreshAt = now
        _state.value.selectedCoin?.let { loadRates(it.id) }
    }
}