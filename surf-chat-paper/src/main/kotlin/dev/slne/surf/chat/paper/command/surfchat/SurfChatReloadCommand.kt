package dev.slne.surf.chat.paper.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.paper.listener.ConnectListener
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.coloredComponent
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.system.measureTimeMillis

fun CommandAPICommand.surfChatReloadCommand() = subcommand("reload") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_RELOAD)
    anyExecutor { executor, _ ->
        val ms = measureTimeMillis {
            plugin.surfChatConfig.reload()
            plugin.discordConfig.reload()

            ConnectListener.ALREADY_REQUESTED = false
        }

        executor.sendText {
            appendPrefix()
            success("Successfully reloaded plugin in ")
            append(ms.coloredComponent(25))
            success("!")
        }
    }
}