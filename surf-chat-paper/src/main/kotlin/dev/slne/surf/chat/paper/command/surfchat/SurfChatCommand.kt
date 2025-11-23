package dev.slne.surf.chat.paper.command.surfchat

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.paper.command.lookup.surfChatLookupCommand
import dev.slne.surf.chat.paper.command.lookup.surfChatLookupHelpCommand
import dev.slne.surf.chat.paper.command.surfchat.functionality.functionalityCommand
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin

fun surfChatCommand() = commandAPICommand("surfchat", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT)
    withAliases("sc")

    surfChatLookupCommand()
    surfChatLookupHelpCommand()
    surfChatReloadCommand()
    functionalityCommand()
}