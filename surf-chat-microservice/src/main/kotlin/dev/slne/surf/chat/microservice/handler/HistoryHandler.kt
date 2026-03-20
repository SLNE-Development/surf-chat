package dev.slne.surf.chat.microservice.handler

import dev.slne.surf.chat.core.common.rabbit.packet.request.history.CreateHistoryEntryRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.history.FindHistoriesRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.history.MarkHistoryEntryDeletedRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.response.history.ManyHistoriesResponsePacket
import dev.slne.surf.chat.microservice.repository.history.HistoryRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.coroutines.launch

object HistoryHandler {
    @RabbitHandler
    fun handleHistoryCreatePacket(packet: CreateHistoryEntryRequestPacket) = packet.launch {
        packet.respond(
            PrimitiveResponse.BooleanResponsePacket(
                HistoryRepository.createHistoryEntry(
                    packet.historyEntry
                )
            )
        )
    }

    @RabbitHandler
    fun handleHistoryFindPacket(packet: FindHistoriesRequestPacket) = packet.launch {
        packet.respond(ManyHistoriesResponsePacket(HistoryRepository.findHistories(packet.filter)))
    }

    @RabbitHandler
    fun handleMarkHistoryDeletedPacket(packet: MarkHistoryEntryDeletedRequestPacket) = packet.launch {
        packet.respond(
            PrimitiveResponse.BooleanResponsePacket(
                HistoryRepository.markDeleted(
                    messageUuid = packet.messageUuid,
                    deletedBy = packet.deletedBy,
                    deletionReason = packet.deletionReason,
                    deletedAt = packet.deletedAt,
                )
            )
        )
    }
}