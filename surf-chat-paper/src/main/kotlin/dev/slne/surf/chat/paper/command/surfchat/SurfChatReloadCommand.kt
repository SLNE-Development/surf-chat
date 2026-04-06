package dev.slne.surf.chat.paper.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.paper.config.AiModerationConfig
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.coloredComponent
import kotlin.system.measureTimeMillis

fun CommandAPICommand.surfChatReloadCommand() = subcommand("reload") {
    withPermission(PermissionRegistry.COMMAND_SURFCHAT_RELOAD)
    anyExecutor { executor, _ ->
        val ms = measureTimeMillis {
            plugin.surfChatConfig.reload()
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