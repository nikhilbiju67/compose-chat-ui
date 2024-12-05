package com.nikhilbiju67.compose_chat_ui.components.message_bubble

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.AudioMessage
import org.jetbrains.compose.resources.painterResource

@Composable
fun AudioMessageBubble(
    audioMessage: AudioMessage,
    audioBubble: @Composable (AudioMessage) -> Unit
) {

    audioBubble(audioMessage)

}
