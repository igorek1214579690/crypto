package com.example.crypto.presentation.convert

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.crypto.R
import com.example.crypto.domain.model.Coin
import com.example.crypto.domain.model.ConvertCurrencies
import com.example.crypto.presentation.common.ErrorDialog
import com.example.crypto.presentation.common.ErrorFullScreen
import com.example.crypto.presentation.common.formatCryptoAmount
import com.example.crypto.presentation.common.formatCurrencyValue

private fun parseAmount(text: String): Double? = text.replace(',', '.').toDoubleOrNull()

@Composable
fun ConvertScreen(viewModel: ConvertViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val coinsError = state.coinsError

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.convert_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { viewModel.onIntent(ConvertIntent.Refresh) },
                enabled = !state.isLoadingRates
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
            }
        }

        when {
            state.isLoadingCoins -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            coinsError != null && state.coins.isEmpty() -> {
                ErrorFullScreen(
                    kind = coinsError,
                    onRetry = { viewModel.onIntent(ConvertIntent.LoadCoins) }
                )
            }
            else -> {
                ConvertContent(state = state, viewModel = viewModel)
            }
        }
    }

    if (state.isCoinPickerOpen) {
        CoinPickerSheet(state = state, viewModel = viewModel)
    }
}

@Composable
private fun ConvertContent(state: ConvertState, viewModel: ConvertViewModel) {
    val coin = state.selectedCoin
    val amount = parseAmount(state.amountText)
    val rate = state.rates[state.selectedCurrency]
    val ratesError = state.ratesError

    var dismissedError by remember { mutableStateOf<com.example.crypto.presentation.common.ErrorKind?>(null) }

    if (ratesError != null && ratesError != dismissedError) {
        ErrorDialog(
            kind = ratesError,
            onRetry = {
                dismissedError = null
                viewModel.onIntent(ConvertIntent.Refresh)
            },
            onDismiss = { dismissedError = ratesError }
        )
    }

    val result: Double? = when {
        amount == null || !state.isAmountValid -> null
        rate == null -> null
        state.direction == ConvertDirection.COIN_TO_CURRENCY -> amount * rate
        else -> if (rate != 0.0) amount / rate else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // "You have" card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.convert_you_have),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (state.direction == ConvertDirection.CURRENCY_TO_COIN) {
                    CurrencyChipsRow(state = state, viewModel = viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = state.amountText,
                        onValueChange = { viewModel.onIntent(ConvertIntent.AmountChanged(it)) },
                        modifier = Modifier.weight(1f),
                        isError = !state.isAmountValid,
                        supportingText = {
                            if (!state.isAmountValid) {
                                Text(stringResource(R.string.convert_enter_number))
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.titleLarge
                    )
                    if (state.direction == ConvertDirection.COIN_TO_CURRENCY && coin != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CoinButton(coin = coin, onClick = { viewModel.onIntent(ConvertIntent.OpenCoinPicker) })
                    }
                }
            }
        }

        // Swap button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            var rotated by remember { mutableStateOf(false) }
            val rotation by animateFloatAsState(
                targetValue = if (rotated) 180f else 0f,
                animationSpec = tween(300),
                label = "swapRotation"
            )
            FilledTonalIconButton(onClick = {
                rotated = !rotated
                viewModel.onIntent(ConvertIntent.Swap)
            }) {
                Icon(
                    Icons.Filled.SwapVert,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { rotationZ = rotation }
                )
            }
        }

        // "You get" card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.convert_you_get),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (state.direction == ConvertDirection.COIN_TO_CURRENCY) {
                    CurrencyChipsRow(state = state, viewModel = viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (coin != null) {
                    CoinButton(coin = coin, onClick = { viewModel.onIntent(ConvertIntent.OpenCoinPicker) })
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.isLoadingRates) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    val resultText = if (result != null) {
                        if (state.direction == ConvertDirection.COIN_TO_CURRENCY) {
                            formatCurrencyValue(state.selectedCurrency, result)
                        } else {
                            formatCryptoAmount(result, coin?.symbol ?: "")
                        }
                    } else {
                        stringResource(R.string.value_placeholder)
                    }
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (rate != null && coin != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(
                                R.string.convert_rate_line,
                                coin.symbol,
                                formatCurrencyValue(state.selectedCurrency, rate)
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // "Same amount in" card
        if (state.direction == ConvertDirection.COIN_TO_CURRENCY && amount != null && state.isAmountValid) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.convert_same_amount_in),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ConvertCurrencies.CODES
                        .filter { it != state.selectedCurrency }
                        .forEachIndexed { index, code ->
                            if (index > 0) HorizontalDivider()
                            val codeRate = state.rates[code]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onIntent(ConvertIntent.SelectCurrency(code)) }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = code)
                                Text(
                                    text = codeRate?.let { formatCurrencyValue(code, amount * it) }
                                        ?: stringResource(R.string.value_placeholder)
                                )
                            }
                        }
                }
            }
        }

        Text(
            text = stringResource(R.string.convert_credits_note, ConvertCurrencies.CODES.size),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CurrencyChipsRow(state: ConvertState, viewModel: ConvertViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        ConvertCurrencies.CODES.forEach { code ->
            FilterChip(
                selected = state.selectedCurrency == code,
                onClick = { viewModel.onIntent(ConvertIntent.SelectCurrency(code)) },
                label = { Text(code) }
            )
        }
    }
}

@Composable
private fun CoinButton(coin: Coin, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick, shape = RoundedCornerShape(12.dp)) {
        AsyncImage(
            model = "https://s2.coinmarketcap.com/static/img/coins/64x64/${coin.id}.png",
            contentDescription = coin.name,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(coin.symbol)
        Text(" ▾")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoinPickerSheet(state: ConvertState, viewModel: ConvertViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filteredCoins = state.coins.filter {
        state.coinSearchQuery.isBlank() ||
                it.name.contains(state.coinSearchQuery, ignoreCase = true) ||
                it.symbol.contains(state.coinSearchQuery, ignoreCase = true)
    }

    ModalBottomSheet(
        onDismissRequest = { viewModel.onIntent(ConvertIntent.CloseCoinPicker) },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = stringResource(R.string.convert_choose_coin),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.coinSearchQuery,
                onValueChange = { viewModel.onIntent(ConvertIntent.SearchCoins(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.convert_search_placeholder)) },
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                items(filteredCoins, key = { it.id }) { coin ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onIntent(ConvertIntent.SelectCoin(coin)) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = "https://s2.coinmarketcap.com/static/img/coins/64x64/${coin.id}.png",
                            contentDescription = coin.name,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = coin.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = coin.symbol,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}