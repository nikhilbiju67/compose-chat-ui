@file:OptIn(ExperimentalForeignApi::class)

package com.nikhilbiju67.compose_chat_ui.audio

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val playerItems = mutableListOf<AVPlayerItem>()
    private val avAudioPlayer: AVPlayer = AVPlayer()
    private var currentItemIndex = -1
    private lateinit var timeObserver: Any
    private val _audioProgress = MutableStateFlow(0.0)
    actual val audioProgress: Flow<Double> get() = _audioProgress.asStateFlow()

    @OptIn(ExperimentalForeignApi::class)
    private val observer: (CValue<CMTime>) -> Unit = { time: CValue<CMTime> ->
        playerState.isBuffering = avAudioPlayer.currentItem?.isPlaybackLikelyToKeepUp() != true
        playerState.isPlaying = avAudioPlayer.timeControlStatus == AVPlayerTimeControlStatusPlaying
        val rawTime: Float64 = CMTimeGetSeconds(time)
        val parsedTime = rawTime.toDuration(DurationUnit.SECONDS).inWholeSeconds
        playerState.currentTime = parsedTime
        _audioProgress.value = parsedTime.toDouble()
        if (avAudioPlayer.currentItem != null) {
            val cmTime = CMTimeGetSeconds(avAudioPlayer.currentItem!!.duration)
            playerState.duration = if (cmTime.isNaN()) 0 else cmTime.toDuration(DurationUnit.SECONDS).inWholeSeconds
        }
    }

    init {
        setUpAudioSession()
        playerState.isPlaying = avAudioPlayer.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    actual fun play() {
        if (currentItemIndex == -1) {
            play(0)
        } else {
            avAudioPlayer.play()
            playerState.isPlaying = true
        }
    }

    actual fun pause() {
        avAudioPlayer.pause()
        playerState.isPlaying = false
    }

    actual fun play(songIndex: Int) {
        playerState.isBuffering = true
        if (songIndex < playerItems.size) {
            currentItemIndex = songIndex
            playWithIndex(currentItemIndex)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun seekTo(time: Double) {
        playerState.isBuffering = true
        val cmTime = CMTimeMakeWithSeconds(time, NSEC_PER_SEC.toInt())
        avAudioPlayer.currentItem?.seekToTime(time = cmTime) {
            playerState.isBuffering = false
        }
    }

    actual fun next() {
        playerState.canNext = (currentItemIndex + 1) < playerItems.size
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

    actual fun addSongsUrls(songsUrl: List<String>) {
        val converted = songsUrl.map {
            NSURL.URLWithString(it)!!
        }
        playerItems.addAll(converted.map { AVPlayerItem(uRL = it) })
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
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = avAudioPlayer.addPeriodicTimeObserverForInterval(interval, null, observer)
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = avAudioPlayer.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { next() }
        )
    }

    private fun stop() {
        if (::timeObserver.isInitialized) avAudioPlayer.removeTimeObserver(timeObserver)
        avAudioPlayer.pause()
        avAudioPlayer.currentItem?.seekToTime(CMTimeMakeWithSeconds(0.0, NSEC_PER_SEC.toInt()))
    }

    private fun playWithIndex(currentItemIndex: Int) {
        stop()
        startTimeObserver()
        playerState.isBuffering = true
        playerState.currentItemIndex = currentItemIndex
        val playItem = playerItems[currentItemIndex]
        avAudioPlayer.replaceCurrentItemWithPlayerItem(playItem)
        avAudioPlayer.play()
    }

    actual fun cleanUp() {
        stop()
    }

    actual fun playerState(): PlayerState {
        return playerState
    }
}
