package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun surfChatCommand() = commandAPICommand("surfchat") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT)
    withAliases("sc", "chat")

    surfChatLookupCommand()
}