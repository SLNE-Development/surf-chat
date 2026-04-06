package dev.slne.surf.chat.paper.command.surfchat.functionality

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.paper.permission.PermissionRegistry

fun CommandAPICommand.functionalityStatusCommand() = subcommand("status") {
    withPermission(PermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_STATUS)
    anyExecutor { player, _ ->
        player.sendText {
            appendInfoPrefix()
            info("Der Chat ist derzeit ")
            variableValue(if (FunctionalityService.getFunctionalities().localChatEnabled) "aktiviert" else "deaktiviert")
            info(".")
        }
    }
}