package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry

class ChannelAdminCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ADMIN)

        subcommand(ChannelMoveCommand("move"))
        subcommand(ChannelForceDeleteCommand("forceDelete"))
        subcommand(ChannelForceJoinCommand("forceJoin"))
        subcommand(ChannelSpyCommand("spy"))
    }
}
