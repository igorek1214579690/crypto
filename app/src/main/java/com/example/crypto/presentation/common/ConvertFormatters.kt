package com.example.crypto.presentation.common

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

private fun thousands(value: Double, decimals: Int): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val pattern = "#,##0." + "0".repeat(decimals)
    val format = DecimalFormat(pattern, symbols)
    format.roundingMode = RoundingMode.HALF_UP
    return format.format(value)
}

fun formatFiatPrefixed(value: Double, symbol: String): String = "$symbol${thousands(value, 2)}"

fun formatFiatSuffixed(value: Double, symbol: String): String = "${thousands(value, 2)} $symbol"

fun formatCryptoAmount(value: Double, ticker: String): String {
    val a = abs(value)
    val numberPart = when {
        a == 0.0 -> "0"
        a >= 1000 -> thousands(value, 2)
        a >= 0.0001 -> thousands(value, 4)
        else -> BigDecimal.valueOf(value)
            .round(MathContext(6, RoundingMode.HALF_UP))
            .toPlainString()
    }
    return "$numberPart $ticker"
}

fun formatCurrencyValue(code: String, value: Double): String = when (code) {
    "USD" -> formatFiatPrefixed(value, "$")
    "EUR" -> formatFiatPrefixed(value, "€")
    "BYN" -> formatFiatSuffixed(value, "Br")
    "RUB" -> formatFiatSuffixed(value, "₽")
    "BTC" -> formatCryptoAmount(value, "BTC")
    else -> thousands(value, 2)
}