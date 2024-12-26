package com.nikhilbiju67.compose_chat_ui.audio

import androidx.compose.runtime.Stable

@Stable
data class PlayerState(
    var isPlaying: Boolean = false,
    var isBuffering: Boolean = false,
    var currentTime: Int = 0,
    var duration: Int = 0,
    var currentlyPlayingId: String? = null

    ) {
    val progress = currentTime.toFloat() / duration.toFloat()
}

data class playerAction(
    val play: Boolean,
    val pause: Boolean,
    val seekTo: Int
)


