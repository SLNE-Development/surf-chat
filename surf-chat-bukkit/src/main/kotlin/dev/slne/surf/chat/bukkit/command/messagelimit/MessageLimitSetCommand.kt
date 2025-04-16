package dev.slne.surf.chat.bukkit.command.messagelimit

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.filterService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class MessageLimitSetCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        integerArgument("messages")
        integerArgument("seconds")

        playerExecutor { player, args ->
            val messages: Int by args
            val seconds: Int by args

            filterService.setMessageLimit(seconds, messages)
            surfChatApi.sendText(player, buildText {
                primary("Das Nachrichten Limit wurde auf ")
                variableValue("$messages/$seconds mps")
                success(" gesetzt.")
            })
        }
    }
}
