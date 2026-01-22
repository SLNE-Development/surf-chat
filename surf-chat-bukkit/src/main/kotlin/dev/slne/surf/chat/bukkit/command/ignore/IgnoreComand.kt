package dev.slne.surf.chat.bukkit.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.command.argument.userStringArgument
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ignore
import dev.slne.surf.chat.bukkit.util.ignores
import dev.slne.surf.chat.bukkit.util.toUserOrThrow
import dev.slne.surf.chat.bukkit.util.unignore
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun ignoreCommand() = commandAPICommand("ignore", plugin) {
    withPermission(PermissionRegistry.COMMAND_IGNORE)
    ignoreListCommand()
    userStringArgument("target")
    playerExecutor { player, args ->
        val target: String by args

        plugin.launch {
            val targetUser = userService.findOrLoadByName(target) ?: run {
                player.sendText {
                    appendErrorPrefix()
                    error("Der Spieler ")
                    variableValue(target)
                    error(" wurde nicht gefunden.")
                }
                return@launch
            }

            val user = player.toUserOrThrow()

            if (player.uniqueId == targetUser.uuid) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du kannst dich nicht selbst ignorieren.")
                }
                return@launch
            }

            if (user.ignores(targetUser.uuid)) {
                user.unignore(targetUser.uuid)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du ignorierst nun nicht mehr ")
                    variableValue(targetUser.name)
                    success(".")
                }
                return@launch
            }

            user.ignore(targetUser.name, targetUser.uuid)

            player.sendText {
                appendSuccessPrefix()
                success("Du ignorierst nun ")
                variableValue(targetUser.name)
                success(".")
            }
        }
    }
}