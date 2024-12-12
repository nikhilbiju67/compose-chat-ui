package com.nikhilbiju67.compose_chat_ui

//import com.nikhilbiju67.compose_chat_ui.audio.AudioPlayer
//import com.nikhilbiju67.compose_chat_ui.audio.rememberPlayerState
//import com.nikhilbiju67.compose_chat_ui.styles.MessageBubbleStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nikhilbiju67.compose_chat_ui.audio.AudioPlayer
import com.nikhilbiju67.compose_chat_ui.audio.rememberPlayerState
import com.nikhilbiju67.compose_chat_ui.styles.defaultBubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.defaultChatStyle
import composechatui.composeapp.generated.resources.Res
import composechatui.composeapp.generated.resources.pause_circle_24px
import composechatui.composeapp.generated.resources.play_circle_24px
import kotlinx.coroutines.flow.Flow
import models.AudioMessage
import models.MediaStatus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {

    var messages by remember {
        mutableStateOf(dummyData)
    }

    val playerState = rememberPlayerState()
    val audioPlayer = remember { AudioPlayer(playerState) }
    var currentlyPlaying by remember { mutableStateOf<AudioMessage?>(null) }
    val mediaStatus: MediaStatus = audioPlayer.mediaStatus.collectAsState(MediaStatus()).value
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        ChatView(
            messages,
            onSend = {
                messages = messages.copy(messages = listOf(it) + messages.messages)
                print(messages.toString())
            },
            modifier = Modifier.background(Color.White),
            chatStyle = defaultChatStyle.copy(
                messageBubbleStyle = defaultBubbleStyle.copy(
                    audioBubble = { audioMessage ->
                        AudioMessage(audioMessage, (mediaStatus), onPlayAudio = {
                            currentlyPlaying = audioMessage
                            audioPlayer.play(audioMessage)
                        },
                            currentlyPlayingAudioId = currentlyPlaying?.id,
                            onPauseAudio = {
                                audioPlayer.pause()
                            })
                    }
                )

            )

        )

    }
}


@Composable
fun AudioMessage(
    audioMessage: AudioMessage,
    mediaStatus: MediaStatus,
    currentlyPlayingAudioId: String?,
    onPlayAudio: () -> Unit,
    onPauseAudio: () -> Unit
) {
    val audioPlaying by derivedStateOf {
        mediaStatus.playerState?.isPlaying == true && currentlyPlayingAudioId == audioMessage.id
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 24.dp)
            .fillMaxWidth()
    ) {
        IconButton(

            modifier = Modifier.size(32.dp),
            onClick = {
                if (audioPlaying) {
                    onPauseAudio()
                } else {
                    (audioMessage.effectiveAudioSource)?.let {
                        onPlayAudio()
                    }
                }
            }) {
            Icon(
                painter = painterResource(if (audioPlaying) Res.drawable.pause_circle_24px else Res.drawable.play_circle_24px),
                contentDescription = null
            )
        }


        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            LinearProgressIndicator(
                progress = if (audioPlaying) ((mediaStatus.playerState?.progressPercentage
                    ?: 0) / 100f) else 0f,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Blue
            )

        }
    }
    Text(
        if (!audioPlaying) "0.0" else "${mediaStatus.playerState?.currentTime ?: "0"}s / ${mediaStatus.playerState?.duration ?: "0"}s",
        style = MaterialTheme.typography.body2
    )
}
