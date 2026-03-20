package dev.slne.surf.chat.core.common.rabbit.packet.request.history

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import dev.slne.surf.surfapi.core.api.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class MarkHistoryEntryDeletedRequestPacket(
    val messageUuid: SerializableUUID,
    val deletedBy: SerializableUUID?,
    val deletionReason: String?,
    val deletedAt: SerializableOffsetDateTime
) : RabbitRequestPacket<PrimitiveResponse.BooleanResponsePacket>()
