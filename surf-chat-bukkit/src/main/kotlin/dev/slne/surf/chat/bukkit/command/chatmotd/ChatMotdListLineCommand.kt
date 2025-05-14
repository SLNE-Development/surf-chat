package dev.slne.surf.chat.bukkit.command.chatmotd

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChatMotdListLineCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerExecutor { player, _ ->
            surfChatApi.sendText(player, buildText {
                info("Die Chat-MOTD ist aktuell auf ")
                appendNewline()
                append(chatMotdService.getMotd())
                appendNewline()
                info(" gesetzt.")
            })
        }
    }
}