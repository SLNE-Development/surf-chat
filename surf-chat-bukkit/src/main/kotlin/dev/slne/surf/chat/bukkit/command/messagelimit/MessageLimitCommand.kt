package dev.slne.surf.chat.bukkit.command.messagelimit

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

class MessageLimitCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.messagelimit")
        subcommand(MessageLimitSetCommand("setLimit"))
        subcommand(MessageLimitInfoCommand("info"))
    }
}