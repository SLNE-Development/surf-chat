package dev.slne.surf.chat.microservice.handler

import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.FindAllFunctionalitiesRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.FindFunctionalityRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.UpsertFunctionalityRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.response.functionality.FunctionalitiesResponsePacket
import dev.slne.surf.chat.core.common.rabbit.packet.response.functionality.ManyFunctionalitiesResponsePacket
import dev.slne.surf.chat.microservice.repository.functionality.FunctionalityRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.coroutines.launch

object FunctionalityHandler {
    @RabbitHandler
    fun handleUpsertFunctionality(packet: UpsertFunctionalityRequestPacket) = packet.launch {
        packet.respond(
            PrimitiveResponse.BooleanResponsePacket(
                FunctionalityRepository.updateOrCreate(
                    packet.serverName,
                    packet.functionalities
                )
            )
        )
    }

    @RabbitHandler
    fun handleFindAllFunctionalities(packet: FindAllFunctionalitiesRequestPacket) = packet.launch {
        packet.respond(
            ManyFunctionalitiesResponsePacket(
                FunctionalityRepository.findAll()
            )
        )
    }

    @RabbitHandler
    fun handleFindFunctionalitiesByServer(packet: FindFunctionalityRequestPacket) = packet.launch {
        packet.respond(
            FunctionalitiesResponsePacket(
                FunctionalityRepository.findByServerOrCreate(packet.serverName)
            )
        )
    }
}