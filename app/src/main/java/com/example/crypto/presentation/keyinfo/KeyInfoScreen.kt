package com.example.crypto.presentation.keyinfo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.crypto.R
import com.example.crypto.domain.model.KeyInfo
import com.example.crypto.presentation.common.ErrorFullScreen
import java.util.Locale

@Composable
fun KeyInfoScreen(
    viewModel: KeyInfoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val keyInfo = state.keyInfo
    val error = state.error

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.key_info_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = viewModel::loadKeyInfo, enabled = !state.isLoading) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.action_refresh))
            }
        }

        when {
            // Keep the data on screen while refreshing; show a thin bar instead of a full-screen spinner.
            keyInfo != null -> {
                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                KeyInfoContent(info = keyInfo)
            }
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            error != null -> {
                ErrorFullScreen(kind = error, onRetry = viewModel::loadKeyInfo)
            }
        }
    }
}

@Composable
private fun KeyInfoContent(info: KeyInfo) {
    val monthLimit = info.creditLimitMonthly
        ?: sumOrNull(info.creditsUsedMonth, info.creditsLeftMonth)
    val dayLimit = info.creditLimitDaily
        ?: sumOrNull(info.creditsUsedToday, info.creditsLeftToday)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MonthCard(
            left = info.creditsLeftMonth,
            used = info.creditsUsedMonth,
            limit = monthLimit,
            resetAt = info.creditLimitMonthlyResetAt,
            resetRaw = info.creditLimitMonthlyReset
        )
        TodayCard(
            used = info.creditsUsedToday,
            left = info.creditsLeftToday,
            limit = dayLimit
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallStatCard(
                title = stringResource(R.string.key_info_month_used),
                value = info.creditsUsedMonth.formatCount(),
                caption = percentOf(info.creditsUsedMonth, monthLimit)
                    ?.let { stringResource(R.string.key_info_percent_of_limit, it) },
                modifier = Modifier.weight(1f)
            )
            SmallStatCard(
                title = stringResource(R.string.key_info_rate_limit),
                value = info.rateLimitMinute.formatCount(),
                valueSuffix = stringResource(R.string.key_info_per_minute),
                caption = stringResource(R.string.key_info_requests),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MonthCard(left: Int?, used: Int?, limit: Int?, resetAt: Long?, resetRaw: String?) {
    InfoCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UsageRing(
                usedFraction = fractionOf(used, limit),
                modifier = Modifier.size(184.dp)
            ) {
                Text(
                    text = left.formatCount(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.key_info_credits_left_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (limit != null) {
                    Text(
                        text = stringResource(R.string.key_info_of_month_limit, limit.formatCount()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            val resetText = resetLabel(resetAt, resetRaw)
            if (resetText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ResetChip(text = resetText)
            }
        }
    }
}

/**
 * Builds a localized "Resets in 25 days, 5 hours" from the reset timestamp.
 * Falls back to the API's English string when the timestamp is missing.
 */
@Composable
private fun resetLabel(resetAt: Long?, raw: String?): String? {
    if (resetAt == null) {
        return raw?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.key_info_resets_raw, it) }
    }
    val totalHours = ((resetAt - System.currentTimeMillis()) / 3_600_000L).coerceAtLeast(0L)
    val days = (totalHours / 24).toInt()
    val hours = (totalHours % 24).toInt()
    val daysText = if (days > 0) pluralStringResource(R.plurals.key_info_days, days, days) else null
    val hoursText = if (hours > 0) pluralStringResource(R.plurals.key_info_hours, hours, hours) else null
    val duration = listOfNotNull(daysText, hoursText).joinToString(", ")
    return if (duration.isEmpty()) {
        stringResource(R.string.key_info_resets_soon)
    } else {
        stringResource(R.string.key_info_resets, duration)
    }
}

@Composable
private fun UsageRing(
    usedFraction: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val animated by animateFloatAsState(
        targetValue = usedFraction,
        animationSpec = tween(durationMillis = 800),
        label = "usageRing"
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val inset = strokeWidth / 2
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            if (animated > 0f) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animated,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) { content() }
    }
}

@Composable
private fun ResetChip(text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Autorenew,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun TodayCard(used: Int?, left: Int?, limit: Int?) {
    val fraction = fractionOf(used, limit)
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 800),
        label = "todayBar"
    )
    InfoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardTitle(stringResource(R.string.key_info_today))
            if (limit != null) {
                Text(
                    text = "${used.formatCount()} / ${limit.formatCount()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { animated },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (fraction >= 0.9f) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            drawStopIndicator = {}
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Caption(stringResource(R.string.key_info_used, used.formatCount()))
            Caption(stringResource(R.string.key_info_left, left.formatCount()))
        }
    }
}

@Composable
private fun SmallStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueSuffix: String? = null,
    caption: String? = null
) {
    InfoCard(modifier = modifier) {
        CardTitle(title)
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (valueSuffix != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = valueSuffix,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
        if (caption != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Caption(caption)
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun CardTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun Caption(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// --- helpers ---

private fun sumOrNull(a: Int?, b: Int?): Int? = if (a != null && b != null) a + b else null

private fun fractionOf(used: Int?, limit: Int?): Float {
    if (used == null || limit == null || limit <= 0) return 0f
    return (used.toFloat() / limit).coerceIn(0f, 1f)
}

private fun percentOf(used: Int?, limit: Int?): Int? {
    if (used == null || limit == null || limit <= 0) return null
    return (used * 100.0 / limit).toInt()
}

@Composable
private fun Int?.formatCount(): String =
    this?.let { String.format(Locale.US, "%,d", it) } ?: stringResource(R.string.value_placeholder)
