package dev.slne.surf.chat.bukkit.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import org.bukkit.entity.Player
import java.util.*

val latestDirectMessages = mutableObject2ObjectMapOf<UUID, UUID>()

fun directMessageCommand() = commandAPICommand("msg") {
    withAliases("dm", "w", "whisper", "tell", "pm")
    withPermission("surf.chat.command.msg")
    //playerArgument("target")
    greedyStringArgument("message")

    playerExecutor { player, args ->
        val target: Player by args
        val message: String by args
        val sentAt = System.currentTimeMillis()
        val messageId = UUID.randomUUID()

        if (player == target) {
            return@playerExecutor run {
                player.sendText {
                    appendPrefix()
                    error("Du kannst dir keine Nachrichten senden.")
                }
            }
        }

        latestDirectMessages[target.uniqueId] = player.uniqueId
    }
}