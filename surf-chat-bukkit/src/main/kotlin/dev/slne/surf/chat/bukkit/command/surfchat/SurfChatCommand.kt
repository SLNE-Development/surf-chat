package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.command.denylist.DenyListCommand
import dev.slne.surf.chat.bukkit.command.chatmotd.ChatMotdCommand
import dev.slne.surf.chat.bukkit.command.messagelimit.MessageLimitCommand
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry

class SurfChatCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT)
        withAliases("sc")

        subcommand(SurfChatDeleteCommand("delete"))
        subcommand(SurfChatChatClearCommand("clear"))
        subcommand(SurfChatLookupCommand("lookup"))
        subcommand(ChatMotdCommand("chatMotd"))
        subcommand(DenyListCommand("denylist"))
        subcommand(MessageLimitCommand("messageLimit"))
        subcommand(SurfChatReloadCommand("reload"))
    }
}
