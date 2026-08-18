package dev.slne.surf.chat.minestom.command.surfchat.functionality

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.minestom.command.argument.niceToggleArgument
import dev.slne.surf.core.api.common.SurfCoreApi
import net.minestom.server.entity.Player

fun CommandAPICommand.functionalityChangeCommand(): CommandAPICommand = withSubcommand(
    subcommand("change") {
        withPermission(ChatPermissions.COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE)
        niceToggleArgument("toggle")
        anyExecutorSuspend { player, args ->
            val toggle: Boolean by args

            if (toggle) {
                FunctionalityService.updateLocalFunctionalities {
                    it.copy(localChatEnabled = true)
                }

                player.sendText {
                    appendSuccessPrefix()
                    success("Der Chat wurde aktiviert.")
                }

                val message = buildText {
                    appendInfoPrefix()
                    variableValue(if (player is Player) player.username else "Console")
                    info(" hat den Chat für den Server ")
                    variableValue(SurfCoreApi.getCurrentServerName())
                    info(" aktiviert.")
                }

                ChatPlatform.broadcast(message, ChatPermissions.TEAM_NOTIFY_FUNCTIONALITY)
            } else {
                FunctionalityService.updateLocalFunctionalities {
                    it.copy(localChatEnabled = false)
                }
                player.sendText {
                    appendSuccessPrefix()
                    success("Der Chat wurde deaktiviert.")
                }

                val message = buildText {
                    appendSuccessPrefix()
                    variableValue(if (player is Player) player.username else "Console")
                    info(" hat den Chat für den Server ")
                    variableValue(SurfCoreApi.getCurrentServerName())
                    info(" deaktiviert.")
                }

                ChatPlatform.broadcast(message, ChatPermissions.TEAM_NOTIFY_FUNCTIONALITY)
            }
        }
    }
)
