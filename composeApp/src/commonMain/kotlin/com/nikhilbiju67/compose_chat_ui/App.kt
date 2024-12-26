package com.nikhilbiju67.compose_chat_ui

//import com.nikhilbiju67.compose_chat_ui.audio.AudioPlayer
//import com.nikhilbiju67.compose_chat_ui.audio.rememberPlayerState
//import com.nikhilbiju67.compose_chat_ui.styles.MessageBubbleStyle
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.materialkolor.DynamicMaterialTheme
import com.nikhilbiju67.compose_chat_ui.audio.AudioPlayer
import com.nikhilbiju67.compose_chat_ui.audio.PlayerState
import com.nikhilbiju67.compose_chat_ui.components.Utils.generateRandomStringMessage
import com.nikhilbiju67.compose_chat_ui.styles.AttachmentStyle
import com.nikhilbiju67.compose_chat_ui.styles.defaultAttachmentOptions
import com.nikhilbiju67.compose_chat_ui.styles.defaultBubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.defaultChatStyle
import com.smarttoolfactory.bubble.ArrowAlignment
import com.smarttoolfactory.bubble.ArrowShape
import com.smarttoolfactory.bubble.BubbleCornerRadius
import com.smarttoolfactory.bubble.BubbleState
import composechatui.composeapp.generated.resources.Res
import composechatui.composeapp.generated.resources.dark_mode_24px
import composechatui.composeapp.generated.resources.palette_24px
import composechatui.composeapp.generated.resources.pause_circle_24px
import composechatui.composeapp.generated.resources.play_circle_24px
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.AudioMessage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


/// Main Composable Function representing the chat application UI.
@Composable
@Preview
fun App() {
    /// State variables for chat messages, player state, and UI customization.
    var messages by remember { mutableStateOf(dummyData) }
    var currentPlayerState by remember { mutableStateOf(PlayerState()) }
    var typing by remember { mutableStateOf(false) }
    var seedColor by remember { mutableStateOf(Color.Red) }
    var isDarkTheme by remember { mutableStateOf(false) }
    var currentlyPlaying by remember { mutableStateOf<AudioMessage?>(null) }

    /// Coroutine scope for asynchronous operations.
    val scope = rememberCoroutineScope()

    /// AudioPlayer instance to handle audio playback.
    val audioPlayer = remember {
        AudioPlayer(currentPlayerState) { playerState ->
            currentPlayerState = playerState.copy(
                isPlaying = playerState.isPlaying,
                isBuffering = playerState.isBuffering,
                currentTime = playerState.currentTime,
                duration = playerState.duration,
                currentlyPlayingId = playerState.currentlyPlayingId
            )
        }
    }

    /// Dynamic Material Theme for customizable colors and theme.
    DynamicMaterialTheme(
        seedColor = seedColor,
        animate = true,
        useDarkTheme = isDarkTheme
    ) {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            topBar = {
                TopAppBar(
                    modifier = Modifier.background(seedColor),
                    onDarkThemeSelect = { isDarkTheme = it },
                    onColorSelect = { seedColor = it },
                    selectedColor = seedColor,
                    isDarkTheme = isDarkTheme
                )
            }
        ) {
            /// Chat view with message handling.
            ChatView(
                typing = typing,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                chatStyle = defaultChatStyle.copy(
                    containerStyle = defaultChatStyle.containerStyle.copy(
                        backGroundColor = MaterialTheme.colorScheme.surface
                    ),
                    attachmentStyle = AttachmentStyle(
                        attachmentIconColor = MaterialTheme.colorScheme.onSurface,
                        attachmentIconBackGroundColor = MaterialTheme.colorScheme.surface,
                        backGroundColor = MaterialTheme.colorScheme.surface,
                        attachmentOptions = defaultAttachmentOptions
                    ),
                    inputFieldStyle = defaultChatStyle.inputFieldStyle.copy(
                        backGroundColor = MaterialTheme.colorScheme.surface,
                        inputTextStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        recordSendButtonIconColor = MaterialTheme.colorScheme.onPrimary,
                        recordSendButtonBackGroundColor = MaterialTheme.colorScheme.primary,
                    ),
                    messageBubbleStyle = defaultBubbleStyle.copy(
                        incomingBubbleStyle = defaultBubbleStyle.incomingBubbleStyle.copy(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            timeStyle = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            bubbleState = BubbleState(
                                arrowShape = ArrowShape.Curved,

                                cornerRadius = BubbleCornerRadius(12.dp, 20.dp, 12.dp, 16.dp),
                                alignment = ArrowAlignment.LeftBottom,


                                ),

                        ),

                        outGoingBubbleStyle = defaultBubbleStyle.outGoingBubbleStyle.copy(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            timeStyle = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            bubbleState = BubbleState(
                                arrowShape = ArrowShape.Curved,

                                cornerRadius =

                                BubbleCornerRadius(20.dp, 12.dp, 16.dp, 12.dp),
                                alignment = ArrowAlignment.RightBottom,


                                )
                        ),
                        audioBubble = { audioMessage ->
                            AudioMessage(audioMessage,
                                onPlayAudio = {
                                    currentlyPlaying = audioMessage
                                    audioPlayer.play(audioMessage)
                                },
                                playerState = currentPlayerState,
                                isOutGoing = audioMessage.sentBy.id == dummyData.loggedInUser.id,
                                onPauseAudio = {
                                    audioPlayer.pause()
                                })
                        }
                    )),
                onSend = { message ->
                    messages = messages.copy(messages = listOf(message) + messages.messages)
                    scope.launch {
                        typing = true
                        withContext(Dispatchers.Default) { delay(2000) }
                        val generatedMessage = dummyData.receivers.firstOrNull()?.let {
                            generateRandomStringMessage(it)
                        }
                        typing = false
                        generatedMessage?.let {
                            messages = messages.copy(messages = listOf(it) + messages.messages)
                        }
                    }
                },
                messageData = messages
            )
        }
    }
}

/// Top AppBar with theme and color customization options.
@Composable
private fun TopAppBar(
    modifier: Modifier,
    onColorSelect: (Color) -> Unit,
    onDarkThemeSelect: (Boolean) -> Unit,
    selectedColor: Color,
    isDarkTheme: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val height by animateDpAsState(targetValue = if (expanded) 150.dp else 50.dp)

    Column(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(0.dp))
            .fillMaxWidth()
            .height(height)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Compose Chat UI",
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painter = painterResource(Res.drawable.palette_24px),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
            }
            IconButton(onClick = { onDarkThemeSelect(!isDarkTheme) }) {
                Icon(
                    painter = painterResource(Res.drawable.dark_mode_24px),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
            }
        }
        if (expanded) {
            ColorPicker(selectedColor, onColorSelect)
        }
    }
}

/// A LazyRow for selecting a color theme.
@Composable
fun ColorPicker(selectedColor: Color, onColorSelect: (Color) -> Unit) {
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta,
        Color.Gray, Color.Black, Color.White
    )
    LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        items(colors.size) { index ->
            val color = colors[index]
            val selected = selectedColor.toArgb() == color.toArgb()
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .border(
                        width = if (selected) 2.dp else 0.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onColorSelect(color) }
            )
        }
    }
}

/// Audio message bubble UI.
@Composable
fun AudioMessage(
    audioMessage: AudioMessage,
    onPlayAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    isOutGoing: Boolean,
    playerState: PlayerState
) {
    val audioPlaying = playerState.isPlaying && playerState.currentlyPlayingId == audioMessage.id
    val tintColor =
        if (isOutGoing) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 24.dp).widthIn(300.dp)
    ) {
        IconButton(onClick = { if (audioPlaying) onPauseAudio() else onPlayAudio() }) {
            Icon(
                painter = painterResource(if (audioPlaying) Res.drawable.pause_circle_24px else Res.drawable.play_circle_24px),
                tint = tintColor,
                contentDescription = null
            )
        }
        LinearProgressIndicator(
            progress = { if (audioPlaying) playerState.progress?.toFloat() ?: 0f else 0f },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            color = tintColor,
            trackColor = tintColor.copy(
                alpha = 0.5f
            )
        )
    }
}
