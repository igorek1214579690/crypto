package com.example.crypto.presentation.coinlist

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.crypto.presentation.common.formatCompactNumber
import com.example.crypto.presentation.common.formatCompactUsd
import com.example.crypto.presentation.common.formatPercent
import com.example.crypto.presentation.common.formatPrice
import androidx.compose.foundation.layout.size

private val PositiveColor = Color(0xFF16C784)
private val NegativeColor = Color(0xFFEA3943)

@Composable
fun CoinDetailScreen(
    coinId: Int,
    onBack: () -> Unit,
    listViewModel: CoinListViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val state by listViewModel.state.collectAsState()
    val coin = state.coins.firstOrNull { it.id == coinId }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
        }

        if (coin == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://s2.coinmarketcap.com/static/img/coins/64x64/${coin.id}.png",
                    contentDescription = coin.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${coin.name}  ${coin.symbol}  #${coin.cmcRank ?: "-"}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatPrice(coin.price),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                val color24 = if (coin.percentChange24h >= 0) PositiveColor else NegativeColor
                Text(
                    text = formatPercent(coin.percentChange24h),
                    color = color24,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PercentStat(label = "1h", value = coin.percentChange1h)
                PercentStat(label = "7d", value = coin.percentChange7d)
                PercentStat(label = "30d", value = coin.percentChange30d)
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn(label = "Market cap", value = formatCompactUsd(coin.marketCap))
                StatColumn(label = "Vol 24h", value = formatCompactUsd(coin.volume24h))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn(
                    label = "Dominance",
                    value = coin.marketCapDominance?.let { "%.1f%%".format(it) } ?: "—"
                )
                StatColumn(
                    label = "Pairs",
                    value = coin.numMarketPairs?.toString() ?: "—"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val circulating = coin.circulatingSupply
            val max = coin.maxSupply
            if (circulating != null && max != null && max > 0) {
                Text(
                    text = "Supply ${formatCompactNumber(circulating)} / ${formatCompactNumber(max)}",
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PercentStat(label: String, value: Double?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val color = if ((value ?: 0.0) >= 0) PositiveColor else NegativeColor
        Text(text = formatPercent(value), color = color, fontWeight = FontWeight.Medium)
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