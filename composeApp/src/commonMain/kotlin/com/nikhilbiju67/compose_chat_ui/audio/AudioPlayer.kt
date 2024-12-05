package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.coroutines.flow.Flow

expect class AudioPlayer(playerState: PlayerState) {
    fun play()
    fun pause()
    fun next()
    fun prev()
    fun play(songIndex: Int)
    fun seekTo(time: Double)
    fun addSongsUrls(songsUrl: List<String>)
    fun playerState(): PlayerState
    fun cleanUp()
    val audioProgress: Flow<Double> // Flow to observe audio progress in seconds
}
