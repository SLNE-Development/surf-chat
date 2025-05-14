package dev.slne.surf.chat.bukkit.command.messagelimit

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry

class MessageLimitCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_MESSAGELIMIT)
        subcommand(MessageLimitSetCommand("setLimit"))
        subcommand(MessageLimitInfoCommand("info"))
    }
}