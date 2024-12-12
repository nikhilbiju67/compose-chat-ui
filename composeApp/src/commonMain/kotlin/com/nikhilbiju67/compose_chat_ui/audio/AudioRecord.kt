package com.nikhilbiju67.compose_chat_ui.audio

import androidx.compose.runtime.Composable

expect  class AudioRecord() {
    internal fun stopRecording(): String
    internal fun isRecording(): Boolean
    internal fun startRecording(config: RecordConfig, onPermissionResult: (Boolean) -> Unit = {})
    @Composable
     fun RequestMicrophonePermission(onPermissionResult: (Boolean) -> Unit,  content: @Composable (invokeParentFunction: () -> Unit) -> Unit)

}
