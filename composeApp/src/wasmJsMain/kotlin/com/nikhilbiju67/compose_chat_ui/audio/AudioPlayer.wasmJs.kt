package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import models.AudioMessage
import org.w3c.dom.HTMLAudioElement

actual class AudioPlayer actual constructor(
    private val playerState: PlayerState,
    private val onProgressCallback: (PlayerState) -> Unit
) {
    private val audioElement = document.createElement("audio") as HTMLAudioElement
    private var currentUrl: String? = null
    private val _playerState = MutableStateFlow(playerState)
    private var currentPlaying: AudioMessage? = null
    actual fun play(message: AudioMessage) {
        val url = message.effectiveAudioSource
        if (url == null) {
            return
        }
        currentPlaying = message
        _playerState.update {
            it.copy(
                isPlaying = true,
                currentlyPlayingId = currentPlaying?.id
            )
        }
        setupListeners()
        if (url != currentUrl) {
            currentUrl = url
            audioElement.src = if (isLocalFile(url)) "file://$url" else url
        }
        audioElement.play()

        onProgressCallback(_playerState.value)
    }

    private fun setupListeners() {
        audioElement.addEventListener("timeupdate", {

            _playerState.value= _playerState.value.copy(
                currentTime = audioElement.currentTime.toInt(),
                duration = audioElement.duration.toInt(),
                isPlaying = _playerState.value.isPlaying,
                currentlyPlayingId = currentPlaying?.id,
                isBuffering = false)

            onProgressCallback(_playerState.value)

        })

        audioElement.addEventListener("ended", {
            currentUrl = null
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                currentTime = 0,
                duration = 0,
                currentlyPlayingId = null
            )
            onProgressCallback(_playerState.value)
        })

        audioElement.addEventListener("error", {
            playerState.isPlaying = false
            // Handle playback error if needed
        })
    }

    actual fun pause() {
        audioElement.pause()
        _playerState.value= _playerState.value.copy(
            isPlaying = false,
            isBuffering = false
        )

        onProgressCallback(_playerState.value)
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
