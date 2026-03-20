package dev.slne.surf.chat.core.common.rabbit.packet.request

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.serialization.Serializable

@Serializable
data class CreateHistoryEntryRequestPacket(
    val historyEntry: HistoryEntry
) : RabbitRequestPacket<PrimitiveResponse.BooleanResponsePacket>()
