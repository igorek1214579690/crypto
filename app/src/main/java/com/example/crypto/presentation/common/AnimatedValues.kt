package com.example.crypto.presentation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@Composable
fun animatedDouble(target: Double, delayMillis: Int = 0, durationMillis: Int = 800): Double {
    val animated by animateFloatAsState(
        targetValue = target.toFloat(),
        animationSpec = tween(durationMillis = durationMillis, delayMillis = delayMillis),
        label = "animatedDouble"
    )
    return animated.toDouble()
}

@Composable
fun animatedInt(target: Int, delayMillis: Int = 0, durationMillis: Int = 800): Int {
    val animated by animateIntAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = durationMillis, delayMillis = delayMillis),
        label = "animatedInt"
    )
    return animated
}