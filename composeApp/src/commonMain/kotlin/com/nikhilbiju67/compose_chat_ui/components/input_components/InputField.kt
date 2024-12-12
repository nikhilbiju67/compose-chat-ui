package com.nikhilbiju67.compose_chat_ui.components.input_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nikhilbiju67.compose_chat_ui.audio.AudioRecord
import com.nikhilbiju67.compose_chat_ui.audio.OutputFormat
import com.nikhilbiju67.compose_chat_ui.audio.OutputLocation
import com.nikhilbiju67.compose_chat_ui.audio.RecordConfig
import com.nikhilbiju67.compose_chat_ui.styles.AttachmentStyle
import com.nikhilbiju67.compose_chat_ui.styles.InputFieldStyle
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer
import components.input_components.TextInputField
import composechatui.composeapp.generated.resources.Res
import composechatui.composeapp.generated.resources.baseline_mic_24
import composechatui.composeapp.generated.resources.baseline_send_24
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.AudioMessage
import models.Message
import models.TextMessage
import models.User
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun InputField(
    onSend: (Message) -> Unit,
    modifier: Modifier = Modifier,
    inputFieldStyle: InputFieldStyle,
    loggedInUser: User,
    attachmentOptions: AttachmentStyle
) {
    val buttonSize = 50
    var showRecordingUi by remember { mutableStateOf(false) }
    var inputString by rememberSaveable { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    val attachmentHeight by animateFloatAsState(if (showAttachmentSheet) 300f else 0f)
    var audioCancelled by remember { mutableStateOf(false) }
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {

        /// Animated visibility of the attachment sheet


        /// Input Field and Action Button Section
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(horizontal = 10.dp)
                .background(inputFieldStyle.backGroundColor, shape = RoundedCornerShape(16.dp))
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = buttonSize.dp)
            ) {
                /// Show Audio Recording UI when recording
                androidx.compose.animation.AnimatedVisibility(
                    visible = showRecordingUi,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300)),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    AudioRecordingInput(
                        cancelled = audioCancelled,
                        onAnimationComplete = {
                            showRecordingUi = false
                            audioCancelled = false
                        }
                    )
                }

                /// Show Text Input Field when not recording
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showRecordingUi,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300)),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    TextInputField(
                        value = inputString,
                        onChange = { inputString = it },
                        onAttachmentClick = { showAttachmentSheet = !showAttachmentSheet },
                        inputFieldStyle = inputFieldStyle,
                        modifier = Modifier
                    )
                }
            }

            /// Display hold-to-record button if input is empty, otherwise show send button
            if (inputString.isEmpty()) {
                HoldableButton(
                    onHold = { showRecordingUi = true },
                    onRelease = {
                        showRecordingUi = false
                    },
                    onSwipeLeft = {
                        audioCancelled = true
                    },
                    isRecording = showRecordingUi,
                    onStopRecord = {
                        println("Recorded file path: $it")
                        onSend(
                            AudioMessage(
                                id = Random.nextInt(100000).toString(),
                                messageContent = it,
                                localFilePath = it,
                                messageReactions = emptyList(),
                                replyingToMessageId = null,
                                sentBy = loggedInUser,
                                sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                                sentTo = emptyList()
                            )
                        )
                    },

                    backGroundColor = inputFieldStyle.recordButtonBackGroundColor,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    Image(
                        modifier = Modifier
                            .clip(CircleShape),
                        painter = painterResource(Res.drawable.baseline_mic_24),
                        contentDescription = "Start recording"
                    )
                }
            } else {
                Button(
                    onClick = {
                        onSend(
                            TextMessage(
                                messageContent = inputString,
                                id = Random.nextInt(100000).toString(),
                                sentTo = emptyList(),
                                sentBy = loggedInUser,
                                messageReactions = emptyList(),
                                replyingToMessageId = null,
                                sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                            )
                        )
                        inputString = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = inputFieldStyle.recordButtonBackGroundColor
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(buttonSize.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.baseline_send_24),
                        contentDescription = "Send message"
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showAttachmentSheet,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight } // Slide in from bottom
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight } // Slide out to bottom
            ) + fadeOut()
        ) {
            AttachmentSheetContent(
                showAttachmentSheet,
                attachmentOptions,
                onAttachmentHide = {
                    showAttachmentSheet = false
                }

            )
        }
    }
}


@Composable
fun HoldableButton(
    modifier: Modifier = Modifier,
    onHold: () -> Unit,                // Starts recording
    onSwipeLeft: () -> Unit,            // Cancels recording
    onRelease: () -> Unit,              // Stops recording     // Flag to show recording UI
    backGroundColor: Color,
    isRecording: Boolean,
    swipeInfoView: (@Composable () -> Unit)? = null,
    swipeInfoModifier: Modifier = Modifier.padding(horizontal = 12.dp),
    onStopRecord: (String) -> Unit = {},
    content: @Composable () -> Unit     // Button content
) {
    val buttonSize = 50.dp
    val maxDragDistance = -300f          // Minimum left drag distance to cancel recording
    var recordButtonX by remember { mutableFloatStateOf(0f) }
    var recordingCancelling by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var _isRecording by remember { mutableStateOf(isRecording) }
    var showSwipeInfo by remember { mutableStateOf(false) }
    val yourShimmerTheme = defaultShimmerTheme.copy(rotation = 180f)
    val recorder = remember { AudioRecord() }
    var permissionGranted by remember { mutableStateOf(false) }

    LaunchedEffect(isRecording&&permissionGranted) {
        _isRecording = isRecording
        showSwipeInfo = isRecording
        recordingCancelling = false
        if (isRecording) {

            recorder.startRecording(
                RecordConfig(
                    outputLocation = OutputLocation.Cache,
                    outputFormat = OutputFormat.MPEG_4
                )
            )
        } else {
            if (recorder.isRecording()) {
                val value = recorder.stopRecording()
                onStopRecord(value)
                println("Recorded file path: $value")
            }

        }
    }


    // Scale animation for the button when recording
    val sizeScale by animateFloatAsState(
        targetValue = if (_isRecording && !recordingCancelling) 1.5f else 1f, label = ""
    )

    fun resetStates() {
        recordButtonX = 0f
        recordingCancelling = false
    }


    CompositionLocalProvider(
        LocalShimmerTheme provides yourShimmerTheme
    ) {
        AudioRecord().RequestMicrophonePermission(onPermissionResult = {
            permissionGranted = it


        }) { askPermission ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .offset { IntOffset(recordButtonX.roundToInt(), 0) }
                    .pointerInput(Unit)
                    {
                        awaitPointerEventScope {
                            while (true) {

                                val event = awaitPointerEvent()
                                val isDown = event.changes.firstOrNull()?.pressed == true
                                if (isDown && !recordingCancelling) {
                                    if (!_isRecording) {
                                        askPermission()
                                        onHold()
                                    } else {
                                        val change = event.changes.firstOrNull()
                                        if (change != null && change.positionChange().x < 0 && _isRecording) {
                                            recordButtonX += change.positionChange().x
                                            if (recordButtonX < maxDragDistance) {
                                                recordingCancelling = true
                                                recordButtonX = 0f
                                                showSwipeInfo = false
                                                onSwipeLeft()

                                                //
                                            }
                                        }
                                    }
                                } else {
                                    recordButtonX = 0f
                                    if (!recordingCancelling) {
                                        onRelease()
                                    }

                                }

                            }
                        }
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showSwipeInfo) if (swipeInfoView != null) swipeInfoView() else Text(
                        "<< Swipe left to cancel".uppercase(),
                        modifier = Modifier.padding(end = 12.dp).shimmer(
                        ),
                        style = TextStyle(color = Color.White)
                    )
                    Box(
                        Modifier.size(buttonSize)
                            .graphicsLayer(scaleX = sizeScale, scaleY = sizeScale)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center,

                        ) {
                        content()
                    }
                }
            }
        }
    }


}





