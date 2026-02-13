package dev.slne.surf.chat.bukkit.command.spy

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun directMessageSpyCommand() = commandAPICommand("spy") {
    withPermission(PermissionRegistry.COMMAND_DIRECT_SPY)
    directMessageSpyClearCommand()
    entitySelectorArgumentOnePlayer("target")
    playerExecutor { player, args ->
        val target: Player by args

        if (player.uniqueId == target.uniqueId) {
            throw CommandAPI.failWithString("Du kannst dich nicht selbst spionieren!")
        }

        if (target.hasPermission(PermissionRegistry.BYPASS_SPY)) {
            player.sendText {
                appendErrorPrefix()
                error("Du kannst keine Teammitglieder spionieren!")
            }
            return@playerExecutor
        }

        if (spyService.getObservingPlayers(target.uniqueId).contains(player.uniqueId)) {
            spyService.removePrivateMessageSpy(player.uniqueId, target.uniqueId)

            player.sendText {
                appendSuccessPrefix()
                success("Du spionierst nun ")
                variableValue(target.name)
                success("s private Nachrichten nicht mehr.")
            }
        } else {
            spyService.addPrivateMessageSpy(player.uniqueId, target.uniqueId)
            player.sendText {
                appendSuccessPrefix()
                success("Du spionierst nun ")
                variableValue(target.name)
                success("s private Nachrichten.")
            }
        }
    }
}