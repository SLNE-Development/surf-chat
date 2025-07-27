package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelCreateCommand() = subcommand("create") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_CREATE)
    textArgument("name")
    playerExecutor { player, args ->
        plugin.launch {
            val user = player.user() ?: return@launch
            val name: String by args

            if (channelService.getChannel(user) != null) {
                player.sendText {
                    error("Du bist bereits in einem Nachrichtenkanal.")
                }
                return@launch
            }

            if (channelService.getChannel(name) != null) {
                player.sendText {
                    error("Es existiert bereits ein Nachrichtenkanal mit dem Namen")
                    variableValue(name)
                    error(".")
                }
                return@launch
            }

            if (!name.isValidChannelName()) {
                player.sendText {
                    error("Der Kanalname muss zwischen 3 und 16 Zeichen lang sein und darf nur Buchstaben und Zahlen enthalten.")
                }
                return@launch
            }

            channelService.createChannel(name, user)
            player.sendText {
                success("Du hast den Nachrichtenkanal ")
                variableValue(name)
                success(" erfolgreich erstellt.")
            }
        }
    }
}

fun String.isValidChannelName(): Boolean {
    return this.length in 3..16 && this.all { it.isLetterOrDigit() }
}
