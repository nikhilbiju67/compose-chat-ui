// In commonMain/kotlin/.../VideoPlayer.kt

package com.nikhilbiju67.compose_chat_ui.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import models.VideoMessage

// Expect Composable function to display the video
@Composable
expect fun VideoPlayerView(
    modifier: Modifier = Modifier,
    videoMessage: VideoMessage,
)