package dev.slne.surf.chat.bukkit.command.chatmotd

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChatMotdRemoveLineCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        integerArgument("line", 1)
        playerExecutor { player, args ->
            val line: Int by args

            chatMotdService.clearMotdLine(line)

            surfChatApi.sendText(player, buildText {
                info("Die Zeile ")
                variableValue(line.toString())
                info(" wurde aus der Chat-MOTD entfernt.")
            })
        }
    }
}