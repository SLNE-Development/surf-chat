package dev.slne.surf.chat.paper.command.surfchat.functionality

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.core.client.functionality.functionalityService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.cloud.api.client.paper.command.args.cloudServerArgument
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.functionalityStatusCommand() = subcommand("status") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_STATUS)
    cloudServerArgument("server")
    anyExecutor { player, args ->
        val server: CloudServer by args

        player.sendText {
            appendPrefix()
            info("Der Chat ist derzeit für den Server ")
            variableValue(if (functionalityService.isEnabled(server.name)) " aktiviert" else " deaktiviert")
            info(".")
        }
    }
}