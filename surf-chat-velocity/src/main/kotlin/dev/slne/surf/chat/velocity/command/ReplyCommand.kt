package dev.slne.surf.chat.velocity.command

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.messageValidator
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.replyService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.model.velocityChatFormat
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendRawText
import dev.slne.surf.chat.velocity.util.sendText
import net.kyori.adventure.text.Component
import java.util.*
import kotlin.jvm.optionals.getOrNull

class ReplyCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_REPLY)
        withAliases("r")
        greedyStringArgument("message")
        playerExecutor { player, args ->
            container.launch {
                val message = args.getUnchecked<String>("message") ?: return@launch
                val uuid = replyService.getLast(player.uniqueId)
                val user = databaseService.getUser(player.uniqueId)

                if (uuid == null) {
                    user.sendText {
                        error("Du hast keinem Spieler zuletzt geschrieben.")
                    }
                    return@launch
                }

                if (uuid == player.uniqueId) {
                    user.sendText {
                        error("Du kannst dir nicht selbst schreiben.")
                    }
                    return@launch
                }

                val targetUser = databaseService.getUser(uuid)
                val messageComponent = Component.text(message)

                messageValidator.parse(
                    messageComponent,
                    MessageType.PRIVATE_TO,
                    user
                ) {
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

                    container.launch {
                        surfChatApi.logMessage(
                            player.uniqueId,
                            MessageType.REPLY,
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