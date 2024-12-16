package com.nikhilbiju67.compose_chat_ui.audio

import androidx.compose.runtime.Composable
import com.nikhilbiju67.compose_chat_ui.audio.audioRecorderJS.consoleLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlin.js.Promise

// External interface for our JavaScript wrapper
external object audioRecorderJS {
    fun startRecording(mimeType: String): Boolean
    fun stopRecording(): Promise<JsAny?>
    fun isRecording(): Boolean
    fun consoleLog(message: String)
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

    @OptIn(DelicateCoroutinesApi::class)
    actual internal suspend fun stopRecording(): String { // Use runBlocking to wait for the promise
        val result = audioRecorderJS.stopRecording().await()  as JsAny?

        val url = when (result) {
//            is Blob -> URL.createObjectURL(result)
            is JsString -> {
                result.toString()
            }
            null -> ""
            else -> {
                consoleLog("Unexpected type returned from stopRecording():${result} ${result::class}")
                ""
            }
        }
        consoleLog("AudioRecord.stopRecording() url: $url")

        return url
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

