package com.nikhilbiju67.compose_chat_ui.video

import com.nikhilbiju67.compose_chat_ui.audio.PlayerState
import kotlinx.coroutines.flow.Flow
import models.MediaStatus
import models.VideoMessage

expect class VideoPlayer(playerState: PlayerState) {
    fun pause()
    fun play(message: VideoMessage)
    fun seekTo(time: Double)
    fun playerState(): PlayerState
    fun cleanUp()
    val mediaStatus: Flow<MediaStatus>

// Flow to observe media status
}

data class VideoControlStatus(
    var playStatus: PlayStatus,
    val videoSeekProgress: Float
)

data class VideoStatusCallback(
    val playStatus: PlayStatus,
    val videoProgress: Float
)

enum class PlayStatus {
    PLAYING,
    PAUSED,
    STOPPED
}
