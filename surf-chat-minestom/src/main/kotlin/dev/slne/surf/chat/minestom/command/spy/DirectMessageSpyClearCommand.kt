package dev.slne.surf.chat.minestom.command.spy

import dev.slne.minestom.lobby.api.command.commandapi.CommandTree
import dev.slne.minestom.lobby.api.command.commandapi.dsl.literalArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.common.service.SpyService

fun CommandTree.directMessageSpyClearCommand(): CommandTree = literalArgument("#clear") {
    withPermission(ChatPermissions.COMMAND_DIRECT_SPY_CLEAR)
    playerExecutor { player, _ ->
        if (!SpyService.isPrivateMessageSpying(player.uuid)) {
            player.sendText {
                appendErrorPrefix()
                error("Du spionierst aktuell bei keinem Spieler.")
            }
            return@playerExecutor
        }

        SpyService.clearPrivateMessageSpies(player.uuid)

        player.sendText {
            appendSuccessPrefix()
            success("Du spionierst jetzt bei keinem Spieler mehr.")
        }
    }
}
