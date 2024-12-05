package models

import kotlinx.datetime.LocalDateTime

abstract class Message {
    abstract val id: String
    abstract val sendAt: LocalDateTime
    abstract val sentBy: User
    abstract val sentTo: List<User>
    abstract val messageType: MessageType
    abstract val replyingToMessageId: String?
    abstract val messageContent: String?
    abstract val messageReactions: List<MessageReaction>
}

class TextMessage(
    override val id: String,
    override val sendAt: LocalDateTime,
    override val sentBy: User,
    override val sentTo: List<User>,
    override val replyingToMessageId: String? = null,
    override val messageContent: String,
    override val messageReactions: List<MessageReaction> = emptyList()
) : Message() {
    override val messageType: MessageType = MessageType.TEXT
}

class ImageMessage(
    override val id: String,
    override val sendAt: LocalDateTime,
    override val sentBy: User,
    override val sentTo: List<User>,
    override val replyingToMessageId: String? = null,
    override val messageContent: String,
    override val messageReactions: List<MessageReaction> = emptyList()
) : Message() {
    override val messageType: MessageType = MessageType.IMAGE

}

class VideoMessage(
    override val id: String,
    override val sendAt: LocalDateTime,
    override val sentBy: User,
    override val sentTo: List<User>,
    override val replyingToMessageId: String? = null,
    override val messageContent: String,
    override val messageReactions: List<MessageReaction> = emptyList()
) : Message() {
    override val messageType: MessageType = MessageType.VIDEO

}

class AudioMessage(
    override val id: String,
    override val sendAt: LocalDateTime,
    override val sentBy: User,
    override val sentTo: List<User>,
    override val messageContent: String,
    override val replyingToMessageId: String? = null,
    override val messageReactions: List<MessageReaction> = emptyList()
) : Message() {
    override val messageType: MessageType = MessageType.AUDIO

}

class FileMessage(
    override val id: String,
    override val sendAt: LocalDateTime,
    override val sentBy: User,
    override val sentTo: List<User>,
    override val replyingToMessageId: String? = null,
    override val messageContent: String,
    override val messageReactions: List<MessageReaction> = emptyList()
) : Message() {
    override val messageType: MessageType = MessageType.FILE

}

class MessageReaction(
    val reactionId: String,
    val reactionContent: String
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE
}


data class User(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
)

data class MessageData(
    val messages: List<Message>,
    val loggedInUser: User,
    val receivers: List<User>
)



//source code from androidX
fun String.toColorInt(): Int {
    if (this[0] == '#') {
        var color = substring(1).toLong(16)
        if (length == 7) {
            color = color or 0x00000000ff000000L
        } else if (length != 9) {
            throw IllegalArgumentException("Unknown color")
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}