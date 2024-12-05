package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer

actual class AudioPlayer actual constructor(private val playerState: PlayerState) {
    private var mediaPlayer: MediaPlayer? = null
    private val mediaItems = mutableListOf<String>()
    private var currentItemIndex = -1

    private val _audioProgress = MutableStateFlow(0.0)
    actual val audioProgress: Flow<Double> get() = _audioProgress.asStateFlow()

    init {
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        NativeDiscovery().discover()

        val playerComponent = EmbeddedMediaPlayerComponent()
        mediaPlayer = playerComponent.mediaPlayer()
        (mediaPlayer as EmbeddedMediaPlayer?)?.videoSurface()?.set(null)
        mediaPlayer?.events()?.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                _audioProgress.value = newTime / 1000.0 // Convert ms to seconds
                playerState.currentTime = newTime / 1000
                playerState.duration = mediaPlayer?.media()?.info()?.duration() ?: 0
            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                next()
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                // playerState.error = "An error occurred during playback."
            }
        })
    }

    actual fun play() {
        if (currentItemIndex == -1 && mediaItems.isNotEmpty()) {
            play(0)
        } else {
            mediaPlayer?.controls()?.play()
            playerState.isPlaying = true
        }
    }

    actual fun pause() {
        mediaPlayer?.controls()?.pause()
        playerState.isPlaying = false
    }

    actual fun next() {
        if (currentItemIndex + 1 < mediaItems.size) {
            play(currentItemIndex + 1)
        }
    }

    actual fun prev() {
        if (currentItemIndex > 0) {
            play(currentItemIndex - 1)
        } else {
            seekTo(0.0)
        }
    }

    actual fun play(songIndex: Int) {
        if (songIndex in mediaItems.indices) {
            currentItemIndex = songIndex
            mediaPlayer?.media()?.play(mediaItems[songIndex])
            playerState.isPlaying = true
            playerState.currentItemIndex = songIndex
        }
    }

    actual fun seekTo(time: Double) {
        mediaPlayer?.controls()?.setTime((time * 1000).toLong()) // Convert seconds to ms
    }

    actual fun addSongsUrls(songsUrl: List<String>) {
        mediaItems.clear()
        mediaItems.addAll(songsUrl)
        if (mediaItems.isNotEmpty()) {
            play(0)
        }
    }


    actual fun playerState(): PlayerState {
        return playerState
    }

    actual fun cleanUp() {
        mediaPlayer?.controls()?.stop()
        mediaPlayer?.release()
        _audioProgress.value = 0.0
    }

}