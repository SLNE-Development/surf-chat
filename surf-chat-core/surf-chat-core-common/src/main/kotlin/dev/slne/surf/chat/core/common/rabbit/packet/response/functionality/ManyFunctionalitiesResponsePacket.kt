package dev.slne.surf.chat.core.common.rabbit.packet.response.functionality

import dev.slne.surf.chat.api.functionality.Functionalities
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class ManyFunctionalitiesResponsePacket(
    val functionalities: Map<String, Functionalities>
) : RabbitResponsePacket()
