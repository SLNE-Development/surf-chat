package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.surfapi.core.api.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable


@Serializable
data class HistoryFilter(
    val messageUuid: SerializableUUID?,
    val senderUuid: SerializableUUID?,
    val receiverUuid: SerializableUUID?,
    val messageType: MessageType?,
    val after: SerializableOffsetDateTime?,
    val server: String?,
    val deleted: Boolean?,
    val deletedBy: SerializableUUID?,
    val limit: Int = 100
) {
    companion object {
        fun empty() = HistoryFilter(
            null, null, null, null, null, null, null, null, 50
        )
    }
}