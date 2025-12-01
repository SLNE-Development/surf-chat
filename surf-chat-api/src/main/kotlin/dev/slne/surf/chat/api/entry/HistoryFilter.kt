package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.message.MessageType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
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
data class HistoryFilter(
    val messageUuid: @Contextual UUID?,
    val senderUuid: @Contextual UUID?,
    val receiverUuid: @Contextual UUID?,
    val messageType: MessageType?,
    val range: Long?,
    val messageLike: String?,
    val server: String?,
    val channel: String?,
    val deletedBy: String?,
    val type: MessageType?,
    val limit: Int?
) {
    companion object {
        fun empty() = HistoryFilter(
            null, null, null, null, null, null, null, null, null, null, null
        )
    }
}