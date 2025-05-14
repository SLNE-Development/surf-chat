package dev.slne.surf.chat.bukkit.command.messagelimit

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.core.service.filterService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class MessageLimitInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_MESSAGELIMIT_INFO)
        playerExecutor { player, _ ->
            val limit = filterService.getMessageLimit()

            surfChatApi.sendText(player, buildText {
                info("Das aktuelle Nachrichten-Limit beträgt ")
                variableValue("${limit.first} Nachrichten")
                info(" in ")
                variableValue("${limit.second} Sekunden")
                info(".")
            })
        }
    }
}