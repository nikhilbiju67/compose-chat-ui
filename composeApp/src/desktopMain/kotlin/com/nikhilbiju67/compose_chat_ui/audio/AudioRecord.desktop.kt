package com.nikhilbiju67.compose_chat_ui.audio

import androidx.compose.runtime.Composable
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.sound.sampled.*
import javax.sound.sampled.AudioFileFormat.Type

actual class AudioRecord {
    private var audioLine: TargetDataLine? = null
    private var isRecording = false
    private var outputStream = ByteArrayOutputStream()
    private var config: RecordConfig? = null // Store the config

    actual internal fun isRecording(): Boolean {
        return isRecording
    }

    actual internal fun startRecording(
        config: RecordConfig,
        onPermissionResult: (Boolean) -> Unit
    ) {
        this.config = config // Store the config for later use
        try {
            val format = getAudioFormat() // Get default format or configure based on config
            val info = DataLine.Info(TargetDataLine::class.java, format)

            if (!AudioSystem.isLineSupported(info)) {
                throw UnsupportedAudioFileException("Audio format not supported")
            }

            audioLine = AudioSystem.getLine(info) as TargetDataLine
            audioLine?.open(format)
            audioLine?.start()
            isRecording = true

            // Start a new thread to write the audio data to a file or stream
            Thread {
                try {
                    when (val location = config.outputLocation) {
                        is OutputLocation.Cache -> {
                            val cacheDir = getCacheDir() // Implement getCacheDir()
                            val audioFile = File.createTempFile(
                                "recording",
                                config.outputFormat.extension,
                                cacheDir
                            )
                            AudioSystem.write(
                                AudioInputStream(audioLine),
                                getAudioFileFormatType(config.outputFormat),
                                audioFile
                            )
                        }

                        is OutputLocation.Internal -> {
                            // Handle writing to internal storage
                        }

                        is OutputLocation.Custom -> {
                            val audioFile = File(location.path)
                            AudioSystem.write(
                                AudioInputStream(audioLine),
                                getAudioFileFormatType(config.outputFormat),
                                audioFile
                            )
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual internal fun stopRecording(): String {
        audioLine?.stop()
        audioLine?.close()
        isRecording = false

        val config =
            this.config ?: throw IllegalStateException("Config not set") // Get stored config

        val filePath = when (val location = config.outputLocation) {
            is OutputLocation.Cache -> {
                // If using cache, the file path is already created in startRecording
                // You may need to manage temporary files and cleanup
                getCacheDir().absolutePath + File.separator + "recording" + config.outputFormat.extension
            }

            is OutputLocation.Internal -> {
                // Handle getting file path for internal storage
                ""
            }

            is OutputLocation.Custom -> {
                // Custom path is already known
                location.path
            }
        }

        return filePath
    }


    private fun getAudioFormat(): AudioFormat {
        val sampleRate = 44100f
        val sampleSizeInBits = 16
        val channels = 1 // Mono
        val signed = true
        val bigEndian = false
        return AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian)
    }

    private fun getAudioFileFormatType(outputFormat: OutputFormat): AudioFileFormat.Type {
        return when (outputFormat) {
            OutputFormat.MPEG_4 -> Type.WAVE // Or other appropriate type
            // Add other formats here
        }
    }

    // Implement this to get the cache directory on desktop
    private fun getCacheDir(): File {
        // Example (you might need to adjust this for different OSes):
        val userHome = System.getProperty("user.home")
        val cacheDir = File(userHome, ".myApp/cache") // Your app's cache folder
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        return cacheDir
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



