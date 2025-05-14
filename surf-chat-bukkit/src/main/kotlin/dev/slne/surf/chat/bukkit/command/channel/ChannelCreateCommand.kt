package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelCreateCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(TextArgument("name"))
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_CREATE)
        playerExecutor { player, args ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val name: String by args

                if (channelService.getChannel(player) != null) {
                    user.sendText(buildText {
                        error("Du bist bereits in einem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if (channelService.getChannel(name) != null) {
                    user.sendText(buildText {
                        error("Es existiert bereits ein Nachrichtenkanal mit dem Namen")
                        variableValue(name)
                        error(".")
                    })
                    return@launch
                }

                if (!isValidString(name)) {
                    user.sendText(buildText {
                        error("Der Kanalname muss zwischen 3 und 16 Zeichen lang sein und darf nur Buchstaben und Zahlen enthalten.")
                    })
                    return@launch
                }

                channelService.createChannel(name, user)
                user.sendText(buildText {
                    success("Du hast den Nachrichtenkanal ")
                    variableValue(name)
                    success(" erfolgreich erstellt.")
                })
            }
        }
    }

    fun isValidString(input: String): Boolean {
        return input.length in 3..16 && input.all { it.isLetterOrDigit() }
    }
}
