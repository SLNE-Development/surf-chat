package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun CommandAPICommand.surfChatLookupCommand() = subcommand("lookup") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT)
    withAliases("sc", "chat")

    playerExecutor { player, args ->
        
    }
}