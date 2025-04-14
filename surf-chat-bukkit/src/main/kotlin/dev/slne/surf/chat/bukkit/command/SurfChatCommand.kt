package dev.slne.surf.chat.bukkit.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.command.blacklist.BlackListCommand
import dev.slne.surf.chat.bukkit.command.chatmotd.ChatMotdCommand

class SurfChatCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat")
        withAliases("sc")

        subcommand(SurfChatDeleteCommand("delete"))
        subcommand(SurfChatChatClearCommand("clear"))
        subcommand(SurfChatLookupCommand("lookup"))
        subcommand(ChatMotdCommand("chatMotd"))
        subcommand(BlackListCommand("blacklist"))
    }
}
