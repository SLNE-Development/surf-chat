package dev.slne.surf.chat.bukkit.command.messagelimit

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.filterService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class MessageLimitSetCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_MESSAGELIMIT_SET)
        integerArgument("messages")
        integerArgument("seconds")

        playerExecutor { player, args ->
            val messages: Int by args
            val seconds: Int by args

            filterService.setMessageLimit(seconds, messages)
            player.sendPrefixed {
                success("Das Nachrichten-Limit wurde erfolgreich auf ")
                variableValue("$messages Nachrichten ")
                success("in ")
                variableValue("$seconds Sekunden")
                success(" gesetzt.")
            }
        }
    }
}
