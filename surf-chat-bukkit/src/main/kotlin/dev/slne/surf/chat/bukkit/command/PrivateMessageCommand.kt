package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendRawText
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.toDisplayUser
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.replyService

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerArgument("player")
        greedyStringArgument("message")

        withAliases("tell", "w", "pm", "dm")
        withPermission("surf.chat.command.private-message")
        playerExecutor { player, args ->
            plugin.launch {
                val target = args.getUnchecked<Player>("player") ?: return@launch
                val message = args.getUnchecked<String>("message") ?: return@launch
                val messageComponent = Component.text(message)

                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                plugin.messageValidator.parse(messageComponent, ChatMessageType.PRIVATE_TO, user) {
                    targetUser.sendRawText(plugin.chatFormat.formatMessage(messageComponent, player.toDisplayUser(), target.toDisplayUser(), ChatMessageType.PRIVATE_FROM, ""))
                    user.sendRawText(plugin.chatFormat.formatMessage(messageComponent, player.toDisplayUser(), target.toDisplayUser(), ChatMessageType.PRIVATE_TO, ""))

                    replyService.updateLast(player.uniqueId, target.uniqueId)
                    replyService.updateLast(target.uniqueId, player.uniqueId)

                    plugin.launch {
                        surfChatApi.logMessage(player.uniqueId, ChatMessageType.PRIVATE_GENERAL, messageComponent)
                    }
                }
            }
        }
    }
}
