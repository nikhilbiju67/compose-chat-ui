package com.nikhilbiju67.compose_chat_ui.video

import com.nikhilbiju67.compose_chat_ui.audio.PlayerState
import kotlinx.browser.document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import models.MediaStatus
import models.VideoMessage
import org.w3c.dom.HTMLVideoElement

actual class VideoPlayer actual constructor(private val playerState: PlayerState) {

    private val videoElement = document.createElement("video") as HTMLVideoElement
    private var currentUrl: String? = null
    private val _mediaStatus = MutableStateFlow(MediaStatus())
    actual val mediaStatus: Flow<MediaStatus> get() = _mediaStatus.asStateFlow()

    init {
        // Disable default controls to build custom UI
        videoElement.controls = false
    }

    actual fun play(message: VideoMessage) {
        val url = message.effectiveVideoSource
        if (url == null) {
            return
        }
        setupListeners()
        if (url != currentUrl) {
            currentUrl = url
            videoElement.src = if (isLocalFile(url)) "file://$url" else url
        }

        videoElement.play()
        playerState.isPlaying = true
    }

    private fun setupListeners() {
        videoElement.addEventListener("timeupdate", {
            playerState.currentTime = videoElement.currentTime.toInt()
            playerState.duration = videoElement.duration.toInt()
            val progress = (videoElement.currentTime / videoElement.duration)




            _mediaStatus.update {
                it.copy(
                    isPlaying = playerState.isPlaying,
                    audioProgress = progress.toFloat(),
                    playerState = playerState
                )
            }
        })

        videoElement.addEventListener("ended", {
            playerState.isPlaying = false
            currentUrl = null
        })

        videoElement.addEventListener("error", {
            playerState.isPlaying = false
            // Handle playback error if needed
        })

        videoElement.addEventListener("loadedmetadata", {
            playerState.duration = videoElement.duration.toInt()
            _mediaStatus.update {
                it.copy(playerState = playerState)
            }
        })
    }

    actual fun pause() {
        videoElement.pause()
        playerState.isPlaying = false
    }

    actual fun seekTo(time: Double) {
        videoElement.currentTime = time
    }

    actual fun cleanUp() {
        videoElement.pause()
        videoElement.src = ""
        currentUrl = null
    }

    private fun isLocalFile(path: String): Boolean {
        return path.startsWith("/") || path.startsWith("file://")
    }

    actual fun playerState(): PlayerState {
        return playerState
    }
}