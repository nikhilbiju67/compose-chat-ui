package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.browser.document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import models.AudioMessage
import models.MediaStatus
import org.w3c.dom.HTMLAudioElement

actual class AudioPlayer actual constructor(private val playerState: PlayerState) {
    private val audioElement = document.createElement("audio") as HTMLAudioElement
    private var currentUrl: String? = null
    private val _mediaStatus = MutableStateFlow(MediaStatus())
    actual val mediaStatus: Flow<MediaStatus> get() = _mediaStatus.asStateFlow()
    actual fun play(message: AudioMessage) {
        val url = message.effectiveAudioSource
        if(url == null) {
            return
        }
        setupListeners()
        if (url != currentUrl) {
            currentUrl = url
            audioElement.src = if (isLocalFile(url)) "file://$url" else url
        }
        audioElement.play()
        playerState.isPlaying = true
    }

    private fun setupListeners() {
        audioElement.addEventListener("timeupdate", {
            playerState.currentTime = audioElement.currentTime.toLong()
            playerState.duration = audioElement.duration.toLong()
            //get progress percentage
            val progress = (audioElement.currentTime / audioElement.duration)
            _mediaStatus.update {
                it.copy(
                    isPlaying = playerState.isPlaying,
                    audioProgress = progress.toFloat(),
                    playerState = playerState
                )
            }

        })

        audioElement.addEventListener("ended", {
            playerState.isPlaying = false
            currentUrl = null
        })

        audioElement.addEventListener("error", {
            playerState.isPlaying = false
            // Handle playback error if needed
        })
    }

    actual fun pause() {
        audioElement.pause()
        playerState.isPlaying = false
    }

    actual fun seekTo(time: Double) {
        audioElement.currentTime = time
    }

    actual fun cleanUp() {
        audioElement.pause()
        audioElement.src = ""
        currentUrl = null
    }

    private fun isLocalFile(path: String): Boolean {
        // This implementation assumes you have a way to determine if a path is local
        return path.startsWith("/") || path.startsWith("file://")
    }

    actual fun playerState(): PlayerState {
        return playerState
    }


}
