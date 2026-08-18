package dev.slne.surf.chat.paper.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.config.AiModerationConfig
import dev.slne.surf.chat.core.client.config.chatConfigProvider
import dev.slne.surf.chat.core.client.util.coloredComponent
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import kotlin.system.measureTimeMillis

fun CommandAPICommand.surfChatReloadCommand() = subcommand("reload") {
    withPermission(PermissionRegistry.COMMAND_SURFCHAT_RELOAD)
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