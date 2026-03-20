package dev.slne.surf.chat.core.common.rabbit.packet.request.functionality

import dev.slne.surf.chat.core.common.rabbit.packet.response.functionality.ManyFunctionalitiesResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
object FindAllFunctionalitiesRequestPacket : RabbitRequestPacket<ManyFunctionalitiesResponsePacket>()
