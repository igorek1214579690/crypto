package com.example.crypto.presentation.keyinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.crypto.R
import com.example.crypto.domain.model.KeyInfo

@Composable
fun KeyInfoScreen(
    viewModel: KeyInfoViewModel = hiltViewModel()
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
            state.keyInfo != null -> {
                KeyInfoContent(info = state.keyInfo!!)
            }
        }
    }
}

@Composable
private fun KeyInfoContent(info: KeyInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LabelText(
            stringResource(
                R.string.key_info_monthly_reset,
                info.creditLimitMonthlyReset ?: placeholder()
            )
        )
        LabelText(stringResource(R.string.key_info_monthly_limit, info.creditLimitMonthly.orDash()))

        Spacer(modifier = Modifier.height(8.dp))
        SectionTitle(stringResource(R.string.key_info_current_day))
        UsageRow(used = info.creditsUsedToday, left = info.creditsLeftToday)

        Spacer(modifier = Modifier.height(8.dp))
        SectionTitle(stringResource(R.string.key_info_current_month))
        UsageRow(used = info.creditsUsedMonth, left = info.creditsLeftMonth)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun UsageRow(used: Int?, left: Int?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LabelText(stringResource(R.string.key_info_credits_used, used.orDash()))
        LabelText(stringResource(R.string.key_info_credits_left, left.orDash()))
    }
}

@Composable
private fun LabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun placeholder(): String = stringResource(R.string.value_placeholder)

@Composable
private fun Int?.orDash(): String = this?.toString() ?: placeholder()
