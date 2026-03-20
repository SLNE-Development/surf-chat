package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.surfapi.core.api.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry(
    val messageUuid: SerializableUUID,
    val senderUuid: SerializableUUID,
    val receiverUuid: SerializableUUID?,
    val messageType: MessageType,
    val sentAt: SerializableOffsetDateTime,
    val message: String,
    val server: String,
    val deleted: Boolean,
    val deletedAt: SerializableOffsetDateTime?,
    val deletedBy: SerializableUUID?,
    val deletionReason: String?
) {
    suspend fun sender() = surfCoreApi.getOfflinePlayer(senderUuid) ?: error("Sender user $senderUuid not found")
    suspend fun receiver() = receiverUuid?.let { surfCoreApi.getOfflinePlayer(it) }
}