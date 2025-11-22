package dev.slne.surf.chat.server.netty.listener

import dev.slne.surf.chat.core.common.netty.packet.clientbound.history.ClientboundHistoryLookupResultPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.ServerboundDenylistActionPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryLogPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryLookupPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryMarkDeletedPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundPrivateMessagePacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundTeamChatMessagePacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundTeamMessagePacket
import dev.slne.surf.chat.core.common.permission.ChatPermissions
import dev.slne.surf.chat.server.database.repository.DenylistActionRepository
import dev.slne.surf.chat.server.database.repository.HistoryRepository
import dev.slne.surf.chat.server.message.ServerMessageFormatterImpl
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.common.server.CloudServerManager
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.springframework.stereotype.Component

@Component
class ServerChatPacketListener(
    private val historyRepository: HistoryRepository,
    private val denylistActionRepository: DenylistActionRepository
) {
    @SurfNettyPacketHandler
    suspend fun handleTeamChatMessagePacket(packet: ServerboundTeamChatMessagePacket) {
        val formatter = ServerMessageFormatterImpl(packet.messageData.message)
        CloudServerManager.broadcast(
            formatter.formatTeamchat(packet.messageData),
            ChatPermissions.COMMAND_TEAMCHAT,
            false
        )
    }

    @SurfNettyPacketHandler
    suspend fun handleTeamMessagePacket(packet: ServerboundTeamMessagePacket) {
        CloudServerManager.broadcast(packet.message, ChatPermissions.TEAM_NOTIFY, false)
    }

    @SurfNettyPacketHandler
    suspend fun handlePrivateMessagePacket(packet: ServerboundPrivateMessagePacket) {
        val formatter = ServerMessageFormatterImpl(packet.messageData.message)

        packet.messageData.sender.sendText {
            append(formatter.formatOutgoingPm(packet.messageData))
        }

        packet.messageData.receiver?.sendText {
            append(formatter.formatIncomingPm(packet.messageData))
        }
    }

    @SurfNettyPacketHandler
    suspend fun handleHistoryLogPacket(packet: ServerboundHistoryLogPacket) {
        historyRepository.logMessage(packet.messageData)
    }

    @SurfNettyPacketHandler
    suspend fun handleHistoryMarkDeletedPacket(packet: ServerboundHistoryMarkDeletedPacket) {
        historyRepository.markDeleted(packet.messageUuid, packet.deletedBy)
    }

    @SurfNettyPacketHandler
    suspend fun handleHistoryLookupPacket(packet: ServerboundHistoryLookupPacket) {
        packet.respond(
            ClientboundHistoryLookupResultPacket(
                historyRepository.findHistoryEntry(
                    packet.filter
                )
            )
        )
    }

    @SurfNettyPacketHandler
    suspend fun handleDenylistActionPacket(packet: ServerboundDenylistActionPacket) {
        denylistActionRepository.makeAction(
            packet.messageId,
            packet.denylistEntry,
            packet.signature,
            packet.player
        )
    }
}