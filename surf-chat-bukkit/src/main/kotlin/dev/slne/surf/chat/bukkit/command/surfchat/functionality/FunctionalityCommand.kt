package dev.slne.surf.chat.bukkit.command.surfchat.functionality

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun CommandAPICommand.functionalityCommand() = subcommand("functionality") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY)

    functionalityStatusCommand()
    functionalityChangeCommand()
    functionalityListCommand()
}