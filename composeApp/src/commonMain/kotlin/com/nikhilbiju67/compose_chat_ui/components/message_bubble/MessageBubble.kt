package com.nikhilbiju67.compose_chat_ui.components.message_bubble

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nikhilbiju67.compose_chat_ui.audio.PlayerState
import com.nikhilbiju67.compose_chat_ui.styles.BubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.ImageMessageStyle
import com.nikhilbiju67.compose_chat_ui.styles.MessageBubbleStyle
import com.smarttoolfactory.bubble.ArrowAlignment
import com.smarttoolfactory.bubble.ArrowShape
import com.smarttoolfactory.bubble.BubbleCornerRadius
import com.smarttoolfactory.bubble.BubbleState
import com.smarttoolfactory.bubble.bubble
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import models.AudioMessage
import models.FileMessage
import models.ImageMessage
import models.Message
import models.TextMessage
import models.VideoMessage

val outgoingBubbleState = BubbleState(
    arrowShape = ArrowShape.Curved,

    cornerRadius =

    BubbleCornerRadius(20.dp, 12.dp, 16.dp, 12.dp),
    alignment = ArrowAlignment.LeftBottom,


    )

val incomingBubbleState = BubbleState(
    arrowShape = ArrowShape.Curved,

    cornerRadius = BubbleCornerRadius(12.dp, 20.dp, 12.dp, 16.dp),
    alignment = ArrowAlignment.RightBottom,


    )

@Composable
fun MessageBubble(
    modifier: Modifier,
    message: Message,
    isSender: Boolean,
    bubbleStyle: BubbleStyle,
    messageBubbleStyle: MessageBubbleStyle
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isSender) Alignment.End else Alignment.Start,
            modifier = Modifier. widthIn(0.dp,300.dp).padding(
                8.dp
            ).bubble(
                color = bubbleStyle.color,
                borderStroke = bubbleStyle.borderStroke,
                bubbleState = bubbleStyle.bubbleState,
                shadow = bubbleStyle.shadow,
            ).then(bubbleStyle.modifier).clickable {

            }
        ) {
            when (message) {
                is TextMessage -> {
                    TextMessageContent(message, bubbleStyle.textStyle)
                }

                is VideoMessage -> {
                    val playerState by remember { mutableStateOf(PlayerState()) }
                    VideoMessageBubble(message)
//                    VideoMessageContent(message)
                }


                is ImageMessage -> {
                    ImageMessageContent(message, bubbleStyle.imageMessageStyle)
                }


                is FileMessage -> {
                    FileMessageContent(message)
                }


                is AudioMessage -> {
                    AudioMessageContent(message, messageBubbleStyle.audioBubble)
                }


                else -> {
                    UnknownMessagePlaceHolder()
                }

            }

            Text(
                bubbleStyle.timeFormat(message.sendAt),
                textAlign = TextAlign.End,
                modifier = Modifier.padding(vertical = 4.dp).align(Alignment.End),
                style = bubbleStyle.timeStyle
            )

        }
    }

}

@Composable
fun UnknownMessagePlaceHolder() {
    Text("Unkown message")
}

@Composable
fun TextMessageContent(textMessage: TextMessage, textStyle: TextStyle) {
    Column {
        Text(textMessage.messageContent, style = textStyle)
    }

}

@Composable
fun ImageMessageContent(imageMessage: ImageMessage, imageMessageStyle: ImageMessageStyle) {
    Column {
        KamelImage(
            { asyncPainterResource(data = imageMessage.messageContent) },
            contentDescription = "description",
            modifier = imageMessageStyle.imageModifier,
            contentScale = imageMessageStyle.contentScale
        )
    }

}

@Composable
fun VideoMessageContent(videoMessage: VideoMessage) {
    Column {
        Text(videoMessage.messageContent)
    }

}

@Composable
fun AudioMessageContent(
    audioMessage: AudioMessage,
    audioBubble: @Composable (AudioMessage) -> Unit
) {
    Column {
        AudioMessageBubble(audioMessage, audioBubble)
    }

}

@Composable
fun FileMessageContent(fileMessage: FileMessage) {
    Column {
        Text(fileMessage.messageContent)
    }

}