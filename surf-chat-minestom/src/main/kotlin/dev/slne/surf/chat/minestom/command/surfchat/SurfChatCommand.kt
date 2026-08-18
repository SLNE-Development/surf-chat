package dev.slne.surf.chat.minestom.command.surfchat

import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandAPICommand
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.minestom.command.surfchat.functionality.functionalityCommand

fun surfChatCommand() = commandAPICommand("surfchat") {
    withPermission(ChatPermissions.COMMAND_SURFCHAT)
    withAliases("sc")

    surfChatLookupCommand()
    surfChatLookupHelpCommand()
    surfChatReloadCommand()
    functionalityCommand()
}
