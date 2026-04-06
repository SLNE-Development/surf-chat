package dev.slne.surf.chat.api.entry

import dev.slne.surf.api.core.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.core.api.common.SurfCoreApi
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
    val deletedAt: SerializableOffsetDateTime? = null,
    val deletedBy: SerializableUUID? = null,
    val deletionReason: String? = null
) {
    suspend fun sender() = SurfCoreApi.getOfflinePlayer(senderUuid) ?: error("Sender user $senderUuid not found")
    suspend fun receiver() = receiverUuid?.let { SurfCoreApi.getOfflinePlayer(it) }
}