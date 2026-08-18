package dev.slne.surf.chat.minestom.command.direct

import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.signedMessageArgument
import dev.slne.minestom.lobby.api.player.LobbyPlayer
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.service.ReplyCache
import dev.slne.surf.chat.minestom.service.DirectMessageService
import dev.slne.surf.core.api.common.SurfCoreApi
import net.kyori.adventure.chat.SignedMessage

fun replyCommand() = commandAPICommand("reply") {
    withPermission(ChatPermissions.COMMAND_REPLY)
    withAliases("r")
    signedMessageArgument("message")

    playerExecutorSuspend { player, args ->
        val message: SignedMessage by args
        val lastMessagedUuid = ReplyCache.getLastTarget(player.uuid)

        if (lastMessagedUuid == null) {
            player.sendText {
                appendErrorPrefix()
                error("Du hast noch keine privaten Nachrichten erhalten, auf die du antworten könntest.")
            }
            return@playerExecutorSuspend
        }

        if (lastMessagedUuid == player.uuid) {
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

        DirectMessageService.sendMessage(player as LobbyPlayer, message, lastMessagedUuid)
    }
}
