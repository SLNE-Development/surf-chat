package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.utils.sendRawText
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.replyService
import dev.slne.surf.chat.core.service.spyService

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(EntitySelectorArgument.OnePlayer("player").replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                val players = Bukkit.getOnlinePlayers().map { it.name }
                players.toSet()
            }))
        greedyStringArgument("message")

        withAliases("tell", "w", "pm", "dm")
        withPermission(ChatPermissionRegistry.COMMAND_MSG)
        playerExecutor { player, args ->
            plugin.launch {
                val target = args.getUnchecked<Player>("player") ?: return@launch
                val message = args.getUnchecked<String>("message") ?: return@launch
                val messageComponent = Component.text(message)

                if(target.uniqueId == player.uniqueId) {
                    player.sendPrefixed {
                        error("Du kannst dir selbst keine Nachrichten senden.")
                    }
                    return@launch
                }

                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (targetUser.isIgnoring(user.uuid)) {
                    targetUser.sendPrefixed {
                        error("Du ignorierst diesen Spieler.")
                    }
                    return@launch
                }

                plugin.messageValidator.parse(
                    messageComponent,
                    MessageType.PRIVATE_TO,
                    player
                ) {
                    if (!targetUser.isIgnoring(user.uuid)) {
                        targetUser.sendRawText(
                            plugin.chatFormat.formatMessage(
                                messageComponent,
                                player,
                                target,
                                MessageType.PRIVATE_FROM,
                                "",
                                UUID.randomUUID(),
                                true
                            )
                        )
                    }

                    user.sendRawText(
                        plugin.chatFormat.formatMessage(
                            messageComponent,
                            player,
                            target,
                            MessageType.PRIVATE_TO,
                            "",
                            UUID.randomUUID(),
                            true
                        )
                    )

                    replyService.updateLast(player.uniqueId, target.uniqueId)
                    replyService.updateLast(target.uniqueId, player.uniqueId)

                    if (spyService.hasPrivateMessageSpies(player)) {
                        spyService.getPrivateMessageSpys(player).forEach {
                            it.sendPrefixed {
                                info("[${player.name} -> ${target.name}] ")
                                append(messageComponent)

                                hoverEvent(
                                    components.getMessageHoverComponent(
                                        player.name,
                                        System.currentTimeMillis(),
                                        "N/A"
                                    )
                                )
                                clickEvent(ClickEvent.suggestCommand("/msg ${player.name} "))
                            }
                        }
                    }

                    plugin.launch {
                        surfChatApi.logMessage(
                            player.uniqueId,
                            MessageType.PRIVATE,
                            messageComponent,
                            UUID.randomUUID()
                        )
                    }
                }
            }
        }
    }
}
