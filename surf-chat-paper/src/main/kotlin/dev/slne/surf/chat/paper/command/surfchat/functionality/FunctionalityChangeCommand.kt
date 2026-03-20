package dev.slne.surf.chat.paper.command.surfchat.functionality

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.core.common.service.functionalityService
import dev.slne.surf.chat.paper.command.argument.niceToggleArgument
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.surfapi.bukkit.api.command.executors.anyExecutorSuspend
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun CommandAPICommand.functionalityChangeCommand() = subcommand("change") {
    withPermission(PermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE)
    niceToggleArgument("toggle")
    anyExecutorSuspend { player, args ->
        val toggle: Boolean by args

        if (toggle) {
            functionalityService.updateLocalFunctionalities {
                it.copy(localChatEnabled = true)
            }

            player.sendText {
                appendSuccessPrefix()
                success("Der Chat wurde aktiviert.")
            }

            val message = buildText {
                appendInfoPrefix()
                variableValue(player.name)
                info(" hat den Chat für den Server ")
                variableValue(surfCoreApi.getCurrentServerName())
                info(" aktiviert.")
            }

            Bukkit.broadcast(message, PermissionRegistry.TEAM_NOTIFY_FUNCTIONALITY)
        } else {
            functionalityService.updateLocalFunctionalities {
                it.copy(localChatEnabled = false)
            }
            player.sendText {
                appendSuccessPrefix()
                success("Der Chat wurde deaktiviert.")
            }

            val message = buildText {
                appendSuccessPrefix()
                variableValue(player.name)
                info(" hat den Chat für den Server ")
                variableValue(surfCoreApi.getCurrentServerName())
                info(" deaktiviert.")
            }

            Bukkit.broadcast(message, PermissionRegistry.TEAM_NOTIFY_FUNCTIONALITY)
        }
    }
}