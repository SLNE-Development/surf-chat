package dev.slne.surf.chat.bukkit.command.chatmotd

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChatMotdToggleCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        playerExecutor { player, _ ->
            val current = chatMotdService.isMotdEnabled()

            if(current) {
                chatMotdService.disableMotd()
                surfChatApi.sendText(player, buildText {
                    success("Die Chat-MOTD-Funktion wurde deaktiviert.")
                })
            } else {
                chatMotdService.enableMotd()
                surfChatApi.sendText(player, buildText {
                    success("Die Chat-MOTD-Funktion wurde aktiviert.")
                })
            }
        }
    }
}