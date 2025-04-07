package dev.slne.surf.chat.bukkit.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import java.util.*

class SurfChatDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat.delete")
        stringArgument("messageID")
        playerExecutor { player, args ->
            val messageID = args.getUnchecked<String>("messageID") ?: return@playerExecutor

            historyService.deleteMessage(player.uniqueId, UUID.fromString(messageID))
            historyService.resendMessages(player.uniqueId)

            surfChatApi.sendText(player, buildText {
                primary("Die Nachricht mit der ID ")
                info(messageID)
                primary(" wurde gelöscht.")
            })
        }
    }
}
