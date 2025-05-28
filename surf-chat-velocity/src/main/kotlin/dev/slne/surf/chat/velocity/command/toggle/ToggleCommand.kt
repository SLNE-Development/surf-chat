package dev.slne.surf.chat.velocity.command.toggle

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry

class ToggleCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_TOGGLE)
        withAliases("toggle")

        subcommand(TogglePmCommand("private-messages"))
        subcommand(ToggleChannelInvitesCommand("channel-invites"))
        subcommand(ToggleSoundCommand("sounds"))
    }
}