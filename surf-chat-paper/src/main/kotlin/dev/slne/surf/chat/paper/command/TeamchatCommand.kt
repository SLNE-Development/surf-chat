package dev.slne.surf.chat.paper.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryLogPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundTeamMessagePacket
import dev.slne.surf.chat.paper.message.MessageDataImpl
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
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
        val messageData = MessageDataImpl(
            messageComponent,
            player.toCloudPlayer() ?: return@playerExecutor,
            null,
            System.currentTimeMillis(),
            messageId,
            CloudServer.current(),
            null,
            null,
            MessageType.TEAM
        )

        ServerboundTeamMessagePacket(messageData).fireAndForget()
        ServerboundHistoryLogPacket(messageData).fireAndForget()
    }
}