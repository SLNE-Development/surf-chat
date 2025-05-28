package dev.slne.surf.chat.velocity.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.util.*

class SurfChatDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)
        stringArgument("messageID")
        playerExecutor { player, args ->
            val messageID = args.getUnchecked<String>("messageID") ?: return@playerExecutor

            if (!historyService.deleteMessage(player.username, UUID.fromString(messageID))) {
                player.sendText {
                    appendPrefix()
                    error("Eine Nachricht mit der ID ")
                    variableValue(messageID)
                    error(" existiert nicht.")
                }
            }
        }
    }
}
