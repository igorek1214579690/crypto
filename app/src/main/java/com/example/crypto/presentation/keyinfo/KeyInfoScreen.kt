package com.example.crypto.presentation.keyinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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
                    text = "Ошибка: ${state.error}",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.keyInfo != null -> {
                val info = state.keyInfo!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Лимит в день: ${info.creditLimitDaily ?: "—"}")
                    Text(text = "Использовано сегодня: ${info.creditsUsedToday ?: "—"}")
                    Text(text = "Осталось сегодня: ${info.creditsLeftToday ?: "—"}")
                    Text(text = "Лимит в месяц: ${info.creditLimitMonthly ?: "—"}")
                    Text(text = "Использовано за месяц: ${info.creditsUsedMonth ?: "—"}")
                    Text(text = "Осталось за месяц: ${info.creditsLeftMonth ?: "—"}")
                }
            }
        }
    }
}