package com.nikhilbiju67.compose_chat_ui

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "composechatui",
    ) {
        App()
    }
}