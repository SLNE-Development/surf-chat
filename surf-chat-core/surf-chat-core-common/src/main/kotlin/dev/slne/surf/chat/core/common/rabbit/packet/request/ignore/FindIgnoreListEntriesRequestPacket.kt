package dev.slne.surf.chat.core.common.rabbit.packet.request.ignore

import dev.slne.surf.chat.core.common.rabbit.packet.response.ignore.ManyIgnorelistEntryResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class FindIgnoreListEntriesRequestPacket(
    val playerUuid: SerializableUUID
) : RabbitRequestPacket<ManyIgnorelistEntryResponsePacket>()
