package dev.slne.surf.chat.bukkit.command.surfchat.functionality

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.functionalityStatusCommand() = subcommand("status") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_STATUS)
    anyExecutor { player, _ ->
        player.sendText {
            appendPrefix()
            info("Der Chat ist derzeit ")
            variableValue(if (functionalityService.isLocalChatEnabled()) "aktiviert" else "deaktiviert")
            info(".")
        }
    }
}