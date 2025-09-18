package dev.slne.surf.chat.core.entry

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import java.util.*

/**
 * Implementation of the `HistoryEntry` interface.
 *
 * Represents an entry in the chat history, containing details about the message,
 * its sender, potential receiver, and associated metadata such as the message type and timestamp.
 * This data class models the historical record of messages in the chat system.
 *
 * @property messageUuid The unique identifier of the message.
 * @property senderUuid The unique identifier of the sender of the message.
 * @property receiverUuid The unique identifier of the receiver, or `null` if the message is not directed to a specific user.
 * @property messageType The type of the message, such as global, channel, team, or direct.
 * @property sentAt The timestamp of when the message was sent, specified as milliseconds since the epoch.
 * @property message The textual content of the message.
 * @property server The instance of the chat server where the message originated.
 * @property channel The name of the channel where the message was sent, or `null` if not applicable.
 * @property deletedBy The unique identifier of the user who deleted the message, or `null` if the message has not been deleted.
 */
data class HistoryEntryImpl(
    override val messageUuid: UUID,
    override val senderUuid: UUID,
    override val messageType: MessageType,
    override val sentAt: Long,
    override val message: String,
    override val server: ChatServer,
    override val deletedBy: String?,
    override val receiverUuid: UUID?,
    override val channel: String?
) : HistoryEntry

/**
 * Implementation of the `HistoryFilter` interface.
 *
 * This class represents a data structure used to define criteria for filtering chat message history.
 * It provides various properties that can be used to narrow down results, such as filtering by
 * message UUID, sender, receiver, message type, time range, and other optional parameters.
 *
 * @property messageUuid The unique identifier of the message to filter by, or `null` if not applicable.
 * @property senderUuid The unique identifier of the sender to filter by, or `null` if not applicable.
 * @property receiverUuid The unique identifier of the receiver to filter by, or `null` if not applicable.
 * @property messageType The type of the message to filter by, or `null` if not applicable.
 * @property range The time range (in milliseconds) to filter messages by, or `null` if not applicable.
 * @property messageLike A substring to search for in the message content, or `null` if not applicable.
 * @property server The server to filter messages by, or `null` if not applicable.
 * @property channel The channel to filter messages by, or `null` if not applicable.
 * @property deletedBy The identifier of the user who deleted the messages to filter by, or `null` if not applicable.
 * @property type The specific type of message to filter by, if applicable and distinct from `messageType`, or `null`.
 * @property limit The maximum number of results to return, or `null` if not applicable.
 */
data class HistoryFilterImpl(
    override val messageUuid: UUID?,
    override val senderUuid: UUID?,
    override val messageType: MessageType?,
    override val range: Long?,
    override val messageLike: String?,
    override val server: ChatServer?,
    override val deletedBy: String?,
    override val receiverUuid: UUID?,
    override val channel: String?,
    override val type: MessageType?,
    override val limit: Int?
) : HistoryFilter
