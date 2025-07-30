package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.coloredComponent
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.system.measureTimeMillis

fun CommandAPICommand.surfChatReloadCommand() = subcommand("reload") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_RELOAD)
    anyExecutor { executor, args ->
        val ms = measureTimeMillis {
            plugin.connectionMessageConfig.reload()
            plugin.chatMotdConfig.reload()
        }

        executor.sendText {
            appendPrefix()
            success("Successfully reloaded plugin in ")
            append(ms.coloredComponent(50))
            success("!")
        }
    }
}