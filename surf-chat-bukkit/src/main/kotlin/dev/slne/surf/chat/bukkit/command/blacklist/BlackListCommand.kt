package dev.slne.surf.chat.bukkit.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

class BlackListCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.blacklist")

        subcommand(BlacklistAddCommand("add"))
        subcommand(BlacklistRemoveCommand("remove"))
        subcommand(BlackListListCommand("list"))
    }
}