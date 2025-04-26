package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.util.history.LoggedMessage
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.service.BukkitMessagingSenderService
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.serverPlayers
import dev.slne.surf.chat.bukkit.util.toPlainText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID
import java.util.regex.Pattern

class BukkitChatListener(): Listener {
    private val pattern = Regex("^@(all|a|here|everyone)\\b\\s*", RegexOption.IGNORE_CASE)

    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val player = event.player
        val message = event.message()
        val messageID = UUID.randomUUID()

        val formattedMessage = plugin.chatFormat.formatMessage(
            message,
            player,
            player,
            ChatMessageType.GLOBAL,
            "N/A",
            messageID,
            true
        )

        val cleanedMessage = message.replaceText(
            TextReplacementConfig.builder()
                .match(Pattern.compile("^@(all|a|here|everyone)\\b\\s*", Pattern.CASE_INSENSITIVE))
                .replacement(Component.empty())
                .build()
        )


        serverPlayers.forEach {
            historyService.logCaching(it.uniqueId, LoggedMessage(player.name, "Unknown", formattedMessage), messageID)
        }

        val channel = channelService.getChannel(player)

        if (channel != null && !message.toPlainText().contains(pattern)) {
            channel.getMembers().forEach {
                it.sendText(
                    plugin.chatFormat.formatMessage(
                        message,
                        player,
                        player,
                        ChatMessageType.CHANNEL,
                        channel.name,
                        messageID,
                        true
                    )
                )
            }

            plugin.launch {
                surfChatApi.logMessage(player.uniqueId, ChatMessageType.CHANNEL, message, messageID)
            }

            event.isCancelled = true

            return
        }

        plugin.launch {
            surfChatApi.logMessage(player.uniqueId, ChatMessageType.GLOBAL, cleanedMessage, messageID)
        }

        var formatted = false

        plugin.messageValidator.parse(cleanedMessage, ChatMessageType.GLOBAL, player) {
            event.renderer { _, _, _, viewer ->
                messagingSenderService.sendData(player.name,
                    if(viewer is Player) viewer.name else "Unknown",
                    cleanedMessage,
                    ChatMessageType.GLOBAL,
                    messageID,
                    "N/A",
                    BukkitMessagingSenderService.getForwardingServers()
                )

                plugin.chatFormat.formatMessage (
                    cleanedMessage,
                    player,
                    if(viewer is Player) viewer else player,
                    ChatMessageType.GLOBAL,
                    "N/A",
                    messageID,
                    false
                )
            }

            formatted = true
        }

        if(!formatted) {
            event.isCancelled = true
        }
    }
}