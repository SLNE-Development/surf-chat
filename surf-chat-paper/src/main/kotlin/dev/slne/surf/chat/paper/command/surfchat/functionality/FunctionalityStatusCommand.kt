package dev.slne.surf.chat.paper.command.surfchat.functionality

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.functionalityStatusCommand() = subcommand("status") {
    withPermission(PermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_STATUS)
    anyExecutor { player, _ ->
        player.sendText {
            appendInfoPrefix()
            info("Der Chat ist derzeit ")
            variableValue(if (functionalityService.getFunctionalities().localChatEnabled) "aktiviert" else "deaktiviert")
            info(".")
        }
    }
}