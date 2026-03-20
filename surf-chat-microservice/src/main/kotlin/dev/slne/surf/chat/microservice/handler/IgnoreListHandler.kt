package dev.slne.surf.chat.microservice.handler

import dev.slne.surf.chat.core.common.rabbit.packet.request.ignore.FindIgnoreListEntriesRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.ignore.UpdateIgnoreRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.response.ignore.ManyIgnorelistEntryResponsePacket
import dev.slne.surf.chat.microservice.repository.ignore.IgnoreRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.coroutines.launch

object IgnoreListHandler {
    @RabbitHandler
    fun handleUpdateIgnoreRequest(packet: UpdateIgnoreRequestPacket) = packet.launch {
        packet.respond(
            PrimitiveResponse.BooleanResponsePacket(
                if (
                    packet.ignored
                )
                    IgnoreRepository.ignore(packet.playerUuid, packet.targetPlayerUuid)
                else
                    IgnoreRepository.unignore(
                        packet.playerUuid,
                        packet.targetPlayerUuid
                    )
            )
        )
    }

    @RabbitHandler
    fun handleFindIgnoreListEntriesRequest(packet: FindIgnoreListEntriesRequestPacket) = packet.launch {
        packet.respond(
            ManyIgnorelistEntryResponsePacket(
                IgnoreRepository.findAllByUuid(packet.playerUuid)
            )
        )
    }
}