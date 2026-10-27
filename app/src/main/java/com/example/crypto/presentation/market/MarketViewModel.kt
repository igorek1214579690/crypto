package com.example.crypto.presentation.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto.domain.usecase.GetMarketOverviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.crypto.presentation.common.classifyError

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val getMarketOverviewUseCase: GetMarketOverviewUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MarketState())
    val state: StateFlow<MarketState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getMarketOverviewUseCase()
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            overview = result.overview,
                            isLoading = false,
                            isFromCache = result.isFromCache,
                            cacheUpdatedAt = result.updatedAt
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(error = classifyError(e), isLoading = false) } }
    }}}

