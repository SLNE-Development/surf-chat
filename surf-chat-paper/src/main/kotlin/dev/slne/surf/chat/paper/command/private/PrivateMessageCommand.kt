package dev.slne.surf.chat.paper.command.private

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundPrivateMessagePacket
import dev.slne.surf.chat.paper.message.MessageDataImpl
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
    withPermission("surf.chat.command.msg")
    onlineCloudPlayerArgument("target")
    greedyStringArgument("message")

    playerExecutor { player, args ->
        val target: CloudPlayer by args
        val message: String by args
        val sentAt = System.currentTimeMillis()
        val messageId = UUID.randomUUID()
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

        if (player.uniqueId == target.uuid) {
            return@playerExecutor run {
                player.sendText {
                    appendPrefix()
                    error("Du kannst dir selbst keine Nachrichten senden.")
                }
            }
        }

        ServerboundPrivateMessagePacket(data).fireAndForget()
    }
}