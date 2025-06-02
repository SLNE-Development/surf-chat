package dev.slne.surf.chat.velocity.command

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.messageValidator

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.replyService
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.chat.velocity.command.argument.playerArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.model.velocityChatFormat
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.components
import dev.slne.surf.chat.velocity.util.getUsername
import dev.slne.surf.chat.velocity.util.sendRawText
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import java.util.*
import kotlin.jvm.optionals.getOrNull

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerArgument("player")
        greedyStringArgument("message")

        withAliases("tell", "w", "pm", "dm")
        withPermission(ChatPermissionRegistry.COMMAND_MSG)
        playerExecutor { player, args ->
            container.launch {
                val target = args.getUnchecked<Player>("player") ?: return@launch
                val message = args.getUnchecked<String>("message") ?: return@launch
                val messageComponent = Component.text(message)

                if(target.uniqueId == player.uniqueId) {
                    player.sendText {
                        appendPrefix()
                        error("Du kannst dir selbst keine Nachrichten senden.")
                    }
                    return@launch
                }

                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (targetUser.ignores(user.uuid)) {
                    targetUser.sendText {
                        error("Du ignorierst diesen Spieler.")
                    }
                    return@launch
                }

                messageValidator.parse(
                    messageComponent,
                    MessageType.PRIVATE_TO,
                    user
                ) {
                    container.launch {
                        if (!targetUser.ignores(user.uuid)) {
                            targetUser.sendRawText(
                                velocityChatFormat.formatMessage(
                                    messageComponent,
                                    user,
                                    targetUser,
                                    MessageType.PRIVATE_FROM,
                                    "",
                                    UUID.randomUUID(),
                                    true
                                )
                            )
                        }

                        user.sendRawText (
                            velocityChatFormat.formatMessage(
                                messageComponent,
                                user,
                                targetUser,
                                MessageType.PRIVATE_TO,
                                "",
                                UUID.randomUUID(),
                                true
                            )
                        )

                        replyService.updateLast(player.uniqueId, target.uniqueId)
                        replyService.updateLast(target.uniqueId, player.uniqueId)

                        if (spyService.hasPrivateMessageSpies(user)) {
                            spyService.getPrivateMessageSpys(user).forEach {
                                it.sendText {
                                    info("[${user.getUsername()} -> ${target.username}] ")
                                    append(messageComponent)

                                    hoverEvent(
                                        components.getMessageHoverComponent(
                                            user.getUsername(),
                                            System.currentTimeMillis(),
                                            "N/A"
                                        )
                                    )
                                    clickEvent(ClickEvent.suggestCommand("/msg ${user.getUsername()} "))
                                }
                            }
                        }

                        container.launch {
                            surfChatApi.logMessage(
                                player.uniqueId,
                                MessageType.PRIVATE,
                                messageComponent,
                                UUID.randomUUID(),
                                player.currentServer.getOrNull()?.serverInfo?.name ?: "Unknown"
                            )
                        }
                    }
                }
            }
        }
    }
}
