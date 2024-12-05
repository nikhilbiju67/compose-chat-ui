package com.nikhilbiju67.compose_chat_ui.audio

import androidx.compose.runtime.*

@Stable
class PlayerState {
    var isPlaying by mutableStateOf(false)
        internal set
    var isBuffering by mutableStateOf(false)
    var currentTime: Long by mutableStateOf(0)
    var duration: Long by mutableStateOf(0)
    var currentItemIndex = -1
    var canNext by mutableStateOf(false)
    var canPrev by mutableStateOf(false)

    val progressPercentage: Int
        get() = if (duration > 0) ((currentTime * 100) / duration).toInt() else 0
}

@Composable
fun rememberPlayerState(): PlayerState {
    return remember {
        PlayerState()
    }
}