@file:OptIn(ExperimentalForeignApi::class)

package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
import platform.AVFoundation.isPlaybackLikelyToKeepUp
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
import platform.darwin.Float64
import platform.darwin.NSEC_PER_SEC
import kotlin.time.DurationUnit
import kotlin.time.toDuration

actual class AudioPlayer actual constructor(private val playerState: PlayerState) {
    private val avAudioPlayer: AVPlayer = AVPlayer()
    private lateinit var timeObserver: Any
    private val _mediaStatus = MutableStateFlow(MediaStatus())
    actual val mediaStatus: Flow<MediaStatus> get() = _mediaStatus.asStateFlow()

    @OptIn(ExperimentalForeignApi::class)
    private val observer: (CValue<CMTime>) -> Unit = { time: CValue<CMTime> ->
        val currentTimeInSeconds = CMTimeGetSeconds(time)
        val totalDurationInSeconds =
            avAudioPlayer.currentItem?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0

        if (!totalDurationInSeconds.isNaN() && totalDurationInSeconds > 0) {
            val progress = (currentTimeInSeconds / totalDurationInSeconds).toFloat()
            playerState.currentTime = currentTimeInSeconds.toLong()
            playerState.duration = totalDurationInSeconds.toLong()

            _mediaStatus.update {
                it.copy(
                    audioProgress = progress,
                    isPlaying = playerState.isPlaying
                )
            }
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
        playerState.isPlaying = false
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun seekTo(time: Double) {
        playerState.isBuffering = true
        val cmTime = CMTimeMakeWithSeconds(time, NSEC_PER_SEC.toInt())
        avAudioPlayer.currentItem?.seekToTime(time = cmTime) {
            playerState.isBuffering = false
        }
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
                playerState.isPlaying = false
                _mediaStatus.update {
                    it.copy(
                        playerState = playerState,
                        isPlaying = false,
                        audioProgress = 1f // Set to 100% at the end of playback
                    )
                }
            }
        )
    }

    private fun stop() {
        if (::timeObserver.isInitialized) avAudioPlayer.removeTimeObserver(timeObserver)
        avAudioPlayer.pause()
        _mediaStatus.value = MediaStatus(
            isPlaying = false,
            playerState = playerState,
            audioProgress = 0f
        )
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

