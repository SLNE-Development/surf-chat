package dev.slne.surf.chat.paper.command.direct

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.core.api.common.SurfCoreApi
import java.util.*


object ReplyCache {
    val lastMessages = mutableObject2ObjectMapOf<UUID, UUID>()
}

fun replyCommand() = commandAPICommand("reply") {
    withPermission(PermissionRegistry.COMMAND_REPLY)
    withAliases("r")
    greedyStringArgument("message")

    playerExecutorSuspend { player, args ->
        val message: String by args

        val lastMessagedUuid = ReplyCache.lastMessages[player.uniqueId]

        if (lastMessagedUuid == null) {
            player.sendText {
                appendErrorPrefix()
                error("Du hast noch keine privaten Nachrichten erhalten, auf die du antworten könntest.")
            }
            return@playerExecutorSuspend
        }

        if (lastMessagedUuid == player.uniqueId) {
            player.sendText {
                appendErrorPrefix()
                error("Du kannst dir selbst keine privaten Nachrichten senden!")
            }
            return@playerExecutorSuspend
        }

        val lastMessagedPlayer = SurfCoreApi.getPlayer(lastMessagedUuid)

        if (lastMessagedPlayer == null) {
            player.sendText {
                appendErrorPrefix()
                error("Der Spieler ist nicht mehr online.")
            }
            return@playerExecutorSuspend
        }

        DirectMessageAccess.sendMessage(player, message, lastMessagedUuid)
    }
}