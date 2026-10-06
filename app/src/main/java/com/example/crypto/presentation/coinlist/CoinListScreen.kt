package com.example.crypto.presentation.coinlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crypto.R
import com.example.crypto.domain.model.Coin

@Composable
fun CoinListScreen(
    viewModel: CoinListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.error != null -> {
                Text(
                    text = stringResource(R.string.error_message, state.error.orEmpty()),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.coins) { coin ->
                        CoinItem(coin)
                    }
                }
            }
        }
    }
}

@Composable
fun CoinItem(coin: Coin) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = stringResource(R.string.coin_name_symbol, coin.name, coin.symbol))
        Text(text = stringResource(R.string.coin_price_usd, coin.price.toString()))
        Text(
            text = stringResource(R.string.coin_percent_change, coin.percentChange24h.toString()),
            color = if (coin.percentChange24h >= 0) Color.Green else Color.Red
        )
    }
}