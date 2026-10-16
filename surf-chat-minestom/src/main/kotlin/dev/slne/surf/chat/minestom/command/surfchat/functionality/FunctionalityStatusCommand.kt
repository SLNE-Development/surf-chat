package dev.slne.surf.chat.minestom.command.surfchat.functionality

import dev.slne.surf.api.minestom.command.CommandAPICommand
import dev.slne.surf.api.minestom.command.dsl.anyExecutor
import dev.slne.surf.api.minestom.command.dsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.common.service.FunctionalityService

fun CommandAPICommand.functionalityStatusCommand(): CommandAPICommand = withSubcommand(
    subcommand("status") {
        withPermission(ChatPermissions.COMMAND_SURFCHAT_FUNCTIONALITY_STATUS)
        anyExecutor { player, _ ->
            player.sendText {
                appendInfoPrefix()
                info("Der Chat ist derzeit ")
                variableValue(if (FunctionalityService.getFunctionalities().localChatEnabled) "aktiviert" else "deaktiviert")
                info(".")
            }
        }
    }
)
