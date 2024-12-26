// In wasmJs/kotlin/.../VideoPlayer.kt

package com.nikhilbiju67.compose_chat_ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.dom.appendElement
import models.VideoMessage
import org.w3c.dom.HTMLVideoElement

// Actual Composable function for wasmJS target
@Composable
actual fun VideoPlayerView(
    modifier: Modifier,
    videoMessage: VideoMessage,
) {

    var videoElement = remember { mutableStateOf<HTMLVideoElement?>(null) }



    Column(modifier = modifier.width(300.dp).height(200.dp)) {
        Box(contentAlignment = Alignment.Center) {
            HtmlView(
                modifier = Modifier.width(300.dp).height(200.dp).zIndex(-100f),
                factory = {
                    //                video.value = document.createElement("video") as HTMLVideoElement
                    //                // No 'controls' attribute added
                    //                video.value!!.setAttribute("preload", "auto")
                    //                video.value!!.setAttribute("width", "100%")
                    //                video.value!!.appendElement("source") {
                    //                    videoMessage.url?.let { setAttribute("src", it) }
                    //                    setAttribute("type", "video/mp4")
                    //                }

                    // No Video.js class
                    // video.className = "video-js"

                    // Store the video element in the state


                    val video = createElement("video") as HTMLVideoElement
                    video.setAttribute("preload", "auto")
                    video.setAttribute("controls", "false")
                    video.setAttribute("data-setup", "{}")
                    video.setAttribute("width", "100%") // Set the width here
                    video.appendElement("source") {
                        videoMessage.url?.let { setAttribute("src", it) }
                        setAttribute("type", "video/mp4")
                    }
                    //                videoElement.value=video
                    video
                    //                video.className = "video-js"

                }
            )
            Box(Modifier.width(100.dp).height(100.dp).background(Color.Red))
        }

        // ... Other Controls (Volume, Fullscreen, etc.)
    }

    // Start playback
    LaunchedEffect(videoMessage) {
//        videoPlayer.play(videoMessage)
    }
}