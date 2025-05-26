package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.service.BukkitMessagingSenderService
import dev.slne.surf.chat.bukkit.util.utils.toPlainText
import dev.slne.surf.chat.bukkit.util.utils.toPlayer
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.chat.core.service.util.HistoryEntry
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*
import java.util.regex.Pattern

class BukkitChatListener() : Listener {
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

        val signature = event.signedMessage().signature()

        if (signature != null) {
            historyService.logCaching(HistoryEntry(
                signature,
                message.toPlainText()
            ), messageID)
        } else {
            plugin.logger.warning("Message signature is null for player ${player.name} with message: $message")
        }

        val channel = channelService.getChannel(player)

        if (channel != null && !message.toPlainText().contains(pattern)) {
            event.viewers().clear()
            event.viewers().addAll(channel.getMembers().map { it.toPlayer() ?: return })

            if (spyService.hasChannelSpies(channel)) {
                event.viewers().addAll(spyService.getChannelSpys(channel))
            }

            event.renderer { _, _, _, viewer ->
                plugin.chatFormat.formatMessage(
                    message,
                    player,
                    viewer as? Player ?: player,
                    ChatMessageType.CHANNEL,
                    channel.name,
                    messageID,
                    true
                )
            }

            plugin.launch {
                surfChatApi.logMessage(player.uniqueId, ChatMessageType.CHANNEL, message, messageID)
            }

            return
        }

        plugin.launch {
            surfChatApi.logMessage(
                player.uniqueId,
                ChatMessageType.GLOBAL,
                cleanedMessage,
                messageID
            )
        }

        var formatted = false

        plugin.messageValidator.parse(cleanedMessage, ChatMessageType.GLOBAL, player) {
            messagingSenderService.sendData(
                player.name,
                player.name,
                formattedMessage,
                ChatMessageType.GLOBAL,
                messageID,
                "N/A",
                BukkitMessagingSenderService.getForwardingServers()
            )

            event.renderer { _, _, _, viewer ->
                plugin.chatFormat.formatMessage(
                    cleanedMessage,
                    player,
                    viewer as? Player ?: player,
                    ChatMessageType.GLOBAL,
                    "N/A",
                    messageID,
                    false
                )
            }

            formatted = true
        }

        if (!formatted) {
            event.isCancelled = true
        }
    }
}