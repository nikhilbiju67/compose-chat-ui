package com.nikhilbiju67.compose_chat_ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TypingIndicator(
    modifier: Modifier,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    dotSize: Int = 8,
    spaceBetween: Int = 4,
    animationDelay: Int = 300 // milliseconds
) {
    val dotScales = listOf(
        remember { Animatable(1f) },
        remember { Animatable(1f) },
        remember { Animatable(1f) }
    )

    // Start the animation
    LaunchedEffect(Unit) {
        dotScales.forEachIndexed { index, animatable ->
            launch {
                delay((animationDelay * index).toLong()) // Delay each dot animation
                while (true) {
                    animatable.animateTo(
                        targetValue = 1.5f,
                        animationSpec = tween(durationMillis = animationDelay, easing = LinearOutSlowInEasing)
                    )
                    animatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = animationDelay, easing = LinearOutSlowInEasing)
                    )
                }
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(spaceBetween.dp),
        modifier = modifier.padding(8.dp)
    ) {
        dotScales.forEach { scale ->
            Box(
                modifier = Modifier
                    .size(dotSize.dp)
                    .scale(scale.value)
                    .background(color = dotColor, shape = CircleShape)
            )
        }
    }
}
