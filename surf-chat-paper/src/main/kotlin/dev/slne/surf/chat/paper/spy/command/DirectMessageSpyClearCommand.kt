package dev.slne.surf.chat.paper.spy.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.spy.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.directMessageSpyClearCommand() = subcommand("clear") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DIRECT_SPY_CLEAR)
    playerExecutor { player, _ ->
        if (!spyService.isPrivateMessageSpying(player.uniqueId)) {
            player.sendText {
                appendPrefix()
                error("Du spionierst aktuell bei keinem Spieler.")
            }
            return@playerExecutor
        }

        spyService.clearPrivateMessageSpies(player.uniqueId)

        player.sendText {
            appendPrefix()
            success("Du spionierst jetzt bei keinem Spieler mehr.")
        }
    }
}