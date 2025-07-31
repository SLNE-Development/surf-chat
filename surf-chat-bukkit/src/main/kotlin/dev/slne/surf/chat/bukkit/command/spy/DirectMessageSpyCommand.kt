package dev.slne.surf.chat.bukkit.command.spy

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.bukkit.command.argument.userArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun directMessageSpyCommand() = commandAPICommand("spy") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DIRECT_SPY)
    directMessageSpyClearCommand()
    userArgument("target")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val target: User by args

        if (user.uuid == target.uuid) {
            player.sendText {
                appendPrefix()
                error("Du kannst dich nicht selbst spionieren!")
            }
            return@playerExecutor
        }

        if (target.hasPermission(SurfChatPermissionRegistry.TEAM_ACCESS)) {
            player.sendText {
                appendPrefix()
                error("Du kannst keine Teammitglieder spionieren!")
            }
            return@playerExecutor
        }

        if (spyService.getPrivateMessageSpies(target.uuid).contains(player.uniqueId)) {
            spyService.removePrivateMessageSpy(player.uniqueId, target.uuid)

            player.sendText {
                appendPrefix()
                success("Du spionierst nun ")
                variableValue(target.name)
                success("s private Nachrichten nicht mehr.")
            }
        } else {
            spyService.addPrivateMessageSpy(player.uniqueId, target.uuid)
            player.sendText {
                appendPrefix()
                success("Du spionierst nun ")
                variableValue(target.name)
                success("s private Nachrichten.")
            }
        }
    }
}