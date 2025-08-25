package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import java.util.*

/**
 * Represents an entry in the chat history.
 *
 * @property messageUuid The unique identifier of the message.
 * @property senderUuid The unique identifier of the sender.
 * @property receiverUuid The unique identifier of the receiver, or `null` if not applicable.
 * @property messageType The type of the message (e.g., text, image, etc.).
 * @property sentAt The timestamp (in milliseconds since epoch) when the message was sent.
 * @property message The content of the message.
 * @property server The server where the message was sent.
 * @property channel The channel where the message was sent, or `null` if not applicable.
 * @property deletedBy The identifier of the user who deleted the message, or `null` if not deleted.
 */
interface HistoryEntry {
    val messageUuid: UUID
    val senderUuid: UUID
    val receiverUuid: UUID?
    val messageType: MessageType
    val sentAt: Long
    val message: String
    val server: ChatServer
    val channel: String?
    val deletedBy: String?
}

/**
 * Represents a filter for querying chat history.
 *
 * @property messageUuid The unique identifier of the message to filter by, or `null` if not applicable.
 * @property senderUuid The unique identifier of the sender to filter by, or `null` if not applicable.
 * @property receiverUuid The unique identifier of the receiver to filter by, or `null` if not applicable.
 * @property messageType The type of the message to filter by, or `null` if not applicable.
 * @property range The time range (in milliseconds) to filter messages by, or `null` if not applicable.
 * @property messageLike A substring to search for in the message content, or `null` if not applicable.
 * @property server The server to filter messages by, or `null` if not applicable.
 * @property channel The channel to filter messages by, or `null` if not applicable.
 * @property deletedBy The identifier of the user who deleted the message to filter by, or `null` if not applicable.
 * @property type The type of the message to filter by, or `null` if not applicable.
 * @property limit The maximum number of results to return, or `null` if not applicable.
 */
interface HistoryFilter {
    val messageUuid: UUID?
    val senderUuid: UUID?
    val receiverUuid: UUID?
    val messageType: MessageType?
    val range: Long?
    val messageLike: String?
    val server: ChatServer?
    val channel: String?
    val deletedBy: String?
    val type: MessageType?
    val limit: Int?
}