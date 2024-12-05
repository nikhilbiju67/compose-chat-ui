package com.nikhilbiju67.compose_chat_ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform