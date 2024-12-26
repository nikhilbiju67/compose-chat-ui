package com.nikhilbiju67.compose_chat_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nikhilbiju67.compose_chat_ui.components.input_components.InputField
import com.nikhilbiju67.compose_chat_ui.components.message_bubble.MessageBubble
import com.nikhilbiju67.compose_chat_ui.styles.BubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.ChatStyle
import com.nikhilbiju67.compose_chat_ui.styles.MessageBubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.defaultChatStyle
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.AudioMessage
import models.ImageMessage
import models.Message
import models.MessageData
import models.TextMessage
import models.User
import models.VideoMessage


@Composable
fun ChatView(
    messageData: MessageData,
    onSend: (Message) -> Unit,
    modifier: Modifier,
    chatStyle: ChatStyle = defaultChatStyle
) {

    // Chat container with a customizable background color
    Box(modifier = modifier.background(chatStyle.containerStyle.backGroundColor)) {

        /// LazyColumn to display endless messages with load more functionality
        EndlessLazyColumn(
            modifier = Modifier,
            items = messageData.messages,

            /// Define how each message bubble is displayed
            itemContent = { modifier, message ->
                MessageBubble(
                    modifier = modifier,
                    message = message,
                    isSender = message.sentBy.id == messageData.loggedInUser.id,
                    bubbleStyle = getBubbleStyle(
                        messageData.loggedInUser.id,
                        message,
                        chatStyle.messageBubbleStyle
                    ),
                    messageBubbleStyle = chatStyle.messageBubbleStyle
                )
            },

            /// Loading indicator
            loading = false,
            loadingItem = {
                Text("Loading")
            },

            /// Remember list state for smooth scrolling
            listState = rememberLazyListState(),

            /// Callback for loading more items when reaching the end of the list
            loadMore = {
                println("Load more")
            },

            /// Key for each list item to improve performance and stability
            listItemKey = {
                it.id
            },
        )

        /// Input field for sending messages, aligned at the bottom of the screen
        InputField(
            onSend = {
                onSend(it)
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            inputFieldStyle = chatStyle.inputFieldStyle,
            attachmentStyle = chatStyle.attachmentStyle,
            loggedInUser = messageData.loggedInUser,
        )
    }
}

/**
 * Determine the bubble style based on whether the message is sent or received.
 */
fun getBubbleStyle(
    loggedInUserId: String,
    message: Message,
    messageBubbleStyle: MessageBubbleStyle
): BubbleStyle {
    return if (loggedInUserId == message.sentBy.id) {
        messageBubbleStyle.outGoingBubbleStyle
    } else {
        messageBubbleStyle.incomingBubbleStyle
    }
}

/**
 * Composable for displaying an endless lazy column with load-more support.
 */
@Composable
internal fun <T> EndlessLazyColumn(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    items: List<T>,
    listItemKey: (T) -> String,
    itemContent: @Composable (Modifier, T) -> Unit,
    loadingItem: @Composable () -> Unit,
    loadMore: () -> Unit
) {

    /// Scroll to the top when new items are added
    LaunchedEffect(items) {
        if (!listState.canScrollBackward)
            listState.animateScrollToItem(0)
    }

    /// Detect when the user scrolls to the bottom
    val reachedBottom: Boolean by remember {
        derivedStateOf { listState.reachedBottom() }
    }

    /// Trigger load more when scrolled to the bottom
    LaunchedEffect(reachedBottom) {
        if (reachedBottom && !loading) loadMore()
    }

    /// LazyColumn for displaying items in reverse order (most recent at the bottom)
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = listState,
        reverseLayout = true
    ) {

        /// Spacer at the top for additional padding
        item {
            Box(modifier = Modifier.height(64.dp)) {}
        }

        /// Display each item with animation and unique key
        items(
            items,
            key = { message -> listItemKey(message) }
        ) { item ->
            itemContent(Modifier.animateItem(), item)
        }

        /// Show loading indicator if loading is in progress
        if (loading) {
            item {
                loadingItem()
            }
        }
    }
}


private fun LazyListState.reachedBottom(): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - 1
}

val messages = listOf(
    VideoMessage(
        id = "18",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        messageContent = "https://onlinetestcase.com/wp-content/uploads/2023/06/100-KB-MP3.mp3",
    ),
    AudioMessage(
        id = "16",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        url = "https://onlinetestcase.com/wp-content/uploads/2023/06/5-MB-MP3.mp3",
        messageContent = "https://onlinetestcase.com/wp-content/uploads/2023/06/100-KB-MP3.mp3",
    ),
    AudioMessage(
        id = "10",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        url = "https://onlinetestcase.com/wp-content/uploads/2023/06/100-KB-MP3.mp3",
        messageContent = "https://onlinetestcase.com/wp-content/uploads/2023/06/100-KB-MP3.mp3",
    ),
    TextMessage(
        id = "1",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "Hello"
    ),
    ImageMessage(
        id = "1-2",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "https://picsum.photos/200/300"
    ),
    TextMessage(
        id = "2",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("1", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "How are you?"
    ),
    TextMessage(
        id = "3",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "I am fine how are you"
    ),
    TextMessage(
        id = "4",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("1", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "Where are you"
    ),
    TextMessage(
        id = "11",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "Hello"
    ),
    TextMessage(
        id = "22",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("1", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "How are you?"
    ),
    TextMessage(
        id = "33",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "I am fine how are you"
    ),
    TextMessage(
        id = "44",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("1", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "Where are you"
    ),
    TextMessage(
        id = "55",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "OK here i am"
    ),
    TextMessage(
        id = "66",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("1", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "Please call me when you reach"
    ),
    TextMessage(
        id = "7",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("1", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "OK i will can you"
    ),
    TextMessage(
        id = "8",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "Sure"
    ),
    TextMessage(
        id = "9",
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        sentBy = User("2", "Nikhil", ""),
        sentTo = emptyList(),
        messageContent = "OK"
    ),

    )
val dummyData: MessageData = MessageData(
    messages, loggedInUser = User(
        "2",
        name = "Alan",

        ),
    receivers = emptyList()
)
