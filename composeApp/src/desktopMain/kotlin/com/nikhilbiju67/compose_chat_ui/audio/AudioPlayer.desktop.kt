package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import models.AudioMessage
import models.MediaStatus
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer

actual class AudioPlayer actual constructor(
    private val playerState: PlayerState, private val onProgressCallback: (PlayerState) -> Unit,

    ) {
    private var mediaPlayer: MediaPlayer? = null
    private val _mediaStatus = MutableStateFlow(MediaStatus())
    actual val mediaStatus: Flow<MediaStatus> get() = _mediaStatus.asStateFlow()
    var currentlyPlaying: AudioMessage? = null

    init {
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        NativeDiscovery().discover()

        val playerComponent = EmbeddedMediaPlayerComponent()
        mediaPlayer = playerComponent.mediaPlayer()
        (mediaPlayer as EmbeddedMediaPlayer?)?.videoSurface()?.set(null) // No video output needed
        mediaPlayer?.events()?.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                playerState.currentTime = (newTime / 1000).toInt()
                playerState.duration = ((mediaPlayer?.media()?.info()?.duration() ?: 0)/1000).toInt()
                onProgressCallback(
                    PlayerState(
                        isPlaying = playerState.isPlaying,
                        currentTime = playerState.currentTime,
                        duration = playerState.duration,
                        currentlyPlayingId = currentlyPlaying?.id
                    )
                )
//                _mediaStatus.update {
//                    it.copy(
//                        audioProgress = progressPercentage / 100,
//                        isPlaying = playerState.isPlaying,
//                        currentlyPlaying = currentlyPlaying,
//                    )
//                }


            }

            override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
                super.mediaPlayerReady(mediaPlayer)

            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                playerState.isPlaying = false
                playerState.currentTime = 0
                onProgressCallback(
                    PlayerState(
                        isPlaying = false,
                        currentTime = 0,
                        duration = playerState.duration,
                        currentlyPlayingId = currentlyPlaying?.id
                    )
                )
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                playerState.isPlaying = false
                onProgressCallback(
                    PlayerState(
                        isPlaying = false,
                        currentTime = 0,
                        duration = playerState.duration,
                        currentlyPlayingId = currentlyPlaying?.id
                    )
                )
            }
        })
    }

    actual fun play(message: AudioMessage) {
        var url = message.effectiveAudioSource
        if (url == null) {
            return
        }
        println("Attempting to play: $url")
        if (mediaPlayer?.media()?.play(url) == true) {
            currentlyPlaying = message
            onProgressCallback(
                playerState.copy(
                    isPlaying = true,
                    currentlyPlayingId = currentlyPlaying?.id
                )
            )
            println("Playback started successfully.")
            playerState.isPlaying = true

            _mediaStatus.update {
                it.copy(
                    isPlaying = playerState.isPlaying
                )
            }
            mediaPlayer?.audio()?.setVolume(100) // Set volume to 100%
        } else {
            println("Failed to start playback.")
        }
    }

    actual fun pause() {
        mediaPlayer?.controls()?.pause()
        playerState.isPlaying = false
    }

    actual fun seekTo(time: Double) {
        mediaPlayer?.controls()?.setTime((time * 1000).toLong()) // Convert seconds to ms
    }

    actual fun playerState(): PlayerState {
        return playerState
    }

    actual fun cleanUp() {
        mediaPlayer?.controls()?.stop()
        mediaPlayer?.release()

        playerState.isPlaying = false
        playerState.currentTime = 0
        playerState.duration = 0
        _mediaStatus.value = MediaStatus()
    }
}