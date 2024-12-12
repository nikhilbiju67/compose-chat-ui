package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.coroutines.flow.Flow
import models.AudioMessage
import models.MediaStatus
import models.Message

expect class AudioPlayer(playerState: PlayerState) {
    fun pause()
    fun play(message: AudioMessage)
    fun seekTo(time: Double)
    fun playerState(): PlayerState
    fun cleanUp()
    val mediaStatus: Flow<MediaStatus>

// Flow to observe media status
}
