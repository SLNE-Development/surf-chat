package dev.slne.surf.chat.bukkit.command.denylist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry

class DenyListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_DENYLIST)
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