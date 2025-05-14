package dev.slne.surf.chat.bukkit.command.denylist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

class DenyListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.denylist")
        withUsage(
            "/denylist <add> <word> [reason]",
            "/denylist <remove> <word>",
            "/denylist <list>",
            "/denylist <reload>"
        )
        withHelp(
            "Modify the denylist",
            "Modify and manage the denylist to prevent certain words from being used in chat."
        )

        subcommand(DenyListAddCommand("add"))
        subcommand(DenyListRemoveCommand("remove"))
        subcommand(DenyListListCommand("list"))
        subcommand(DenyListUpdateCommand("reload"))
    }
}