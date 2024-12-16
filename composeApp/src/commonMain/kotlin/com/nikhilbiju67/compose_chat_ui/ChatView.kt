package com.nikhilbiju67.compose_chat_ui

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.nikhilbiju67.compose_chat_ui.styles.BubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.ChatStyle
import com.nikhilbiju67.compose_chat_ui.styles.MessageBubbleStyle
import com.nikhilbiju67.compose_chat_ui.styles.defaultChatStyle
import com.nikhilbiju67.compose_chat_ui.components.message_bubble.MessageBubble
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.AudioMessage
import models.ImageMessage
import models.Message
import models.MessageData
import models.TextMessage
import models.User


@Composable
fun ChatView(
    messageData: MessageData,
    onSend: (Message) -> Unit,
    modifier: Modifier,
    chatStyle: ChatStyle = defaultChatStyle
) {

    Box(modifier = modifier.background(chatStyle.containerStyle.backGroundColor))
    {
        EndlessLazyColumn(
            modifier = Modifier,
            items = messageData.messages,

            itemContent = { modifier, it ->
                MessageBubble(
                    modifier = modifier,
                    it,
                    it.sentBy.id == messageData.loggedInUser.id,
                    bubbleStyle = getBubbleStyle(
                        messageData.loggedInUser.id,
                        it,
                        chatStyle.messageBubbleStyle
                    ),
                    messageBubbleStyle = chatStyle.messageBubbleStyle
                )
            },
            loading = false,
            loadingItem = {
                Text("Loading")
            },
            listState = rememberLazyListState(),

            loadMore = {
                println("load more")
            },
            listItemKey = {
                it.id
            },
        )
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

fun getBubbleStyle(
    loggedInUserId: String,
    message: Message,
    messageBubbleStyle: MessageBubbleStyle
): BubbleStyle {
    if (loggedInUserId == message.sentBy.id)
        return messageBubbleStyle.outGoingBubbleStyle
    else {
        return messageBubbleStyle.incomingBubbleStyle
    }
}


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

    LaunchedEffect(items) {
        ///if scroll position is zero animate to 0
        if (!listState.canScrollBackward)
            listState.animateScrollToItem(0)
    }

    val reachedBottom: Boolean by remember { derivedStateOf { listState.reachedBottom() } }

    // load more if scrolled to bottom
    LaunchedEffect(reachedBottom) {
        if (reachedBottom && !loading) loadMore()
    }
    LaunchedEffect(items) {
        ///if scroll position is zero animate to 0
        if (!listState.canScrollBackward)
            listState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxHeight(),
        state = listState,
        reverseLayout = true
    ) {
        item {
            Box(modifier = Modifier.height(64.dp)) {
                Text("End")
            }
        }
//        items(
//            items = list,
//            key = {
//                Math.random()
//            }
//        ){
//            ListItem(modifier=Modifier.animateItemPlacement())
//
//        }
        items(
            items,

//          key = { itemKey(items[it]) }
            key = { message ->
                // Return a stable + unique key for the item
                listItemKey(message)
            }

        ) { it ->

            itemContent(
                Modifier.animateItem(

                ), it
            )
        }
//        items.forEach { item ->
//            item(key = itemKey(item)) {
//                itemContent(item)
//            }
//        }
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
