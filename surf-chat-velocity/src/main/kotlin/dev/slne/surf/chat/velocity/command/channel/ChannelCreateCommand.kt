package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText

class ChannelCreateCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(TextArgument("name"))
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_CREATE)
        playerExecutor { player, args ->
            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val name: String by args

                if (channelService.getChannel(user) != null) {
                    user.sendText {
                        error("Du bist bereits in einem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (channelService.getChannel(name) != null) {
                    user.sendText{
                        error("Es existiert bereits ein Nachrichtenkanal mit dem Namen")
                        variableValue(name)
                        error(".")
                    }
                    return@launch
                }

                if (!isValidString(name)) {
                    user.sendText {
                        error("Der Kanalname muss zwischen 3 und 16 Zeichen lang sein und darf nur Buchstaben und Zahlen enthalten.")
                    }
                    return@launch
                }

                channelService.createChannel(name, user)
                user.sendText {
                    success("Du hast den Nachrichtenkanal ")
                    variableValue(name)
                    success(" erfolgreich erstellt.")
                }
            }
        }
    }

    fun isValidString(input: String): Boolean {
        return input.length in 3..16 && input.all { it.isLetterOrDigit() }
    }
}
