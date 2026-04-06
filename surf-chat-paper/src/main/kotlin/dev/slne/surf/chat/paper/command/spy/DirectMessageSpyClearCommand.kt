package dev.slne.surf.chat.paper.command.spy

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.common.service.SpyService
import dev.slne.surf.chat.paper.permission.PermissionRegistry

fun CommandAPICommand.directMessageSpyClearCommand() = subcommand("#clear") {
    withPermission(PermissionRegistry.COMMAND_DIRECT_SPY_CLEAR)
    playerExecutor { player, _ ->
        if (!SpyService.isPrivateMessageSpying(player.uniqueId)) {
            player.sendText {
                appendErrorPrefix()
                error("Du spionierst aktuell bei keinem Spieler.")
            }
            return@playerExecutor
        }

        SpyService.clearPrivateMessageSpies(player.uniqueId)

        player.sendText {
            appendSuccessPrefix()
            success("Du spionierst jetzt bei keinem Spieler mehr.")
        }
    }
}