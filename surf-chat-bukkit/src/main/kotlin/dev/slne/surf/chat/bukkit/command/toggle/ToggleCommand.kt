package dev.slne.surf.chat.bukkit.command.toggle

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

class ToggleCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.toggle")
        withAliases("toggle")

        subcommand(TogglePmCommand("private-messages"))
        subcommand(ToggleChannelInvitesCommand("channel-invites"))
        subcommand(ToggleSoundCommand("sounds"))
    }
}