package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendRawText
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.serverPlayers
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.replyService
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.stringCollection {
            serverPlayers.map { it.name }
        }))
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

                if(targetUser.uuid == user.uuid) {
                    user.sendText(buildText {
                        error("Du kannst dir selbst keine Nachrichten senden.")
                    })
                    return@launch
                }

                plugin.messageValidator.parse(messageComponent, ChatMessageType.PRIVATE_TO, player) {
                    targetUser.sendRawText(plugin.chatFormat.formatMessage(messageComponent, player, target, ChatMessageType.PRIVATE_FROM, "", UUID.randomUUID(), true))
                    user.sendRawText(plugin.chatFormat.formatMessage(messageComponent, player, target, ChatMessageType.PRIVATE_TO, "", UUID.randomUUID(), true))

                    replyService.updateLast(player.uniqueId, target.uniqueId)
                    replyService.updateLast(target.uniqueId, player.uniqueId)

                    if(spyService.hasPrivateMessageSpys(player)) {
                        spyService.getPrivateMessageSpys(player).forEach { it.sendText {
                            spacer("[${player.name}] ")
                            append { messageComponent }
                        } }
                    }

                    plugin.launch {
                        surfChatApi.logMessage(player.uniqueId, ChatMessageType.PRIVATE, messageComponent, UUID.randomUUID())
                    }
                }
            }
        }
    }
}
