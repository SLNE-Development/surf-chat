package dev.slne.surf.chat.paper.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryLogPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundTeamChatMessagePacket
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.server.CloudServer
import net.kyori.adventure.text.Component
import java.util.*

fun teamchatCommand() = commandAPICommand("teamchat") {
    withAliases("tc")
    greedyStringArgument("message")
    withPermission(SurfChatPermissionRegistry.COMMAND_TEAMCHAT)

    playerExecutor { player, args ->
        val message: String by args
        val messageComponent = Component.text(message)
        val messageId = UUID.randomUUID()
        val messageData = MessageData(
            messageComponent,
            messageId,
            player.uniqueId,
            null,
            System.currentTimeMillis(),
            CloudServer.current().name,
            null,
            null,
            MessageType.TEAM
        )

        ServerboundTeamChatMessagePacket(messageData).fireAndForget()
        ServerboundHistoryLogPacket(messageData).fireAndForget()
    }
}