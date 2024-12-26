@file:OptIn(ExperimentalForeignApi::class)

package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import models.AudioMessage
import models.MediaStatus
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSEC_PER_SEC

actual class AudioPlayer actual constructor(
    private val playerState: PlayerState,
    private val onProgressCallback: (PlayerState) -> Unit
) {
    private val avAudioPlayer: AVPlayer = AVPlayer()
    private lateinit var timeObserver: Any
    private val _mediaStatus = MutableStateFlow(MediaStatus())
    private val _playerState = MutableStateFlow(playerState)
    private var currentPlaying: AudioMessage? = null

    @OptIn(ExperimentalForeignApi::class)
    private val observer: (CValue<CMTime>) -> Unit = { time: CValue<CMTime> ->
        val currentTimeInSeconds = CMTimeGetSeconds(time)
        val totalDurationInSeconds =
            avAudioPlayer.currentItem?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0

        if (!totalDurationInSeconds.isNaN() && totalDurationInSeconds > 0) {
            val progress = (currentTimeInSeconds / totalDurationInSeconds).toFloat()

            _playerState.value = _playerState.value.copy(
                currentTime = currentTimeInSeconds.toInt(),
                duration = totalDurationInSeconds.toInt(),
                currentlyPlayingId = currentPlaying?.id,
                isPlaying = playerState.isPlaying,
                isBuffering = playerState.isBuffering
            )
            onProgressCallback(_playerState.value)

        }
    }

    init {
        setUpAudioSession()
        playerState.isPlaying = avAudioPlayer.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    actual fun play(message: AudioMessage) {
        val url = message.effectiveAudioSource
        if (url == null) {
            println("Error: No valid audio source provided.")
            return
        }
        currentPlaying = message

        playerState.isBuffering = true

        stop()
        startTimeObserver()

        val nsUrl = NSURL.URLWithString(url) ?: throw IllegalArgumentException("Invalid URL")
        val playItem = AVPlayerItem(uRL = nsUrl)
        avAudioPlayer.replaceCurrentItemWithPlayerItem(playItem)
        avAudioPlayer.play()
        playerState.isPlaying = true
    }

    actual fun pause() {
        avAudioPlayer.pause()
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            isBuffering = false
        )
        onProgressCallback(_playerState.value)
    }

    private fun setUpAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (e: Exception) {
            println("Error setting up audio session: ${e.message}")
        }
    }

    private fun startTimeObserver() {
        val interval = CMTimeMakeWithSeconds(0.1, NSEC_PER_SEC.toInt())
        timeObserver = avAudioPlayer.addPeriodicTimeObserverForInterval(interval, null, observer)
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = avAudioPlayer.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    isBuffering = false
                )
                onProgressCallback(_playerState.value)
            }
        )
    }

    private fun stop() {
        if (::timeObserver.isInitialized) avAudioPlayer.removeTimeObserver(timeObserver)
        avAudioPlayer.pause()
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            isBuffering = false,
            currentTime = 0
        )
        onProgressCallback(_playerState.value)
        avAudioPlayer.currentItem?.seekToTime(CMTimeMakeWithSeconds(0.0, NSEC_PER_SEC.toInt()))
    }

    actual fun cleanUp() {
        stop()
        _mediaStatus.value = MediaStatus(
            isPlaying = false,
            audioProgress = 0f,
            playerState = playerState
        )
    }

    actual fun playerState(): PlayerState {
        return playerState
    }
}

