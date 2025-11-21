package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.message.MessageType
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
data class HistoryEntry(
    val messageUuid: UUID,
    val senderUuid: UUID,
    val receiverUuid: UUID?,
    val messageType: MessageType,
    val sentAt: Long,
    val message: String,
    val server: String,
    val channel: String?,
    val deletedBy: String?
)
