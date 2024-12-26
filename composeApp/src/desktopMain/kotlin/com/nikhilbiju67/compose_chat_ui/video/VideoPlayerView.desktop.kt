package com.nikhilbiju67.compose_chat_ui.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import composechatui.composeapp.generated.resources.Res
import composechatui.composeapp.generated.resources.pause_circle_24px
import composechatui.composeapp.generated.resources.play_circle_24px
import kotlinx.coroutines.delay
import models.VideoMessage
import org.jetbrains.compose.resources.painterResource
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.Component
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.roundToLong

@Composable
actual fun VideoPlayerView(modifier: Modifier, videoMessage: VideoMessage) {
    val mediaPlayerComponent = remember { initializeMediaPlayerComponent() }
    val mediaPlayer = remember { mediaPlayerComponent.mediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0f) }
    var totalDuration by remember { mutableStateOf(0f) }
    var formattedCurrentPosition by remember { mutableStateOf("00:00") }
    var formattedTotalDuration by remember { mutableStateOf("00:00") }
    var showVideoPlayer by remember { mutableStateOf(false) }

    val factory = remember { { mediaPlayerComponent } }

    /// Prepare the media player and fetch total video duration.
    LaunchedEffect(videoMessage.url) {
        mediaPlayer.media().prepare(videoMessage.url)
        delay(500)
        totalDuration = mediaPlayer.status().length().toFloat()
        formattedTotalDuration = formatTime(totalDuration)
    }

    /// Release resources when the composable is disposed.
    DisposableEffect(Unit) { onDispose(mediaPlayer::release) }

    /// Update the current video playback position when playing.
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = mediaPlayer.status().time().toFloat()
            formattedCurrentPosition = formatTime(currentPosition)
            delay(1000)
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.Bottom) {
        if (showVideoPlayer) SwingPanel(
            factory = factory,
            background = Color.Transparent,
            modifier = Modifier.weight(1f).fillMaxWidth()
        )

        /// Video control UI.
        VideoControls(
            modifier = Modifier.fillMaxWidth().height(30.dp),
            isPlaying = isPlaying,
            isMuted = isMuted,
            currentPosition = currentPosition,
            totalDuration = totalDuration,
            formattedCurrentPosition = formattedCurrentPosition,
            formattedTotalDuration = formattedTotalDuration,
            onPlayPauseToggle = {
                showVideoPlayer = true
                isPlaying = !isPlaying
                if (isPlaying) mediaPlayer.controls().play() else mediaPlayer.controls().pause()
            },
            onMuteToggle = {
                isMuted = !isMuted
                mediaPlayer.audio().setMute(isMuted)
            },
            onSeek = { position ->
                val time = (position * totalDuration) / 100
                mediaPlayer.controls().setTime(time.roundToLong())
            },
            onFullScreen = {
                mediaPlayer.fullScreen().set(true)
            }
        )
    }

}

@Composable
fun VideoControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isMuted: Boolean,
    currentPosition: Float,
    totalDuration: Float,
    formattedCurrentPosition: String,
    formattedTotalDuration: String,
    onPlayPauseToggle: () -> Unit,
    onMuteToggle: () -> Unit,
    onSeek: (Float) -> Unit,
    onFullScreen: () -> Unit
) {
    Column(modifier = modifier.padding(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPlayPauseToggle) {
                Icon(
                    painter = painterResource(if (isPlaying) Res.drawable.pause_circle_24px else Res.drawable.play_circle_24px),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            Spacer(Modifier.weight(1f))
//            IconButton(onClick = onFullScreen) {
//                Icon(
//                    painter = painterResource(Res.drawable.fullscreen_24px),
//                    contentDescription = "Full screen"
//                )
//            }
        }


    }
}

private fun Component.mediaPlayer() = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

fun initializeMediaPlayerComponent(): Component {
    NativeDiscovery().discover()
    return if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }
}

private fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}

fun formatTime(value: Float): String {
    val df = DecimalFormat("00")
    val minutes = (value / (1000 * 60)).toInt()
    val seconds = ((value / 1000) % 60).toInt()
    return "${df.format(minutes)}:${df.format(seconds)}"
}