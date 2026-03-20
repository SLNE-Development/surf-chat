package dev.slne.surf.chat.core.common.rabbit.packet.response.ignore

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class ManyIgnorelistEntryResponsePacket(
    val entries: List<IgnoreListEntry>
) : RabbitResponsePacket()