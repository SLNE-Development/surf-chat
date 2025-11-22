package dev.slne.surf.chat.paper.command.surfchat.functionality

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.core.client.functionality.functionalityService
import dev.slne.surf.chat.paper.command.argument.niceToggleArgument
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.cloud.api.client.paper.command.args.cloudServerArgument
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun CommandAPICommand.functionalityChangeCommand() = subcommand("change") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE)
    cloudServerArgument("server")
    niceToggleArgument("toggle")
    anyExecutor { player, args ->
        val toggle: Boolean by args
        val server: CloudServer by args

        plugin.launch {
            if (toggle) {
                functionalityService.enableFunctionality(server.name)
                player.sendText {
                    appendPrefix()
                    success("Der Chat wurde für den Server ")
                    variableValue(server.name)
                    success(" aktiviert.")
                }

                Bukkit.getOnlinePlayers()
                    .filter { it.hasPermission(SurfChatPermissionRegistry.TEAM_NOTIFY_FUNCTIONALITY) }
                    .forEach {
                        it.sendText {
                            appendPrefix()
                            variableValue(player.name)
                            info(" hat den Chat für den Server ")
                            variableValue(server.name)
                            info(" aktiviert.")
                        }
                    }
            } else {
                functionalityService.disableFunctionality(server.name)
                player.sendText {
                    appendPrefix()
                    success("Der Chat wurde für den Server ")
                    variableValue(server.name)
                    success(" deaktiviert.")
                }

                Bukkit.getOnlinePlayers()
                    .filter { it.hasPermission(SurfChatPermissionRegistry.TEAM_NOTIFY_FUNCTIONALITY) }
                    .forEach {
                        it.sendText {
                            appendPrefix()
                            variableValue(player.name)
                            info(" hat den Chat für den Server ")
                            variableValue(server.name)
                            info(" deaktiviert.")
                        }
                    }
            }
        }
    }
}