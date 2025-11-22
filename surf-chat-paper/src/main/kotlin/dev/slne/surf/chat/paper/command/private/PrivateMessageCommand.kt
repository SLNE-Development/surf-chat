package dev.slne.surf.chat.paper.command.private

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryLogPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundPrivateMessagePacket
import dev.slne.surf.chat.core.common.permission.ChatPermissions
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.client.paper.command.args.onlineCloudPlayerArgument
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import java.util.*

fun directMessageCommand() = commandAPICommand("msg") {
    withAliases("dm", "w", "whisper", "tell", "pm")
    withPermission(ChatPermissions.COMMAND_PRIVATE)
    onlineCloudPlayerArgument("target")
    greedyStringArgument("message")

    playerExecutor { player, args ->
        val target: CloudPlayer by args
        val message: String by args
        val sentAt = System.currentTimeMillis()
        val messageId = UUID.randomUUID()
        val data = MessageData(
            Component.text(message),
            messageId,
            player.toCloudPlayer() ?: return@playerExecutor,
            target,
            sentAt,
            CloudServer.current(),
            null,
            null,
            MessageType.PRIVATE
        )

        if (player.uniqueId == target.uuid) {
            return@playerExecutor run {
                player.sendText {
                    appendPrefix()
                    error("Du kannst dir selbst keine Nachrichten senden.")
                }
            }
        }

        SyncValues.latestPrivateMessages.removeIf { it.first == player.uniqueId }
        SyncValues.latestPrivateMessages.add(player.uniqueId to target.uuid)

        ServerboundPrivateMessagePacket(data).fireAndForget()
        ServerboundHistoryLogPacket(data).fireAndForget()
    }
}