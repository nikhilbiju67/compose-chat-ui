package com.nikhilbiju67.compose_chat_ui.audio

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.nikhilbiju67.ChatAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

actual class AudioPlayer actual constructor(private val playerState: PlayerState) {
    private var mediaPlayer :ExoPlayer =  ExoPlayer.Builder(ChatAndroidApp.appContext).build()
    private val mediaItems = mutableListOf<MediaItem>()
    private var currentItemIndex = -1

    private val _audioProgress = MutableStateFlow(0.0)
    actual val audioProgress: Flow<Double> get() = _audioProgress.asStateFlow()

    private var progressJob: Job? = null

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> {}
                Player.STATE_BUFFERING -> playerState.isBuffering = true
                Player.STATE_ENDED -> if (playerState.isPlaying) next()
                Player.STATE_READY -> {
                    playerState.isBuffering = false
                    playerState.duration = mediaPlayer.duration / 1000
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playerState.isPlaying = isPlaying
            if (isPlaying) startProgressUpdates() else stopProgressUpdates()
        }
    }

    init {
        mediaPlayer.addListener(listener)
    }

    actual fun play() {
        if (currentItemIndex == -1) {
            play(0)
        } else {
            mediaPlayer.play()
        }
    }

    actual fun pause() {
        mediaPlayer.pause()
    }

    actual fun addSongsUrls(songsUrl: List<String>) {
        mediaItems += songsUrl.map { MediaItem.fromUri(it) }
        mediaPlayer.prepare()
    }

    actual fun next() {
        playerState.canNext = (currentItemIndex + 1) < mediaItems.size
        if (playerState.canNext) {
            currentItemIndex += 1
            playWithIndex(currentItemIndex)
        }
    }

    actual fun prev() {
        when {
            playerState.currentTime > 3 -> seekTo(0.0)
            else -> {
                playerState.canPrev = (currentItemIndex - 1) >= 0
                if (playerState.canPrev) {
                    currentItemIndex -= 1
                    playWithIndex(currentItemIndex)
                }
            }
        }
    }

    actual fun play(songIndex: Int) {
        if (songIndex < mediaItems.size) {
            currentItemIndex = songIndex
            playWithIndex(currentItemIndex)
        }
    }

    actual fun seekTo(time: Double) {
        val seekTime = time * 1000
        mediaPlayer.seekTo(seekTime.toLong())
    }

    actual fun cleanUp() {
        mediaPlayer.release()
        mediaPlayer.removeListener(listener)
        stopProgressUpdates()
    }

    private fun playWithIndex(index: Int) {
        playerState.currentItemIndex = index
        val playItem = mediaItems[index]
        mediaPlayer.setMediaItem(playItem)
        mediaPlayer.play()
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (playerState.isPlaying) {
                _audioProgress.value = mediaPlayer.currentPosition / 1000.0
                playerState.currentTime = _audioProgress.value.toLong()
                playerState.duration = mediaPlayer.duration / 1000
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    actual fun playerState(): PlayerState {
        return playerState
    }
}
