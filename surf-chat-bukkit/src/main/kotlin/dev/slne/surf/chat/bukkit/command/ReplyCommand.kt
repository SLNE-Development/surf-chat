package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendRawText
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.replyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.*

class ReplyCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_REPLY)
        withAliases("r")
        greedyStringArgument("message")
        playerExecutor { player, args ->
            plugin.launch {
                val message = args.getUnchecked<String>("message") ?: return@launch
                val uuid = replyService.getLast(player.uniqueId)
                val user = databaseService.getUser(player.uniqueId)

                if (uuid == null) {
                    user.sendText(buildText {
                        error("Der Spieler wurde nicht gefunden.")
                    })
                    return@launch
                }

                val targetUser = databaseService.getUser(uuid)
                val target = Bukkit.getPlayer(uuid)

                if (target == null) {
                    user.sendText(buildText {
                        error("Der Spieler wurde nicht gefunden.")
                    })
                    return@launch
                }

                if (target == player) {
                    user.sendText(buildText {
                        error("Du kannst dir nicht selbst schreiben.")
                    })
                    return@launch
                }

                val messageComponent = Component.text(message)

                plugin.messageValidator.parse(
                    messageComponent,
                    ChatMessageType.PRIVATE_TO,
                    player
                ) {
                    targetUser.sendRawText(
                        plugin.chatFormat.formatMessage(
                            messageComponent,
                            player,
                            target,
                            ChatMessageType.PRIVATE_FROM,
                            "",
                            UUID.randomUUID(),
                            true
                        )
                    )
                    user.sendRawText(
                        plugin.chatFormat.formatMessage(
                            messageComponent,
                            player,
                            target,
                            ChatMessageType.PRIVATE_TO,
                            "",
                            UUID.randomUUID(),
                            true
                        )
                    )

                    plugin.launch {
                        surfChatApi.logMessage(
                            player.uniqueId,
                            ChatMessageType.REPLY,
                            messageComponent,
                            UUID.randomUUID()
                        )
                    }
                }
            }
        }
    }
}