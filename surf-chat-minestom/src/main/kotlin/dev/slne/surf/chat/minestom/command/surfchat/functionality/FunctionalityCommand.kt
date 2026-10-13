package dev.slne.surf.chat.minestom.command.surfchat.functionality

import dev.slne.surf.api.minestom.command.CommandAPICommand
import dev.slne.surf.api.minestom.command.dsl.subcommand
import dev.slne.surf.chat.core.client.permission.ChatPermissions

fun CommandAPICommand.functionalityCommand(): CommandAPICommand = withSubcommand(
    subcommand("functionality") {
        withPermission(ChatPermissions.COMMAND_SURFCHAT_FUNCTIONALITY)

        functionalityStatusCommand()
        functionalityChangeCommand()
        functionalityListCommand()
    }
)
