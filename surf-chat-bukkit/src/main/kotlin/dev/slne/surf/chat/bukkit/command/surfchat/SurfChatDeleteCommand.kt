package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.service.BukkitHistoryService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import java.util.*

class SurfChatDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat.delete")
        stringArgument("messageID")
        playerExecutor { player, args ->
            val messageID = args.getUnchecked<String>("messageID") ?: return@playerExecutor

            if (!historyService.getMessageIds().contains(UUID.fromString(messageID))) {
                surfChatApi.sendText(player, buildText {
                    error("Eine Nachricht mit der ID ")
                    variableValue(messageID)
                    error(" existiert nicht.")
                })
                return@playerExecutor
            }

            if(historyService.deleteMessage(player.name, UUID.fromString(messageID))) {
                surfChatApi.sendText(player, buildText {
                    success("Die Nachricht wurde gelöscht.")
                })
            } else {
                surfChatApi.sendText(player, buildText {
                    error("Beim Löschen der Nachricht ist ein Fehler aufgetreten.")
                })
            }
        }
    }
}
