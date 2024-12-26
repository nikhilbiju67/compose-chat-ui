package com.nikhilbiju67.compose_chat_ui.audio

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.nikhilbiju67.ChatAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import models.AudioMessage
import java.io.File

actual class AudioPlayer actual constructor(
    private val playerState: PlayerState,
    private val onProgressCallback: (PlayerState) -> Unit
) {
    private var mediaPlayer: ExoPlayer = ExoPlayer.Builder(ChatAndroidApp.appContext).build()
    private val mediaItems = mutableListOf<MediaItem>()
    private var currentItemIndex = -1
    private val _playerState = MutableStateFlow(playerState)
    var currentlyPlaying: AudioMessage? = null

    private var progressJob: Job? = null

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> {}
                Player.STATE_BUFFERING -> {
                    playerState.isBuffering = true
                    updateMediaStatus()
                }

                Player.STATE_READY -> {
                    playerState.isBuffering = false
                    playerState.duration = (mediaPlayer.duration / 1000).toInt()
                    updateMediaStatus()
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playerState.isPlaying = isPlaying
            updateMediaStatus()
            if (isPlaying) startProgressUpdates() else stopProgressUpdates()
        }
    }

    init {
        mediaPlayer.addListener(listener)
    }

    actual fun play(message: AudioMessage) {
        var url = message.effectiveAudioSource
        if (url == null) {
            return
        }
        val mediaItem = if (isLocalFile(url)) {
            MediaItem.fromUri("file://$url")
        } else {
            MediaItem.fromUri(url)
        }
        currentlyPlaying = message

        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare()
        mediaPlayer.play()

        playerState.isPlaying = true
        playerState.duration = (mediaPlayer.duration / 1000).toInt()
        updateMediaStatus()
    }

    actual fun pause() {
        mediaPlayer.pause()
        playerState.isPlaying = false
        updateMediaStatus()
    }

    actual fun cleanUp() {
        mediaPlayer.release()
        mediaPlayer.removeListener(listener)
        stopProgressUpdates()
    }


    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (playerState.isPlaying) {
                val progressInPercentage =
                    mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                _playerState.value = _playerState.value.copy(
                    currentTime = (mediaPlayer.currentPosition / 1000).toInt(),
                    currentlyPlayingId = currentlyPlaying?.id,
                    isPlaying = playerState.isPlaying,
                    isBuffering = playerState.isBuffering,
                    duration = playerState.duration
                )
                onProgressCallback(_playerState.value)
                delay(10)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun isLocalFile(path: String): Boolean {
        return File(path).exists()
    }

    actual fun playerState(): PlayerState {
        return playerState
    }

    private fun updateMediaStatus() {
       _playerState.value = _playerState.value.copy(
            currentTime = (mediaPlayer.currentPosition / 1000).toInt(),
            currentlyPlayingId = currentlyPlaying?.id,
            isPlaying = playerState.isPlaying,
            isBuffering = playerState.isBuffering,
            duration = playerState.duration
        )
        onProgressCallback(playerState)
    }
}


