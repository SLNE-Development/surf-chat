package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.jorel.commandapi.kotlindsl.uuidArgument
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import java.util.*

class SurfChatDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)
        uuidArgument("messageID")
        playerExecutor { player, args ->
            val messageID = args.getUnchecked<UUID>("messageID") ?: return@playerExecutor

            if (!historyService.deleteMessage(player.name, messageID)) {
                player.sendPrefixed {
                    error("Eine Nachricht mit der ID ")
                    variableValue(messageID.toString())
                    error(" existiert nicht.")
                }
            }
        }
    }
}
