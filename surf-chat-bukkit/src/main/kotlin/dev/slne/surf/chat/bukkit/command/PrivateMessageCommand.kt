package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.service.BukkitMessagingSenderService
import dev.slne.surf.chat.bukkit.util.sendRawText
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.serverPlayers
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import dev.slne.surf.chat.core.service.replyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(StringArgument("player").replaceSuggestions(ArgumentSuggestions.stringCollection {
            serverPlayers.map { it.name }
        }))
        greedyStringArgument("message")

        withAliases("tell", "w", "pm", "dm")
        withPermission("surf.chat.command.private-message")
        playerExecutor { player, args ->
            plugin.launch {
                val target = args.getUnchecked<String>("player") ?: return@launch
                val message = args.getUnchecked<String>("message") ?: return@launch
                val messageComponent = Component.text(message)

                val targetPlayer: Player? = Bukkit.getPlayer(target)
                val user = databaseService.getUser(player.uniqueId)

                if(player.name == target) {
                    user.sendText(buildText {
                        error("Du kannst dir nicht selbst eine Nachricht senden.")
                    })
                    return@launch
                }

                if(targetPlayer == null) {
                    plugin.messageValidator.parse(messageComponent, ChatMessageType.PRIVATE_TO, player) {
                        user.sendRawText(plugin.chatFormat.formatMessage(messageComponent, player, player, ChatMessageType.PRIVATE_TO, "", UUID.randomUUID(), true))

                        plugin.launch {
                            surfChatApi.logMessage(player.uniqueId, ChatMessageType.PRIVATE_GENERAL, messageComponent, UUID.randomUUID())
                        }

                        messagingSenderService.sendData (
                            player.name,
                            target,
                            messageComponent,
                            ChatMessageType.PRIVATE_FROM,
                            UUID.randomUUID(),
                            "N/A",
                            BukkitMessagingSenderService.getForwardingServers()
                        )
                    }
                } else {
                    val user = databaseService.getUser(player.uniqueId)
                    val targetUser = databaseService.getUser(targetPlayer.uniqueId)

                    plugin.messageValidator.parse(messageComponent, ChatMessageType.PRIVATE_TO, player) {
                        targetUser.sendRawText(plugin.chatFormat.formatMessage(messageComponent, player, targetPlayer, ChatMessageType.PRIVATE_FROM, "", UUID.randomUUID(), true))
                        user.sendRawText(plugin.chatFormat.formatMessage(messageComponent, player, player, ChatMessageType.PRIVATE_TO, "", UUID.randomUUID(), true))

                        replyService.updateLast(player.uniqueId, targetUser.uuid)
                        replyService.updateLast(targetUser.uuid, player.uniqueId)

                        plugin.launch {
                            surfChatApi.logMessage(player.uniqueId, ChatMessageType.PRIVATE_GENERAL, messageComponent, UUID.randomUUID())
                        }
                    }
                }
            }
        }
    }
}
