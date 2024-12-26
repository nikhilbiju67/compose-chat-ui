package com.nikhilbiju67.compose_chat_ui.audio

import models.AudioMessage

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class AudioPlayer(
    playerState: PlayerState,
    onProgressCallback: (PlayerState) -> Unit,
) {
    fun pause()
    fun play(message: AudioMessage)
    fun playerState(): PlayerState
    fun cleanUp()

// Flow to observe media status
}
