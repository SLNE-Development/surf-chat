package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.command.surfchat.functionality.functionalityCommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin

fun surfChatCommand() = commandAPICommand("surfchat", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT)
    withAliases("sc")

    surfChatLookupCommand()
    surfChatLookupHelpCommand()
    surfChatReloadCommand()
    functionalityCommand()
}