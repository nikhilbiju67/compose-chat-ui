package com.nikhilbiju67.compose_chat_ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.bubble.BubbleShadow
import com.smarttoolfactory.bubble.BubbleState
import com.nikhilbiju67.compose_chat_ui.components.message_bubble.outgoingBubbleState
import composechatui.composeapp.generated.resources.Res
import composechatui.composeapp.generated.resources.contact_page_24px
import composechatui.composeapp.generated.resources.description_24px
import composechatui.composeapp.generated.resources.gallery_thumbnail_24px
import composechatui.composeapp.generated.resources.location_on_24px
import composechatui.composeapp.generated.resources.photo_camera_24px
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import models.AudioMessage
import models.toColorInt
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

data class ChatStyle(
    val containerStyle: ContainerStyle,
    val messageBubbleStyle: MessageBubbleStyle,
    val inputFieldStyle: InputFieldStyle,
    val attachmentStyle: AttachmentStyle,

    )

data class AttachmentStyle(
    val backGroundColor: Color,
    val attachmentOptions: List<AttachmentOption>,
    val modifier: Modifier? = Modifier
)

data class AttachmentOption(
    val iconColor: Color,
    val iconSize: Dp,
    val containerColor: Color = Color.Transparent,
    val attachmentModifier: Modifier? = null,
    val label: String,
    val labelStyle: TextStyle?,
    val icon: @Composable () -> Unit,
)

data class ContainerStyle(val backGroundColor: Color, val padding: Dp)
data class MessageBubbleStyle(
    val incomingBubbleStyle: BubbleStyle,
    val outGoingBubbleStyle: BubbleStyle,
    val userSpecificBubbleStyles: List<UserSpecificBubbleStyle>,
    val audioBubble: @Composable (AudioMessage) -> Unit
)

data class InputFieldStyle(
    val backGroundColor: Color,
    val textFieldBackGroundColor: Color,
    val shadow: Shadow,
    val inputTextStyle: TextStyle,
    val recordButtonBackGroundColor: Color,
    val recordingText: String,
    val attachmentButtonBackGroundColor: Color,
    val bottomMargin: Dp
)

data class UserSpecificBubbleStyle(
    val userId: String,
    val bubbleStyle: BubbleStyle
)

data class   BubbleStyle(
    val bubbleState: BubbleState,
    val shadow: BubbleShadow,
    val borderStroke: BorderStroke?,
    val color: Color,
    val textStyle: TextStyle,
    val timeFormat: (LocalDateTime) -> String,
    val timeStyle: TextStyle,
    val modifier: Modifier = Modifier,
    val imageMessageStyle: ImageMessageStyle
)

data class ImageMessageStyle(
    val imageModifier: Modifier,
    val contentScale: ContentScale
)

var defaultBubbleStyle=MessageBubbleStyle(
    outGoingBubbleStyle = BubbleStyle(
        modifier = Modifier.padding(8.dp),
        borderStroke = null,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
        bubbleState = outgoingBubbleState,
        color = Color("#3E6A97".toColorInt()),
        shadow = BubbleShadow(
            spotColor = Color.Black,
            ambientColor = Color.Black,
            elevation = 1.dp
        ),
        timeFormat = formatTime(),
        timeStyle = TextStyle(fontSize = 12.sp, color = Color.White),
        imageMessageStyle = ImageMessageStyle(
            imageModifier = Modifier.width(200.dp).height(200.dp),
            contentScale = ContentScale.Crop
        )
    ),
    incomingBubbleStyle = BubbleStyle(
        modifier = Modifier.padding(8.dp),
        borderStroke = null,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
        bubbleState = outgoingBubbleState,
        color = Color("#213040".toColorInt()),
        shadow = BubbleShadow(
            spotColor = Color.Black,
            ambientColor = Color.Black,
            elevation = 1.dp
        ),
        timeFormat = formatTime(),
        timeStyle = TextStyle(fontSize = 12.sp, color = Color.White),
        imageMessageStyle = ImageMessageStyle(
            imageModifier = Modifier.width(200.dp).height(200.dp),
            contentScale = ContentScale.Crop
        )
    ),
    userSpecificBubbleStyles = emptyList(),
    audioBubble = {
        Text("This is an audio message")
    }

)


val defaultAttachmentOptions = listOf(
    AttachmentOption(
        label = "Document",
        containerColor = Color(0xffaf74ff),
        iconColor = Color.White,
        icon = {
            Image(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(Res.drawable.description_24px),
                contentDescription = "Gallery",
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        labelStyle = TextStyle(fontSize = 12.sp, color = Color.White),
        iconSize = 24.dp,
    ),
    AttachmentOption(
        label = "Camera",
        containerColor = Color(0xffd53262),
        iconColor = Color.White,
        icon = {
            Image(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(Res.drawable.photo_camera_24px),
                contentDescription = "Camera",
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        labelStyle = TextStyle(fontSize = 12.sp, color = Color.White),
        iconSize = 24.dp,
    ),

    AttachmentOption(
        containerColor = Color(0xffaf74ff),
        label = "Gallery",
        iconColor = Color.White,
        icon = {
            Image(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(Res.drawable.gallery_thumbnail_24px),
                contentDescription = "Gallery",
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        labelStyle = TextStyle(fontSize = 12.sp, color = Color.White),
        iconSize = 24.dp,
    ),
    AttachmentOption(
        containerColor = Color(0xfff3712a),
        label = "Audio",
        iconColor = Color.White,
        icon = {
            Image(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(Res.drawable.gallery_thumbnail_24px),
                contentDescription = "Gallery",
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        labelStyle = TextStyle(fontSize = 12.sp, color = Color.White),
        iconSize = 24.dp,
    ),


    AttachmentOption(
        label = "Contact",
        iconColor = Color.White,
        containerColor = Color(0xff1595ae),
        icon = {
            Image(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(Res.drawable.contact_page_24px),
                contentDescription = "Gallery",
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        labelStyle = TextStyle(fontSize = 12.sp, color = Color.White),
        iconSize = 24.dp,
    ),
    AttachmentOption(
        label = "Location",
        iconColor = Color.White,
        containerColor = Color(0xff349e6e),
        icon = {
            Image(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(Res.drawable.location_on_24px),
                contentDescription = "Location",
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        labelStyle = TextStyle(fontSize = 12.sp, color = Color.White),
        iconSize = 24.dp,
    )
)

val defaultChatStyle = ChatStyle(
    containerStyle = ContainerStyle(
        backGroundColor = Color("#18222D".toColorInt()),
        padding = 10.dp
    ),
    attachmentStyle = AttachmentStyle(
        backGroundColor = Color("#18222D".toColorInt()),
        attachmentOptions = defaultAttachmentOptions
    ),
    inputFieldStyle = InputFieldStyle(
        backGroundColor = Color(0xff18222D),
        inputTextStyle = TextStyle(fontSize = 15.sp, color = Color.White),
        attachmentButtonBackGroundColor = Color("#18222D".toColorInt()),
        bottomMargin = 12.dp,
        recordButtonBackGroundColor = Color("#18222D".toColorInt()),
        shadow = Shadow(),
        recordingText = "Recording your voice",
        textFieldBackGroundColor = Color(0xff1f2b3c),
    ),
    messageBubbleStyle = defaultBubbleStyle,
)

private fun formatTime() = { localDateTime: LocalDateTime ->
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val localDateTimeInstant = localDateTime.toInstant(TimeZone.currentSystemDefault())
    val nowInstant = now.toInstant(TimeZone.currentSystemDefault())

    // Calculate the difference in seconds
    val secondsDiff = abs(nowInstant.epochSeconds - localDateTimeInstant.epochSeconds)
    val minutesDiff = secondsDiff / 60
    val hoursDiff = minutesDiff / 60
    val daysDiff = hoursDiff / 24

    when {
        daysDiff >= 2 -> localDateTime.date.toString() // Example: "2024-11-26"
        daysDiff == 1L -> "Yesterday"
        hoursDiff >= 1 -> "$hoursDiff hour${if (hoursDiff > 1) "s" else ""} ago"
        minutesDiff >= 1 -> "$minutesDiff minute${if (minutesDiff > 1) "s" else ""} ago"
        else -> "Just now"
    }
}

