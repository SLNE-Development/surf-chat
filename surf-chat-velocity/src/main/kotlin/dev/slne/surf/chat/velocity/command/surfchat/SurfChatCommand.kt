package dev.slne.surf.chat.velocity.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

import dev.slne.surf.chat.velocity.command.denylist.DenyListCommand
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry

class SurfChatCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT)
        withAliases("sc")

        subcommand(SurfChatDeleteCommand("delete"))
        subcommand(SurfChatChatClearCommand("clear"))
        subcommand(SurfChatLookupCommand("lookup"))
        subcommand(DenyListCommand("denylist"))
        subcommand(SurfChatReloadCommand("reload"))
    }
}
