package com.example.crypto.presentation.common

import java.util.Locale
import kotlin.math.abs

fun formatPrice(value: Double): String {
    return when {
        value >= 1 -> String.format(Locale.US, "$%,.2f", value)
        value > 0 -> String.format(Locale.US, "$%.6f", value).trimEnd('0').trimEnd('.')
        else -> "$0.00"
    }
}

fun formatCompactUsd(value: Double?): String {
    if (value == null) return "—"
    val a = abs(value)
    return when {
        a >= 1_000_000_000_000 -> String.format(Locale.US, "$%.2fT", value / 1_000_000_000_000)
        a >= 1_000_000_000 -> String.format(Locale.US, "$%.1fB", value / 1_000_000_000)
        a >= 1_000_000 -> String.format(Locale.US, "$%.1fM", value / 1_000_000)
        a >= 1_000 -> String.format(Locale.US, "$%.1fK", value / 1_000)
        else -> String.format(Locale.US, "$%.2f", value)
    }
}

fun formatCompactNumber(value: Double?): String {
    if (value == null) return "—"
    val a = abs(value)
    return when {
        a >= 1_000_000_000 -> String.format(Locale.US, "%.1fB", value / 1_000_000_000)
        a >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000)
        a >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000)
        else -> String.format(Locale.US, "%.0f", value)
    }
}

fun formatPercent(value: Double?): String {
    if (value == null) return "—"
    val sign = if (value >= 0) "+" else ""
    return String.format(Locale.US, "%s%.2f%%", sign, value)
}