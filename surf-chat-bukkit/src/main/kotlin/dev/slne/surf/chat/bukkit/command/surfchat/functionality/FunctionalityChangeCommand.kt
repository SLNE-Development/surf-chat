package dev.slne.surf.chat.bukkit.command.surfchat.functionality

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.command.argument.niceToggleArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun CommandAPICommand.functionalityChangeCommand() = subcommand("change") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE)
    niceToggleArgument("toggle")
    anyExecutor { player, args ->
        val toggle: Boolean by args

        plugin.launch {
            if (toggle) {
                functionalityService.enableLocalChat(plugin.server)
                player.sendText {
                    appendPrefix()
                    success("Der Chat wurde aktiviert.")
                }

                Bukkit.getOnlinePlayers()
                    .filter { it.hasPermission(SurfChatPermissionRegistry.TEAM_ACCESS) }.forEach {
                        it.sendText {
                            appendPrefix()
                            variableValue(player.name)
                            info(" hat den Chat für den Server ")
                            variableValue(plugin.server.name)
                            info(" aktiviert.")
                        }
                    }
            } else {
                functionalityService.disableLocalChat(plugin.server)
                player.sendText {
                    appendPrefix()
                    success("Der Chat wurde deaktiviert.")
                }

                Bukkit.getOnlinePlayers()
                    .filter { it.hasPermission(SurfChatPermissionRegistry.TEAM_ACCESS) }.forEach {
                        it.sendText {
                            appendPrefix()
                            variableValue(player.name)
                            info(" hat den Chat für den Server ")
                            variableValue(plugin.server.name)
                            info(" deaktiviert.")
                        }
                    }
            }
        }
    }
}