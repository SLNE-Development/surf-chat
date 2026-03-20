package dev.slne.surf.chat.core.common.rabbit.packet.response.history

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class ManyHistoriesResponsePacket(
    val histories: List<HistoryEntry>
) : RabbitResponsePacket()