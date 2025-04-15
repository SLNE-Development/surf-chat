package dev.slne.surf.chat.bukkit.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

class BlackListCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.blacklist")
        withUsage("/blacklist <add> <word> [reason]", "/blacklist <remove> <word>", "/blacklist <list>", "/blacklist <reload>")
        withHelp("Modify the blacklist", "Modify and manage the blacklist to prevent certain words from being used in chat.")

        subcommand(BlacklistAddCommand("add"))
        subcommand(BlacklistRemoveCommand("remove"))
        subcommand(BlackListListCommand("list"))
        subcommand(BlacklistUpdateCommand("reload"))
    }
}