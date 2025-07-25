package dev.slne.surf.chat.velocity.command

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.chat.velocity.command.argument.playerArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.*
import kotlin.jvm.optionals.getOrNull

private val channel = MinecraftChannelIdentifier.from(Constants.CHANEL_DM)

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

        val currentServer = player.currentServer.getOrNull() ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Ein interner Fehler ist aufgetreten. Bitte versuche es später erneut.")
            }
        }

        currentServer.sendPluginMessage(channel, ByteArrayOutputStream().use { byteStream ->
            DataOutputStream(byteStream).use { out ->
                out.writeUTF(player.uniqueId.toString())
                out.writeUTF(target.uniqueId.toString())
                out.writeUTF(messageId.toString())
                out.writeUTF(message)
                out.writeLong(sentAt)
                out.writeUTF(currentServer.serverInfo.name)
            }
            byteStream.toByteArray()
        })
    }
}