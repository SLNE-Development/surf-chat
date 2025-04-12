package dev.slne.surf.chat.bukkit.command.chatmotd

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChatMotdAddLineCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        integerArgument("line", 1)
        greedyStringArgument("content")

        playerExecutor { player, args ->
            val line: Int by args
            val content: String by args

            chatMotdService.setMotdLine(line, content)

            surfChatApi.sendText(player, buildText {
                primary("Die Zeile ")
                info(line.toString())
                primary(" wurde auf ")
                info(content)
                primary(" gesetzt.")
            })
        }
    }
}