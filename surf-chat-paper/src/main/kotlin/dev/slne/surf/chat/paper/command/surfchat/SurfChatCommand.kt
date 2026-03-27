package dev.slne.surf.chat.paper.command.surfchat

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.paper.command.surfchat.functionality.functionalityCommand
import dev.slne.surf.chat.paper.permission.PermissionRegistry

fun surfChatCommand() = commandAPICommand("surfchat") {
    withPermission(PermissionRegistry.COMMAND_SURFCHAT)
    withAliases("sc")

    surfChatLookupCommand()
    surfChatLookupHelpCommand()
    surfChatReloadCommand()
    functionalityCommand()
}