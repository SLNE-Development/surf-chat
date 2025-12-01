package dev.slne.surf.chat.paper.command.private

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryLogPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundPrivateMessagePacket
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.client.paper.command.args.onlineCloudPlayerArgument
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import java.util.*

fun directMessageCommand() = commandAPICommand("msg") {
    withAliases("dm", "w", "whisper", "tell", "pm")
    withPermission(SurfChatPermissionRegistry.COMMAND_TELL)
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
            player.uniqueId,
            target.uuid,
            sentAt,
            CloudServer.current().name,
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

        SyncValues.latestPrivateMessages.removeIf { it.user == player.uniqueId }
        SyncValues.latestPrivateMessages.add(
            SyncValues.LastPrivateMessage(
                player.uniqueId,
                target.uuid
            )
        )

        SyncValues.latestPrivateMessages.removeIf { it.target == target.uuid }
        SyncValues.latestPrivateMessages.add(
            SyncValues.LastPrivateMessage(
                target.uuid,
                player.uniqueId
            )
        )



        ServerboundPrivateMessagePacket(data).fireAndForget()
        ServerboundHistoryLogPacket(data).fireAndForget()
    }
}