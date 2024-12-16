package com.nikhilbiju67.compose_chat_ui.audio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val MICROPHONE_PERMISSION_REQUEST_CODE = 101

@Suppress("TooGenericExceptionCaught", "SwallowedException")
actual class AudioRecord {
    private var recorder: MediaRecorder? = null
    private var output: String? = null
    private var recordingState: RecordingState = RecordingState.IDLE
    actual internal suspend fun stopRecording(): String {
        recordingState = RecordingState.IDLE
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return output.also {
            output = null
            recorder = null
        } ?: ""
    }

    internal actual fun isRecording(): Boolean {
        return recordingState == RecordingState.RECORDING

    }


    internal actual fun startRecording(
        config: RecordConfig,
        onPermissionResult: (Boolean) -> Unit
    ) {

        // Permission is granted, proceed with recording
        output = config.getOutput()
        recorder = createMediaRecorder(config)

        recorder?.apply {
            runCatching {
                prepare()
            }.onFailure {
                // Handle preparation failure
                it.printStackTrace()
            }

            setOnErrorListener { _, _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    stopRecording()
                }
            }

            start()
            recordingState = RecordingState.RECORDING
        }
    }


    private fun createMediaRecorder(config: RecordConfig): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder()
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(config.outputFormat.toMediaRecorderOutputFormat())
            setOutputFile(output)
            setAudioEncoder(config.audioEncoder.toMediaRecorderAudioEncoder())
        }

    private fun OutputFormat.toMediaRecorderOutputFormat(): Int = when (this) {
        OutputFormat.MPEG_4 -> MediaRecorder.OutputFormat.MPEG_4
    }

    private fun AudioEncoder.toMediaRecorderAudioEncoder(): Int = when (this) {
        AudioEncoder.AAC -> MediaRecorder.AudioEncoder.AAC
    }

    private fun RecordConfig.getOutput(): String {
        val fileName = "${System.currentTimeMillis()}${outputFormat.extension}"
        return when (this.outputLocation) {
            OutputLocation.Cache -> "${System.getProperty("java.io.tmpdir")}/$fileName"
            OutputLocation.Internal -> "${System.getProperty("user.dir")}/$fileName"
            is OutputLocation.Custom -> "${this.outputLocation.path}/$fileName"
        }
    }

    @Composable
    actual fun RequestMicrophonePermission(
        onPermissionResult: (Boolean) -> Unit,
        content: @Composable (requestPermission: () -> Unit) -> Unit
    ) {
        // Remember the launcher for the permission request
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                onPermissionResult(isGranted)
            }
        )

        // Pass a callable lambda to the content
        content {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

}


