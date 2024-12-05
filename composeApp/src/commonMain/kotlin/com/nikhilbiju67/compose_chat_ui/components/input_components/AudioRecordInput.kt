package com.nikhilbiju67.compose_chat_ui.components.input_components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import components.BlinkView
import composechatui.composeapp.generated.resources.Res
import composechatui.composeapp.generated.resources.baseline_mic_24
import composechatui.composeapp.generated.resources.delete_24px
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource

@Composable
fun AudioRecordingInput(
    modifier: Modifier = Modifier,
    onAnimationComplete: () -> Unit,
    cancelled: Boolean = false
) {


    /// Alpha animation for the microphone
    val alphaAnimation = remember { Animatable(0f) }
    var micCancelState by remember { mutableStateOf(AnimateState.Idle) }

    /// Transitions for microphone and bin animations
    val micTransition = updateTransition(micCancelState, label = "Mic Transition")
    val binTransition = updateTransition(micCancelState, label = "Bin Shake Transition")

    /// Animated height for mic movement up/down
    val micHeight by micTransition.animateDp(
        transitionSpec = { tween(500, easing = EaseInOut) }
    ) {
        when (it) {
            AnimateState.GoingUp, AnimateState.Up -> (-200).dp
            else -> 0.dp
        }
    }

    /// Bin shake effect for canceling the mic input
    val binOffset by binTransition.animateDp(
        transitionSpec = {
            repeatable(
                iterations = 3,
                animation = tween(100, easing = EaseInOutBounce),
                repeatMode = RepeatMode.Reverse
            )
        }
    ) {
        if (it == AnimateState.Bottom) 5.dp else 0.dp
    }

    /// Opacity of the microphone, fades out when moving down
    val micOpacity by micTransition.animateFloat(
        transitionSpec = { tween(500, easing = EaseInOut) }
    ) {
        when (it) {
            AnimateState.GoingUp, AnimateState.Up -> 1f
            AnimateState.GoingDown -> if (micHeight > (-30).dp) 0f else 1f
            else -> 1f
        }
    }

    val micScale by micTransition.animateFloat(
        transitionSpec = { tween(500, easing = EaseInOut) }
    ) {
        when (it) {
            AnimateState.GoingUp, AnimateState.Up -> 1f
            AnimateState.GoingDown -> 0.5f
            else -> 1f
        }
    }

    /// Rotation animation for the microphone
    val rotationAngle by micTransition.animateFloat(
        transitionSpec = { tween(500, easing = EaseInOut) }
    ) {
        when (it) {
            AnimateState.GoingUp -> 120f
            AnimateState.GoingDown -> 360f
            AnimateState.Bottom -> 720f
            else -> 0f
        }
    }
    LaunchedEffect(cancelled) {
        if (cancelled) {
            if (micCancelState == AnimateState.Idle || micCancelState == AnimateState.Bottom) {
                micCancelState = AnimateState.GoingUp
            }
        }
    }
    /// Transition listeners to handle sequential animations
    LaunchedEffect(micTransition) {
        snapshotFlow { micTransition.currentState == micTransition.targetState }
            .collectLatest { atTarget ->
                if (atTarget) {
                    micCancelState = when (micCancelState) {
                        AnimateState.GoingUp -> AnimateState.GoingDown
                        AnimateState.GoingDown -> AnimateState.Bottom
                        else -> micCancelState
                    }
                }
            }
    }

    LaunchedEffect(binTransition) {
        snapshotFlow { binTransition.currentState == binTransition.targetState }
            .collectLatest { atTarget ->
                if (atTarget && micCancelState == AnimateState.Bottom) {
                    micCancelState = AnimateState.Idle
                    onAnimationComplete()
                }
            }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            /// Mic icon with blinking and rotating animation
            BlinkView(
                blink = micCancelState == AnimateState.Idle
            ) {
                Image(
                    modifier = Modifier
                        .graphicsLayer { scaleX = micScale; scaleY = micScale }
                        .size(30.dp)
                        .offset(0.dp, micHeight)
                        .rotate(rotationAngle)
                        .graphicsLayer { alpha = micOpacity },
                    painter = painterResource(Res.drawable.baseline_mic_24),
                    contentDescription = "Mic",
                    colorFilter = ColorFilter.tint(Color.Red)
                )
            }

            /// Display delete icon when mic is canceling
            if (micCancelState != AnimateState.Idle && micCancelState != AnimateState.GoingUp) {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .offset { IntOffset(binOffset.value.toInt(), binOffset.value.toInt() / 5) },
                    painter = painterResource(Res.drawable.delete_24px),
                    contentDescription = "Delete",
                    colorFilter = ColorFilter.tint(Color.Red)
                )
            }
        }
        Spacer(Modifier)
        /// Instructional tex
    }
}


enum class AnimateState {
    GoingUp,
    GoingDown,
    Up,
    Idle,
    Bottom
}

