package com.example.crypto.presentation.coinlist

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.crypto.R
import com.example.crypto.domain.model.Coin
import com.example.crypto.presentation.common.formatCompactNumber
import com.example.crypto.presentation.common.formatCompactUsd
import com.example.crypto.presentation.common.formatPercent
import com.example.crypto.presentation.common.formatPrice

private val PositiveColor = Color(0xFF16C784)
private val NegativeColor = Color(0xFFEA3943)

@Composable
fun CoinListScreen(
    viewModel: CoinListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var expandedId by rememberSaveable { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.coin_list_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            state.error != null -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.error_message, state.error ?: ""),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.coins, key = { it.id }) { coin ->
                        CoinCard(
                            coin = coin,
                            expanded = expandedId == coin.id,
                            onClick = {
                                expandedId = if (expandedId == coin.id) null else coin.id
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CoinCard(coin: Coin, expanded: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = coin.cmcRank?.toString() ?: stringResource(R.string.value_placeholder),
                    modifier = Modifier.width(22.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                AsyncImage(
                    model = "https://s2.coinmarketcap.com/static/img/coins/64x64/${coin.id}.png",
                    contentDescription = coin.name,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = coin.name, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Text(
                        text = coin.symbol,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.coin_price_usd, formatPrice(coin.price)),
                        fontWeight = FontWeight.SemiBold
                    )
                    PercentPill(value = coin.percentChange24h)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LabeledPercent(
                            label = stringResource(R.string.period_1h),
                            value = coin.percentChange1h
                        )
                        LabeledPercent(
                            label = stringResource(R.string.period_7d),
                            value = coin.percentChange7d
                        )
                        LabeledPercent(
                            label = stringResource(R.string.period_30d),
                            value = coin.percentChange30d
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn(
                            label = stringResource(R.string.label_market_cap),
                            value = formatCompactUsd(coin.marketCap)
                        )
                        StatColumn(
                            label = stringResource(R.string.label_volume_24h),
                            value = formatCompactUsd(coin.volume24h)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn(
                            label = stringResource(R.string.label_dominance),
                            value = coin.marketCapDominance?.let { "%.1f%%".format(it) }
                                ?: stringResource(R.string.value_placeholder)
                        )
                        StatColumn(
                            label = stringResource(R.string.label_pairs),
                            value = coin.numMarketPairs?.toString()
                                ?: stringResource(R.string.value_placeholder)
                        )
                    }

                    val circulating = coin.circulatingSupply
                    val max = coin.maxSupply
                    if (circulating != null && max != null && max > 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(
                                R.string.label_supply,
                                formatCompactNumber(circulating),
                                formatCompactNumber(max)
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (circulating / max).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PercentPill(value: Double) {
    val isPositive = value >= 0
    val color = if (isPositive) PositiveColor else NegativeColor
    Text(
        text = stringResource(R.string.coin_percent_change, formatPercent(value)),
        color = color,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun LabeledPercent(label: String, value: Double?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val color = if ((value ?: 0.0) >= 0) PositiveColor else NegativeColor
        val text = if (value != null) {
            stringResource(R.string.coin_percent_change, formatPercent(value))
        } else {
            stringResource(R.string.value_placeholder)
        }
        Text(text = text, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}