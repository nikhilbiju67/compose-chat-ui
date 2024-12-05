package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.browser.document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.w3c.dom.HTMLAudioElement

actual class AudioPlayer actual constructor(private val playerState: PlayerState) {
    private val audioElement = document.createElement("audio") as HTMLAudioElement
    private val mediaItems = mutableListOf<String>()
    private var currentItemIndex = -1

    actual fun play() {
        setupListeners()
        if (currentItemIndex == -1 && mediaItems.isNotEmpty()) {
            play(0)
        } else {
            audioElement.play()
            playerState.isPlaying = true
        }
    }
    private fun setupListeners() {
        audioElement.addEventListener("timeupdate", {
            _audioProgress.value = audioElement.currentTime
            playerState.currentTime = audioElement.currentTime.toLong()
            playerState.duration = audioElement.duration.toLong()
        })

        audioElement.addEventListener("ended", {
            next()
        })

        audioElement.addEventListener("error", {
//            playerState.error = "An error occurred during playback."
        })
    }
    actual fun pause() {
        audioElement.pause()
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
            audioElement.src = mediaItems[songIndex]
            audioElement.play()
            playerState.isPlaying = true
            playerState.currentItemIndex = songIndex
        }
    }

    actual fun seekTo(time: Double) {
        audioElement.currentTime = time
    }

    actual fun addSongsUrls(songsUrl: List<String>) {
        mediaItems.clear()
        mediaItems.addAll(songsUrl)
        if (mediaItems.isNotEmpty()) {
            play(0)
        }
    }

    actual fun cleanUp() {
        audioElement.pause()
        audioElement.src = ""
        _audioProgress.value = 0.0
    }

    actual fun playerState(): PlayerState {
        return playerState
    }

    private val _audioProgress = MutableStateFlow(0.0)
    actual val audioProgress: Flow<Double> get() = _audioProgress.asStateFlow()

}