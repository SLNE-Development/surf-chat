package dev.slne.surf.chat.bukkit.command.chatmotd

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

class ChatMotdCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.chatmotd")

        subcommand(ChatMotdAddLineCommand("addLine"))
        subcommand(ChatMotdRemoveLineCommand("removeLine"))
        subcommand(ChatMotdListLineCommand("listLines"))
        subcommand(ChatMotdToggleCommand("toggleStatus"))
    }
}