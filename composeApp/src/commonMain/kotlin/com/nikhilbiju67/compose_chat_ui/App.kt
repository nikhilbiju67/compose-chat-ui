package com.nikhilbiju67.compose_chat_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
//import com.nikhilbiju67.compose_chat_ui.audio.AudioPlayer
//import com.nikhilbiju67.compose_chat_ui.audio.rememberPlayerState
//import com.nikhilbiju67.compose_chat_ui.styles.MessageBubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.defaultBubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.defaultChatStyle
import composechatui.composeapp.generated.resources.Res
import composechatui.composeapp.generated.resources.pause_circle_24px
import composechatui.composeapp.generated.resources.play_circle_24px
import models.AudioMessage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {

    var messages by remember {
        mutableStateOf(dummyData)
    }
    val playerState = rememberPlayerState()
    val player = remember { AudioPlayer(playerState) }
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
                        AudioMessage(audioMessage, audioPlayer = player)
                    }
                )

            )

        )

    }
}


@Composable
fun AudioMessage(audioMessage: AudioMessage, audioPlayer: AudioPlayer) {
    val audioProgress by audioPlayer.audioProgress.collectAsState(initial = 0.0)
    val playerState = remember { audioPlayer.playerState() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(onClick = {
            if (playerState.isPlaying) {
                audioPlayer.pause()
            } else {
                audioPlayer.addSongsUrls(listOf(audioMessage.messageContent))
                audioPlayer.play()
            }
        }) {
            Icon(
                painter = painterResource(if (playerState.isPlaying) Res.drawable.pause_circle_24px else Res.drawable.play_circle_24px),
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("${playerState.currentTime}s / ${playerState.duration}s")
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = playerState.progressPercentage / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Blue
            )
        }
    }
}
