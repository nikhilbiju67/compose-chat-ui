package com.nikhilbiju67.compose_chat_ui.components.message_bubble

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nikhilbiju67.compose_chat_ui.video.PlayStatus
import com.nikhilbiju67.compose_chat_ui.video.VideoControlStatus
import com.nikhilbiju67.compose_chat_ui.video.VideoPlayerView
import models.VideoMessage

@Composable
fun VideoMessageBubble(videoMessage: VideoMessage) {

    Box(contentAlignment = Alignment.Center) {
        VideoPlayerView(
            Modifier
                .width(300.dp)
                .height(200.dp),
            videoMessage = videoMessage,
        )

    }
}
