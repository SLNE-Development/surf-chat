package dev.slne.surf.chat.server.netty.listener

import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundPrivateMessagePacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundTeamMessagePacket
import dev.slne.surf.chat.core.common.permission.ChatPermissions
import dev.slne.surf.chat.server.message.ServerMessageFormatterImpl
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.common.server.CloudServerManager
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.springframework.stereotype.Component

@Component
class ServerChatPacketListener {
    @SurfNettyPacketHandler
    suspend fun handleTeamMessagePacket(packet: ServerboundTeamMessagePacket) {
        val formatter = ServerMessageFormatterImpl(packet.messageData.message)
        CloudServerManager.broadcast(
            formatter.formatTeamchat(packet.messageData),
            ChatPermissions.COMMAND_TEAMCHAT,
            false
        )
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
}