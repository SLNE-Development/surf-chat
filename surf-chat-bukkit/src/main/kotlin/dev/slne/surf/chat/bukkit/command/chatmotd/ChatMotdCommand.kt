package dev.slne.surf.chat.bukkit.command.chatmotd

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry

class ChatMotdCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHATMOTD)

        subcommand(ChatMotdAddLineCommand("addLine"))
        subcommand(ChatMotdRemoveLineCommand("removeLine"))
        subcommand(ChatMotdListLineCommand("listLines"))
        subcommand(ChatMotdToggleCommand("toggleStatus"))
    }
}