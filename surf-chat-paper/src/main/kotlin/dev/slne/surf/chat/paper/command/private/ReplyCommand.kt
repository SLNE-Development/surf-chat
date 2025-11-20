package dev.slne.surf.chat.paper.command.private

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.client.ChatPermissions
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryLogPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundPrivateMessagePacket
import dev.slne.surf.chat.paper.message.MessageDataImpl
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import java.util.*

fun replyCommand() = commandAPICommand("reply") {
    withAliases("r")
    withPermission(ChatPermissions.COMMAND_REPLY)
    greedyStringArgument("message")

    playerExecutor { player, args ->
        val message: String by args
        val sentAt = System.currentTimeMillis()
        val messageId = UUID.randomUUID()

        val targetUuid = latestPrivateMessages.firstOrNull { it.first == player.uniqueId }?.second
            ?: return@playerExecutor run {
                player.sendText {
                    appendPrefix()
                    error("Du hast noch keine Nachrichten erhalten.")
                }
            }

        val target = CloudPlayer[targetUuid] ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Du hast noch keine Nachrichten erhalten.")
            }
        }

        if (player.uniqueId == target) {
            return@playerExecutor run {
                player.sendText {
                    appendPrefix()
                    error("Du kannst dir keine Nachrichten senden.")
                }
            }
        }

        val data = MessageDataImpl(
            Component.text(message),
            player.toCloudPlayer() ?: return@playerExecutor,
            target,
            sentAt,
            messageId,
            CloudServer.current(),
            null,
            null,
            MessageType.PRIVATE
        )

        ServerboundPrivateMessagePacket(data).fireAndForget()
        ServerboundHistoryLogPacket(data).fireAndForget()
    }
}