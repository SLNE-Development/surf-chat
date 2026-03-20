package dev.slne.surf.chat.core.common.rabbit.packet.request.history

import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.core.common.rabbit.packet.response.history.ManyHistoriesResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
data class FindHistoriesRequestPacket(
    val filter: HistoryFilter
) : RabbitRequestPacket<ManyHistoriesResponsePacket>()
