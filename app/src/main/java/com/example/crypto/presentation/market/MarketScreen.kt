package com.example.crypto.presentation.market

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.crypto.R
import com.example.crypto.domain.model.FearGreed
import com.example.crypto.presentation.common.animatedDouble
import com.example.crypto.presentation.common.animatedInt
import com.example.crypto.presentation.common.formatCompactUsd
import com.example.crypto.presentation.common.formatPercent
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import com.example.crypto.presentation.common.formatTimestamp
import androidx.compose.material3.Icon
import com.example.crypto.presentation.common.ErrorFullScreen

private val PositiveColor = Color(0xFF16C784)
private val NegativeColor = Color(0xFFEA3943)
private val DominanceBtcColor = Color(0xFFF7931A)
private val DominanceEthColor = Color(0xFF627EEA)
private val DominanceOtherColor = Color(0xFFB0B4BA)

private fun fearGreedColor(value: Int): Color = when {
    value < 25 -> Color(0xFFEA3943)
    value < 45 -> Color(0xFFF3A440)
    value <= 55 -> Color(0xFFF8D33A)
    value <= 75 -> Color(0xFF9ED660)
    else -> Color(0xFF16C784)
}

@Composable
fun MarketScreen(viewModel: MarketViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.market_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        if (state.isFromCache) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.CloudOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        R.string.offline_banner,
                        state.cacheUpdatedAt?.let { formatTimestamp(it) } ?: "—"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        when {
            state.isLoading && state.overview == null -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            state.error != null && state.overview == null -> {
                ErrorFullScreen(
                    kind = state.error!!,
                    onRetry = { viewModel.load() }
                )


            }
            state.overview != null -> {
                val overview = state.overview!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    overview.fearGreed?.let { fg ->
                        FearGreedCard(fearGreed = fg, history = overview.fearGreedHistory)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val mcText = overview.totalMarketCap?.let {
                            formatCompactUsd(animatedDouble(it))
                        } ?: stringResource(R.string.value_placeholder)
                        StatCard(
                            title = stringResource(R.string.market_total_cap),
                            value = mcText,
                            changeText = overview.totalMarketCapChange?.let {
                                stringResource(R.string.coin_percent_change, formatPercent(it))
                            },
                            changeColor = if ((overview.totalMarketCapChange ?: 0.0) >= 0) PositiveColor else NegativeColor,
                            modifier = Modifier.weight(1f)
                        )

                        val volText = overview.totalVolume24h?.let {
                            formatCompactUsd(animatedDouble(it, delayMillis = 100))
                        } ?: stringResource(R.string.value_placeholder)
                        StatCard(
                            title = stringResource(R.string.market_volume_24h),
                            value = volText,
                            changeText = overview.totalVolumeChange?.let {
                                stringResource(R.string.coin_percent_change, formatPercent(it))
                            },
                            changeColor = if ((overview.totalVolumeChange ?: 0.0) >= 0) PositiveColor else NegativeColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    overview.btcDominance?.let { btc ->
                        val eth = overview.ethDominance ?: 0.0
                        val other = (100.0 - btc - eth).coerceAtLeast(0.0)
                        DominanceCard(btc = btc, eth = eth, other = other)
                    }

                    overview.altcoinSeasonIndex?.let { index ->
                        AltSeasonCard(index = index)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = stringResource(R.string.market_active_coins),
                            value = overview.activeCryptocurrencies?.let { animatedInt(it).toString() }
                                ?: stringResource(R.string.value_placeholder),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = stringResource(R.string.market_exchanges),
                            value = overview.activeExchanges?.let {
                                animatedInt(it, delayMillis = 100).toString()
                            } ?: stringResource(R.string.value_placeholder),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FearGreedCard(fearGreed: FearGreed, history: List<FearGreed>) {
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
                text = stringResource(R.string.market_fear_greed),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                FearGreedGauge(
                    value = fearGreed.value,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(2f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.End) {
                    val animatedValue = animatedInt(fearGreed.value)
                    Text(
                        text = animatedValue.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val color = fearGreedColor(fearGreed.value)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(color.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = fearGreed.classification,
                            color = color,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (history.size > 1) {
                Spacer(modifier = Modifier.height(16.dp))
                FearGreedHistoryBars(history = history)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.market_fear_greed_history, history.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FearGreedGauge(value: Int, modifier: Modifier = Modifier) {
    val zoneColors = listOf(
        Color(0xFFEA3943),
        Color(0xFFF3A440),
        Color(0xFFF8D33A),
        Color(0xFF9ED660),
        Color(0xFF16C784)
    )
    Canvas(modifier = modifier) {
        val strokeWidthPx = size.height * 0.22f
        val radius = (size.width / 2f) - strokeWidthPx / 2f
        val center = Offset(size.width / 2f, size.height)
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        val sweepPerZone = 180f / zoneColors.size

        zoneColors.forEachIndexed { index, color ->
            drawArc(
                color = color,
                startAngle = 180f + index * sweepPerZone,
                sweepAngle = sweepPerZone,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
            )
        }

        val angleDeg = 180f + (value.coerceIn(0, 100) / 100f) * 180f
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val needleLength = radius * 0.8f
        val end = Offset(
            x = center.x + (needleLength * cos(angleRad)).toFloat(),
            y = center.y + (needleLength * sin(angleRad)).toFloat()
        )
        drawLine(
            color = Color.DarkGray,
            start = center,
            end = end,
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        drawCircle(color = Color.DarkGray, radius = 8f, center = center)
    }
}

@Composable
private fun FearGreedHistoryBars(history: List<FearGreed>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        history.forEachIndexed { index, item ->
            val targetFraction = (item.value / 100f).coerceIn(0.05f, 1f)
            val animatedFraction by animateFloatAsState(
                targetValue = targetFraction,
                animationSpec = androidx.compose.animation.core.tween(
                    durationMillis = 400,
                    delayMillis = index * 20
                ),
                label = "barHeight"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(animatedFraction)
                    .clip(RoundedCornerShape(1.dp))
                    .background(fearGreedColor(item.value))
            )
        }
    }
}

@Composable
private fun DominanceCard(btc: Double, eth: Double, other: Double) {
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
                text = stringResource(R.string.market_dominance),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(btc.toFloat().coerceAtLeast(0.001f))
                        .fillMaxHeight()
                        .background(DominanceBtcColor)
                )
                Box(
                    modifier = Modifier
                        .weight(eth.toFloat().coerceAtLeast(0.001f))
                        .fillMaxHeight()
                        .background(DominanceEthColor)
                )
                Box(
                    modifier = Modifier
                        .weight(other.toFloat().coerceAtLeast(0.001f))
                        .fillMaxHeight()
                        .background(DominanceOtherColor)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem(
                    color = DominanceBtcColor,
                    text = stringResource(R.string.market_dominance_btc, "%.1f".format(btc))
                )
                LegendItem(
                    color = DominanceEthColor,
                    text = stringResource(R.string.market_dominance_eth, "%.1f".format(eth))
                )
                LegendItem(
                    color = DominanceOtherColor,
                    text = stringResource(R.string.market_dominance_other, "%.1f".format(other))
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AltSeasonCard(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.market_altseason),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val animatedIndex = animatedInt(index)
                Text(
                    text = stringResource(R.string.market_altseason_value, animatedIndex),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            val fraction by animateFloatAsState(
                targetValue = (index / 100f).coerceIn(0f, 1f),
                label = "altseason"
            )
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.market_bitcoin_season),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.market_altcoin_season),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    changeText: String? = null,
    changeColor: Color = PositiveColor,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (changeText != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(changeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = changeText,
                        color = changeColor,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}