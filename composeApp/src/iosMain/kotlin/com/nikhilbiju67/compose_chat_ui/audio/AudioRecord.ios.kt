package com.nikhilbiju67.compose_chat_ui.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioQuality
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptions
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVAudioSessionRecordPermissionDenied
import platform.AVFAudio.AVAudioSessionRecordPermissionUndetermined
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.CoreAudioTypes.AudioFormatID
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSURL.Companion.fileURLWithPath
import platform.Foundation.NSUserDomainMask
import platform.Foundation.temporaryDirectory
import kotlin.system.getTimeMillis
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted


// Constants
private const val SampleRate = 44100.0 // Standard sample rate for recording

// Exception classes
class RecordFailException(message: String = "Recording failed.") : Exception(message)
class PermissionMissingException(message: String = "Permission denied.") : Exception(message)
class NoOutputFileException(message: String = "No output file path.") : Exception(message)


// Enum for OutputFormat
//enum class OutputFormat(val extension: String) {
//    MPEG_4(".m4a");
//
//    fun toAVFormatID(): AudioFormatID = when (this) {
//        MPEG_4 -> kAudioFormatMPEG4AAC
//    }
//}


// AudioRecord class
actual class AudioRecord {
    private var recorder: AVAudioRecorder? = null
    private var output: String? = null
    private var isRecording: Boolean = false

    @OptIn(ExperimentalForeignApi::class)
    @Throws(RecordFailException::class)
    internal actual fun startRecording(
        config: RecordConfig,
        onPermissionResult: (Boolean) -> Unit
    ) {
        println(config.toString())

        try {
            checkPermission()
        } catch (e: PermissionMissingException) {
            onPermissionResult(false)
            throw e
        }

        onPermissionResult(true) // Proceed if permission is granted
        configureAudioSession()

        output = config.getOutput()

        val settings = mapOf<Any?, Any>(
            AVFormatIDKey to config.outputFormat.toAVFormatID(),
            AVSampleRateKey to SampleRate,
            AVNumberOfChannelsKey to 1,
            AVEncoderAudioQualityKey to AVAudioQuality.MAX_VALUE
        )

        val url = fileURLWithPath(output!!)
        recorder = AVAudioRecorder(
            url,
            settings,
            null
        )

        recorder?.let {
            if (!it.prepareToRecord()) {
                throw RecordFailException()
            }
            if (!it.record()) {
                throw RecordFailException()
            }
            isRecording = true
        } ?: throw RecordFailException()
    }


    internal actual fun stopRecording(): String {
        isRecording = false
        recorder?.stop()

        return output.also {
            output = null
            recorder = null
        } ?: throw NoOutputFileException()
    }

    internal actual fun isRecording(): Boolean = isRecording

    @OptIn(ExperimentalForeignApi::class)
    private fun configureAudioSession() {
        memScoped {
            val audioSession = AVAudioSession.sharedInstance()
            val categoryErrorPtr = alloc<ObjCObjectVar<NSError?>>()
            audioSession.setCategory(
                AVAudioSessionCategoryPlayAndRecord,
                withOptions = AVAudioSessionCategoryOptions.MAX_VALUE,
                error = categoryErrorPtr.ptr
            )
            val categoryError = categoryErrorPtr.value
            if (categoryError != null) {
                println("Failed to set AVAudioSession category: ${categoryError.localizedDescription}")
                throw RecordFailException()
            }

            val activateErrorPtr = alloc<ObjCObjectVar<NSError?>>()
            audioSession.setActive(true, error = activateErrorPtr.ptr)
            val activateError = activateErrorPtr.value
            if (activateError != null) {
                println("Failed to activate AVAudioSession: ${activateError.localizedDescription}")
                throw RecordFailException()
            }
        }
    }

    private fun checkPermission() {
        val audioSession = AVAudioSession.sharedInstance()
        when (audioSession.recordPermission()) {
            AVAudioSessionRecordPermissionDenied -> {
                throw PermissionMissingException()
            }

            AVAudioSessionRecordPermissionUndetermined -> {
                audioSession.requestRecordPermission { granted ->
                    if (!granted) {
                        throw PermissionMissingException()
                    }
                }
            }

            else -> Unit // Permission granted
        }
    }

    private fun RecordConfig.getOutput(): String {
        val timestamp = getTimeMillis().toString()
        val fileName = "${timestamp}${outputFormat.extension}"

        return when (this.outputLocation) {
            OutputLocation.Cache -> "${NSFileManager.defaultManager.temporaryDirectory.path}/$fileName"
            OutputLocation.Internal -> {
                val urls = NSFileManager.defaultManager.URLsForDirectory(
                    NSDocumentDirectory,
                    NSUserDomainMask
                )
                val documentsURL = urls.first() as? NSURL ?: throw NoOutputFileException()
                "${documentsURL.path!!}/$fileName"
            }

            is OutputLocation.Custom -> "${this.outputLocation.path}/$fileName"


        }
    }

    private fun OutputFormat.toAVFormatID(): AudioFormatID = when (this) {
        OutputFormat.MPEG_4 -> kAudioFormatMPEG4AAC
    }

    @Composable
    actual fun RequestMicrophonePermission(
        onPermissionResult: (Boolean) -> Unit,
        content: @Composable (invokeParentFunction: () -> Unit) -> Unit
    ) {
        val isPermissionGranted = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            val currentStatus = AVAudioSession.sharedInstance().recordPermission()
            if (currentStatus == AVAudioSessionRecordPermissionGranted) {
                isPermissionGranted.value = true
                onPermissionResult(true)
            } else {
                onPermissionResult(false)

            }
        }
        content {
            requestMicrophonePermission { granted ->
                isPermissionGranted.value = granted
                onPermissionResult(granted)
            }
        }

    }

    private fun requestMicrophonePermission(callback: (Boolean) -> Unit) {
        AVAudioSession.sharedInstance().requestRecordPermission { granted ->
            callback(granted)
        }
    }


}







