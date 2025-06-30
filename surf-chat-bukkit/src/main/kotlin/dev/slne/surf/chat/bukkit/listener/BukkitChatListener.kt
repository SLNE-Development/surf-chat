package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.model.messageValidator
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.plain
import dev.slne.surf.chat.bukkit.util.toChatUser
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.spyService
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.Bukkit
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

        plugin.launch {
            val senderUser = player.toChatUser()

            val formattedMessage = plugin.chatFormat.formatMessage(
                message,
                senderUser,
                senderUser,
                MessageType.GLOBAL,
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
                historyService.logCaching(signature, messageID)
            } else {
                plugin.logger.warning("Message signature is null for player ${player.name} with message: $message")
            }

            val channel = channelService.getChannel(senderUser)

            if (channel != null && !message.plain().contains(pattern)) {
                event.viewers().clear()
                event.viewers().addAll(channel.members.mapNotNull { Bukkit.getPlayer(it.uuid) })

                if (spyService.hasChannelSpies(channel)) {
                    //TODO: IMPLEMENT SPYS
                }

                event.renderer { _, _, _, _ ->
                    plugin.chatFormat.formatMessage(
                        message,
                        senderUser,
                        senderUser,
                        MessageType.CHANNEL,
                        channel.name,
                        messageID,
                        true
                    )
                }

                plugin.launch {
                    surfChatApi.logMessage(player.uniqueId, MessageType.CHANNEL, message, messageID, "N/A")//TODO: SERVER RETRIEVAL
                }

                return@launch
            }

            surfChatApi.logMessage(
                player.uniqueId,
                MessageType.GLOBAL,
                cleanedMessage,
                messageID,
                "N/A"//TODO: SERVER RETRIEVAL
            )

            var formatted = false


            messageValidator.parse(cleanedMessage, MessageType.GLOBAL, senderUser) {
                event.renderer { _, _, _, _ ->
                    plugin.chatFormat.formatMessage(
                        cleanedMessage,
                        senderUser,
                        senderUser,
                        MessageType.GLOBAL,
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
}