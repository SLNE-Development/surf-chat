package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.message.MessageType
import java.time.OffsetDateTime
import java.util.*

/**
 * Represents a filter for querying chat history.
 *
 * @property messageUuid The unique identifier of the message to filter by, or `null` if not applicable.
 * @property senderUuid The unique identifier of the sender to filter by, or `null` if not applicable.
 * @property receiverUuid The unique identifier of the receiver to filter by, or `null` if not applicable.
 * @property messageType The type of the message to filter by, or `null` if not applicable.
 * @property after The time range (in milliseconds) to filter messages by, or `null` if not applicable.
 * @property messageLike A substring to search for in the message content, or `null` if not applicable.
 * @property server The server to filter messages by, or `null` if not applicable.
 * @property deletedBy The identifier of the user who deleted the message to filter by, or `null` if not applicable.
 * @property type The type of the message to filter by, or `null` if not applicable.
 * @property limit The maximum number of results to return, or `null` if not applicable.
 */
data class HistoryFilter(
    val messageUuid: UUID?,
    val senderUuid: UUID?,
    val receiverUuid: UUID?,
    val messageType: MessageType?,
    val after: OffsetDateTime?,
    val messageLike: String?,
    val server: String?,
    val deleted: Boolean?,
    val deletedBy: UUID?,
    val limit: Int = 100
) {
    companion object {
        fun empty() = HistoryFilter(
            null, null, null, null, null, null, null, null, null, 50
        )
    }
}