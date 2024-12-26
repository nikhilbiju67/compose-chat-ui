package com.nikhilbiju67.compose_chat_ui.video

import com.nikhilbiju67.compose_chat_ui.audio.PlayerState
import kotlinx.coroutines.flow.Flow
import models.MediaStatus
import models.VideoMessage

actual class VideoPlayer actual constructor(playerState: PlayerState) {

// Flow to observe media status
    actual fun pause() {
    }

    actual fun play(message:VideoMessage) {
    }

    actual fun seekTo(time: Double) {
    }

    actual fun playerState(): PlayerState {
        TODO("Not yet implemented")
    }

    actual fun cleanUp() {
    }

    actual val mediaStatus: Flow<MediaStatus>
        get() = TODO("Not yet implemented")
}