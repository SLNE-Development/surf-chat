package dev.slne.surf.chat.paper.command.surfchat.functionality

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry

fun CommandAPICommand.functionalityCommand() = subcommand("functionality") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY)

    functionalityStatusCommand()
    functionalityChangeCommand()
    functionalityListCommand()
}