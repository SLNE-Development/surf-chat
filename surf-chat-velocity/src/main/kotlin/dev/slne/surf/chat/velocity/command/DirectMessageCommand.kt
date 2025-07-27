package dev.slne.surf.chat.velocity.command

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.chat.core.DirectMessageUpdateType
import dev.slne.surf.chat.velocity.command.argument.playerArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.*
import kotlin.jvm.optionals.getOrNull

private val channel = MinecraftChannelIdentifier.from(Constants.CHANNEL_DM)

fun directMessageCommand() = commandAPICommand("msg") {
    withAliases("dm", "w", "whisper", "tell")
    withPermission("surf.chat.command.msg")
    playerArgument("target")
    greedyStringArgument("message")

    playerExecutor { player, args ->
        val target: Player by args
        val message: String by args
        val sentAt = System.currentTimeMillis()
        val messageId = UUID.randomUUID()

        val senderServer = player.currentServer.getOrNull() ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Ein interner Fehler ist aufgetreten. Bitte versuche es später erneut.")
            }
        }

        val targetServer = target.currentServer.getOrNull() ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Der Spieler ist nicht auf einem Server.")
            }
        }

        senderServer.sendPluginMessage(
            channel,
            ByteArrayOutputStream().use { byteStream ->
                DataOutputStream(byteStream).use { out ->
                    out.writeUTF(DirectMessageUpdateType.SEND_AND_LOG_MESSAGE.toString())
                    out.writeUTF(player.uniqueId.toString())
                    out.writeUTF(player.username)
                    out.writeUTF(target.uniqueId.toString())
                    out.writeUTF(target.username)
                    out.writeUTF(messageId.toString())
                    out.writeUTF(message)
                    out.writeLong(sentAt)
                    out.writeUTF(senderServer.serverInfo.name)
                }
                byteStream.toByteArray()
            })

        targetServer.sendPluginMessage(channel, ByteArrayOutputStream().use { byteStream ->
            DataOutputStream(byteStream).use { out ->
                out.writeUTF(DirectMessageUpdateType.RECEIVE_MESSAGE.toString())
                out.writeUTF(player.uniqueId.toString())
                out.writeUTF(player.username)
                out.writeUTF(target.uniqueId.toString())
                out.writeUTF(target.username)
                out.writeUTF(messageId.toString())
                out.writeUTF(message)
                out.writeLong(sentAt)
                out.writeUTF(senderServer.serverInfo.name)
            }
            byteStream.toByteArray()
        })
    }
}