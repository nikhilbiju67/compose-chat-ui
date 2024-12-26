package com.nikhilbiju67.compose_chat_ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.nikhilbiju67.compose_chat_ui.video.LocalLayerContainer
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    ComposeViewport(document.body!!) {
        CompositionLocalProvider(LocalLayerContainer provides document.body!!) {
            App()
        }
    }
}