package dev.slne.surf.chat.bukkit.command.messagelimit

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.filterService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class MessageLimitInfoCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        playerExecutor { player, _ ->
            val limit = filterService.getMessageLimit()

            surfChatApi.sendText(player, buildText {
                primary("Das Nachrichten Limit ist auf ")
                variableValue("${limit.first}/${limit.second} mps (${limit.first} Nachrichten in ${limit.second} Sekunden)")
                success(" gesetzt.")
            })
        }
    }
}