package com.nikhilbiju67.compose_chat_ui.audio


import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// External interface for our JavaScript wrapper
external object audioRecorderJS {
    fun startRecording(mimeType: String): Boolean
    fun stopRecording(): String?
    fun isRecording(): Boolean
}

actual class AudioRecord actual constructor() {

    internal actual fun isRecording(): Boolean {
        return audioRecorderJS.isRecording()
    }

    internal actual fun startRecording(
        config: RecordConfig,
        onPermissionResult: (Boolean) -> Unit
    ) {
        val mimeType = when (config.outputFormat) {
            OutputFormat.MPEG_4 -> "audio/mp4"
            // Add other formats as needed
            else -> ""
        }

        CoroutineScope(Dispatchers.Main).launch {
            audioRecorderJS.startRecording(mimeType)
        }
    }

    actual internal fun stopRecording(): String {
        val url = audioRecorderJS.stopRecording()
        return url ?: ""
    }

    @Composable
    actual fun RequestMicrophonePermission(
        onPermissionResult: (Boolean) -> Unit,
        content: @Composable (invokeParentFunction: () -> Unit) -> Unit
    ) {
        content {
            onPermissionResult(true)
        }
    }


}

