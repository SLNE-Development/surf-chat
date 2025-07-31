package dev.slne.surf.chat.bukkit.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.bukkit.command.argument.userArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.ignoreService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun ignoreCommand() = commandAPICommand("ignore", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_IGNORE)
    ignoreListCommand()
    userArgument("target")
    playerExecutor { player, args ->
        val target: User by args

        if (player.uniqueId == target.uuid) {
            player.sendText {
                appendPrefix()
                error("Du kannst dich nicht selbst ignorieren.")
            }
            return@playerExecutor
        }

        plugin.launch {
            if (ignoreService.isIgnored(player.uniqueId, target.uuid)) {
                ignoreService.unIgnore(player.uniqueId, target.uuid)

                player.sendText {
                    appendPrefix()
                    success("Du ignorierst nun nicht mehr ")
                    variableValue(target.name)
                    success(".")
                }
                return@launch
            }

            ignoreService.ignore(player.uniqueId, target.uuid)

            player.sendText {
                appendPrefix()
                success("Du ignorierst nun ")
                variableValue(target.name)
                success(".")
            }
        }
    }
}