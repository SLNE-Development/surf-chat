package dev.slne.surf.chat.minestom.command.surfchat

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.config.AiModerationConfig
import dev.slne.surf.chat.core.client.config.chatConfigProvider
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.util.coloredComponent
import kotlin.system.measureTimeMillis

fun CommandAPICommand.surfChatReloadCommand(): CommandAPICommand = withSubcommand(
    subcommand("reload") {
        withPermission(ChatPermissions.COMMAND_SURFCHAT_RELOAD)
        anyExecutor { executor, _ ->
            val ms = measureTimeMillis {
                chatConfigProvider.reload()
                AiModerationConfig.reload()
            }

            executor.sendText {
                appendSuccessPrefix()
                success("Successfully reloaded plugin in ")
                append(ms.coloredComponent(25))
                success("!")
            }
        }
    }
)
